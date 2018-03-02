package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class DrawModel extends Observable{

    public static final int SMALL = 3, MEDIUM = 7, LARGE = 15;

    private List<DrawableObject> drawnObjects = null;
    private Color selectedColor;
    private int size;

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
        setChanged();
        notifyObservers();
    }

    public int getSize(){
        return this.size;
    }
}
