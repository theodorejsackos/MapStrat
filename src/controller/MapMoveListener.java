package controller;

import model.DrawModel;
import model.MapModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/** The MapMoveListener responds to map panning events. When the user right clicks on the map,
 * the kernel's size is smaller than the maximum size, and moves the mouse, the map will pan
 * according to the mouse movement. Dragging in the +x, +y direction will move the kernel
 * in the -x, -y direction. This creates the effect of sliding the map in the direction of the
 * mouse under a fixed view window. This listener is the control element updating the MapModel.
 *
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 * @see model.MapModel
 */
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
