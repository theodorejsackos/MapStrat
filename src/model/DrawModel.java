package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class DrawModel extends Observable{

    public static final int SMALL = 3, MEDIUM = 7, LARGE = 15;

    private List<DrawableObject> drawnObjects = null;
    private Color selectedColor;
    private int size;
    private boolean drawing = false, stamping = false;
    private LineStroke currentStroke;

    public DrawModel(){
        drawnObjects = new ArrayList<>(50);
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
        startDrawing();
        setChanged();
        notifyObservers();
    }

    public int getSize(){
        return this.size;
    }

    public void startDrawing(){
        drawing  = true;
        stamping = false;
    }

    public boolean isDrawing(){
        return drawing;
    }

    public void startStamping(){
        drawing  = false;
        stamping = true;
    }

    public boolean isStamping(){
        return stamping;
    }

    public void startStroke(int x, int y){
        drawing = true;
        stamping = false;
        currentStroke = new LineStroke(x, y, this.size, this.selectedColor);
        currentStroke.addStroke(x, y);
        drawnObjects.add(currentStroke);
        setChanged();
        notifyObservers();
    }

    public void addStroke(int x, int y){
        currentStroke.addStroke(x, y);
        setChanged();
        notifyObservers();
    }

    public void finalizeStroke(int x, int y){
        currentStroke.addStroke(x, y);
        currentStroke = null;
        drawing = false;
        setChanged();
        notifyObservers();
    }
}
