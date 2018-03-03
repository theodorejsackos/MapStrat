package controller;

import model.DrawModel;

import java.awt.*;

public class MouseDragTracker extends Thread {

    private Component canvas;
    private DrawModel drawModel;
    public MouseDragTracker(Component c, DrawModel dm){
        this.canvas = c;
        this.drawModel = dm;
    }


}
