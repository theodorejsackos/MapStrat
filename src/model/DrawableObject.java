package model;

import java.awt.*;

public abstract class DrawableObject {
    protected int x, y;
    protected int width, height, size;

    protected DrawableObject(){

    }

    protected DrawableObject(int x, int y){
        this.x = x;
        this.y = y;
    }

    protected DrawableObject(int x, int y, int size){
        this.x = x;
        this.y = y;
        this.size = size;
    }

    protected DrawableObject(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width  = width;
        this.height = height;
    }

    public abstract void render(Graphics g);

    public final int getX() {
        return x;
    }
    public final int getY() {
        return y;
    }
    public final int getWidth() {
        return width;
    }
    public final int getHeight() {
        return height;
    }
    public final void setX(int x) {
        this.x = x;
    }
    public final void setY(int y) {
        this.y = y;
    }
    public final void setWidth(int width) {
        this.width = width;
    }
    public final void setHeight(int height) {
        this.height = height;
    }
    public final int getSize(){return size;}
    public final void setSize(int size){
        this.size = size;
    }
}
