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

    public static final int BRUSH_SIZE_MINIMUM = 1, BRUSH_SIZE_MAXIMUM = 10;

    private List<DrawableObject> drawnObjects = null;
    private Color selectedColor;
    private int selectedSize;
    private LineStroke currentStroke;

    public DrawModel(){
        drawnObjects = new ArrayList<>(50);
        selectedColor = new Color(255, 0, 0);
        selectedSize = 1;
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
        this.selectedSize = size;
        setChanged();
        notifyObservers();
    }

    public int getSize(){
        return this.selectedSize;
    }

    public void startStroke(){
        currentStroke = new LineStroke(selectedColor, selectedSize);
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
