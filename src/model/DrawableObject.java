package model;

import java.awt.*;
import java.io.Serializable;

public abstract class DrawableObject implements Serializable {
    private static int ID_COUNTER = 0;

    private final int drawObjectId;

    DrawableObject(){
        this.drawObjectId = ID_COUNTER++;
    }

    public abstract void renderRelativeToKernel(Graphics g, int kx, int ky, int ksize, int csize);

    public final int getDrawObjectId(){
        return drawObjectId;
    }

    public final boolean is(int id){
        return this.drawObjectId == id;
    }
}
