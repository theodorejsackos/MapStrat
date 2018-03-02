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

    public void mouseReleased(MouseEvent e) {
        /* When the right mouse button is released, stop the scrolling. */
        if(e.getButton() == MouseEvent.BUTTON3) {
            dragActive = false;
        }
    }

    public void mouseDragged(MouseEvent e) {
        if(dragActive){
            int deltaX = prevX - e.getX();
            int deltaY = prevY - e.getY();

            mapModel.updateKernelX(mapModel.getKernelX() + deltaX);
            mapModel.updateKernelY(mapModel.getKernelY() + deltaY);

            prevX = e.getX();
            prevY = e.getY();
        }
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}
