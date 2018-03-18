package model;

import newnet.Host;
import newnet.Message;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
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


    public void connect(int port, String gid){
        final String host = "mapgee.us";
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
        /* Disconnect with a separate thread, join() is blocking and shouldn't be done on the caller's
         * thread, this may hang the GUI indeterminately. */
        new Thread(() -> {
            /* Send leave message to server, eventually the server should echo the leave back to this handler */
            serverHandler.leave();

            /* Wait for the echo to come back and for the serverHandler thread to die */
            try {
                serverHandler.join();
            }catch(InterruptedException e){
                System.err.println("Interrupted while waiting for ServerHandler to join");
            }

            serverHandler = null;
            setChanged();
            notifyObservers();
        }).start();
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

            acceptLoop:
            while(connected){
                Message m = null;
                try {
                    m = Message.get(server);
                }catch(SocketException e){/* If caught, then m will remain null and triggers the following check */}

                if(m == null) {
                    System.err.println("Garbage message received from " + server + " severing connection.");
                    numPeers     = 0;
                    connected    = false;
                    drawnObjects = new ArrayList<>();
                    setChanged();
                    notifyObservers();
                    continue;
                }

                switch(m.type){
                    case STATUS:
                        /* This is either the first response from the server or a generic 'we are still connected' message */
                        connected = true;
                        numPeers = m.getNumPeers();
                        break;
                    case UPDATE:
                        drawnObjects.add(m.getStroke());
                        setChanged();
                        notifyObservers();
                        break;
                    case REFRESH:
                        drawnObjects = m.getState();
                        setChanged();
                        notifyObservers();
                        break;

                    case JOIN_GROUP:
                        System.err.println("JOIN_GROUP message should not be sent to the client, outgoing only");
                        break;
                    case LEAVE_GROUP:
                        /* Reset all state */
                        numPeers     = 0;
                        connected    = false;
                        drawnObjects = new ArrayList<>(50);
                        System.out.println("Left group, handler thread dying");
                        break acceptLoop;
                }
            }

            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void updateServer(DrawableObject o){
            Message.update(gid, o).send(server);
        }

        public void leave(){
            Message.leave(gid).send(server);
        }
    }
}
