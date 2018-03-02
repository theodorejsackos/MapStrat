package controller;

import model.MapModel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MapMoveListener implements MouseListener, MouseMotionListener {
    private MapModel mapModel;
    private int prevX, prevY;
    private boolean dragActive = false;

    public MapMoveListener(MapModel mapModel){
        this.mapModel = mapModel;
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
    }

    /* Responds to the event of the user releasing a mouse button from the down position */
    public void mouseReleased(MouseEvent e) {
        /* When the right mouse button is released, stop the scrolling. */
        if(e.getButton() == MouseEvent.BUTTON3) {
            dragActive = false;
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
