package controller;

import model.DrawModel;
import model.MapModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MapMoveListener implements MouseListener, MouseMotionListener {
    private MapModel mapModel;
    private DrawModel drawModel;
    private Component canvas;
    private int prevX, prevY;
    private boolean dragActive = false;
    private Timer draw;

    public MapMoveListener(MapModel mapModel, DrawModel dm, Component canvas){
        this.drawModel = dm;
        this.mapModel = mapModel;
        this.canvas = canvas;
    }

    /* Responds to the event of the user pushing a mouse button down */
    public void mousePressed(MouseEvent e) {
        /* When the right mouse is pressed down, record the position of the click
         * and begin scrolling the map around as the mouse is moved (handled by
         * mouseDragged() */
        if(e.getButton() == MouseEvent.BUTTON3) {
            prevX = e.getX();
            prevY = e.getY();
            dragActive = true;
        }

        if(e.getButton() == MouseEvent.BUTTON1){
            drawModel.startStroke(mapModel.getKernelX() + e.getX(), mapModel.getKernelY() + e.getY());
            draw = new Timer(25, (ActionEvent evt) -> {
                Point p = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(p, canvas);
                drawModel.addStroke(mapModel.getKernelX() + p.x, mapModel.getKernelY() + p.y);
            });
            draw.start();
        }
    }

    /* Responds to the event of the user releasing a mouse button from the down position */
    public void mouseReleased(MouseEvent e) {
        /* When the right mouse button is released, stop the scrolling. */
        if(e.getButton() == MouseEvent.BUTTON3) {
            dragActive = false;
        }

        if(e.getButton() == MouseEvent.BUTTON1){
            drawModel.finalizeStroke(mapModel.getKernelX() + e.getX(), mapModel.getKernelY() + e.getY());
            draw.stop();
        }
    }

    /* If a mouse button is down and the mouse moves, respond to the event */
    public void mouseDragged(MouseEvent e) {
        /* If the user is actively dragging the mouse around while the right mouse
         * button is being pressed, appropriately slide the map around in accordance
         * with the user's mouse movement. */
        if(dragActive){
            int deltaX = prevX - e.getX();
            int deltaY = prevY - e.getY();

            mapModel.updateKernelX(mapModel.getKernelX() + deltaX * 2);
            mapModel.updateKernelY(mapModel.getKernelY() + deltaY * 2);

            prevX = e.getX();
            prevY = e.getY();
        }
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}
