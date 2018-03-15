package model;

import newnet.Host;
import newnet.Message;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/** The DrawModel represents the state of the current drawing configuration
 * (stamp vs. stroke, size, color, etc), as well as all objects that need to
 * be rendered on the drawing canvas.
 *
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 * @see view.DrawingCanvas
 */
public class DrawModel extends Observable {

    public static final int BRUSH_SIZE_MINIMUM = 1, BRUSH_SIZE_MAXIMUM = 10;

    private List<DrawableObject> drawnObjects = null;

    private int        selectedSize;
    private Color      selectedColor;
    private LineStroke currentStroke; // The stroke that is currently being drawn (null if not in use)

    private volatile boolean connected  = false;
    private ServerHandler serverHandler = null;
    private int           numPeers      = 0;

    /* Default initialize a larger than normal list of drawable objects, the color to red, and the
     * brush size to 1px */
    public DrawModel(){
        drawnObjects = new ArrayList<>(50);
        selectedColor = new Color(255, 0, 0);
        selectedSize = 1;
    }

    /* Returns the current state of drawable objects */
    public List<DrawableObject> getAll(){
        return drawnObjects;
    }

    /* Changes the current drawing color, notifies observers of the state change */
    public void setColor(Color c){
        this.selectedColor = c;
        setChanged();
        notifyObservers();
    }

    /* Returns the current state of the color to draw with */
    public Color getColor(){
        return selectedColor;
    }

    /* Changes the current drawing brush size, notifies observers of the state change */
    public void setSize(int size){
        this.selectedSize = size;
        setChanged();
        notifyObservers();
    }

    /* Returns the current state of the size of the brush */
    public int getSize(){
        return this.selectedSize;
    }

    /* Begin drawing a stroke by creating a new LineStroke object, add that reference exactly once to
     * the list of drawable objects. */
    public void startStroke(){
        currentStroke = new LineStroke(selectedColor, selectedSize);
        drawnObjects.add(currentStroke);
    }

    /* Add a new point to the LineStroke that is currently being constructed, notifies observers of the state change */
    public void addStroke(int x, int y){
        currentStroke.addStroke(x, y);
        setChanged();
        notifyObservers();
    }

    /* When the stroke has been completed, return the intance stroke to null (the stroke's reference will live on
     * in the list of drawable objects, but will not be changed again */
    public void finalizeStroke(){
        if(connected)
            serverHandler.updateServer(currentStroke);
        currentStroke = null;
    }


    public void connect(String host, int port, String gid){

        /* Do connect to server asynchronously to not screw with the GUI interactiveness */
        new Thread(new Runnable(){
            @Override
            public void run() {
                Host drawServer = null;
                try {
                    drawServer = new Host(host, port, gid);
                }catch(IOException e){
                    System.err.println("Failed to establish a connection to " + host + ":" + port);
                    e.printStackTrace();
                }

                System.err.printf("Joining '%s':'%d' [%s]\n", host, port, gid);

                /* Request to join the specified group */
                Message.join(gid).send(drawServer);
                connected = true;

                serverHandler = new ServerHandler(drawServer, gid);
                serverHandler.start();
            }
        }).start();
    }

    public void disconnect(){
        serverHandler.stopRunning();
        try {
            serverHandler.join();
        }catch(InterruptedException e){
            System.err.println("Interrupted while waiting for ServerHandler to join");
        }
    }

    private class ServerHandler extends Thread{
        private Host server;
        private String gid;

        public ServerHandler(Host server, String gid) {
            this.server  = server;
            this.gid = gid;
        }

        @Override
        public void run(){
            System.err.println("Awaiting messages from server in ServerHandler");
            while(connected){
                Message m = Message.get(server);
                if(m == null) {
                    System.err.println("Garbage message received from " + server + " severing connection.");
                    connected = false;
                    drawnObjects = new ArrayList<>();
                    setChanged();
                    notifyObservers();
                    continue;
                }

                System.err.printf("[Group %s]: Server handler received ", gid);
                switch(m.type){
                    case JOIN_GROUP:
                        System.err.println("JOIN_GROUP message");
                        break;
                    case LEAVE_GROUP:
                        System.err.println("LEAVE_GROUP message");
                        break;
                    case STATUS:
                        System.err.println("STATUS message");
                        /* This is either the first response from the server or a generic 'we are still connected' message */
                        connected = true;
                        numPeers = m.getNumPeers();
                        break;
                    case UPDATE:
                        System.err.println("UPDATE message");
                        break;
                    case REFRESH:
                        System.err.println("REFRESH message");
                        drawnObjects = m.getState();
                        System.out.println(m.getState());
                        setChanged();
                        notifyObservers();
                        break;
                }
            }
        }

        public void stopRunning(){
            connected = false;
        }

        public void updateServer(DrawableObject o){
            Message.update(gid, o).send(server);
        }

        public void refresh(){
            Message.refresh(gid, null).send(server);
        }
    }
}
