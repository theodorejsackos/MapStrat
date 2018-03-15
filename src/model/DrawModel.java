package model;

import newnet.Host;

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
public class DrawModel extends Observable{

    public static final int BRUSH_SIZE_MINIMUM = 1, BRUSH_SIZE_MAXIMUM = 10;

    private List<DrawableObject> drawnObjects = null;

    private int        selectedSize;
    private Color      selectedColor;
    private LineStroke currentStroke; // The stroke that is currently being drawn (null if not in use)

    private boolean connected           = false;
    private ServerHandler serverHandler = null;

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
        currentStroke = null;
    }


    public void connect(String host, int port, String gid){
        try {
            Host drawServer = new Host(host, port);
            drawServer.ois = new ObjectInputStream(drawServer.getInputStream());
            drawServer.oos = new ObjectOutputStream(drawServer.getOutputStream());
            drawServer.id = gid;

            connected = true;
            serverHandler = new ServerHandler(drawServer);
            serverHandler.start();

        }catch(IOException e){
            System.err.println("Failed to establish a connection to " + host + ":" + port);
        }
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
        private volatile boolean running;

        public ServerHandler(Host server) {
            this.server  = server;
            this.running = true;
        }

        @Override
        public void run(){
            while(running){

            }
        }

        public void stopRunning(){
            running = false;
        }
    }
}
