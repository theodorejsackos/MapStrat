package model;

import java.awt.*;
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

    public static final int SMALL = 3, MEDIUM = 7, LARGE = 15;

    private List<DrawableObject> drawnObjects = null;
    private Color selectedColor;
    private int size;
    private LineStroke currentStroke;

    public DrawModel(){
        drawnObjects = new ArrayList<>(50);
        selectedColor = new Color(255, 0, 0);
        size = MEDIUM;
    }

    public List<DrawableObject> getAll(){
        return drawnObjects;
    }

    public void setColor(Color c){
        this.selectedColor = c;
        setChanged();
        notifyObservers();
    }

    public Color getColor(){
        return selectedColor;
    }

    public void setSize(int size){
        this.size = size;
        setChanged();
        notifyObservers();
    }

    public int getSize(){
        return this.size;
    }

    public void startStroke(int x, int y){
        currentStroke = new LineStroke(x, y, this.size, this.selectedColor);
        drawnObjects.add(currentStroke);
    }

    public void addStroke(int x, int y){
        currentStroke.addStroke(x, y);
        setChanged();
        notifyObservers();
    }

    public void finalizeStroke(){
        currentStroke = null;
    }
}
