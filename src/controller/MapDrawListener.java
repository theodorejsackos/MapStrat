package controller;

import model.DrawModel;
import model.MapModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/** The MapDrawListener responds to map drawing events. Drawing events include 'stamping' (drawing a 
 * fixed image in a particular position) and 'drawing' (line drawing along the path of the cursor).
 * Both such events are intitiated by left clicks on the drawing canvas, and the event result is
 * determined by the state of the DrawModel (specifying the drawing configuration currently active).
 * 
 * TODO: 3/4/2018 Stamping is not implemented. See issue #7.
 *
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 * @see model.MapModel
 * @see model.DrawModel
 * @see view.ControlPanel
 */
public class MapDrawListener implements MouseListener {
    private static final int MOUSE_POSITION_POLLING_RATE = 25;

    private MapModel mapModel;
    private DrawModel drawModel;
    private Component canvas;
    private Timer draw;

    public MapDrawListener(MapModel mapModel, DrawModel dm, Component canvas){
        this.drawModel = dm;
        this.mapModel = mapModel;
        this.canvas = canvas;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        /* When the user begins clicking to draw spawn another thread to poll the mouse position
         * for the line's path - mouseDragged can be unreliable depending on what work is being
         * done on the main thread */
        if(e.getButton() == MouseEvent.BUTTON1){
            drawModel.startStroke(mapModel.getKernelX() + e.getX(), mapModel.getKernelY() + e.getY());

            /* For every MOUSE_POSITION_POLLING_RATE millisections, poll the position of the mouse on
             * the screen, convert it to a position relative to the canvas, and add it to the points that
             * form the line being drawn. */
            draw = new Timer(MOUSE_POSITION_POLLING_RATE, (ActionEvent evt) -> {
                Point p = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(p, canvas);
                drawModel.addStroke(mapModel.getKernelX() + p.x, mapModel.getKernelY() + p.y);
            });
            /* Start the polling thread */
            draw.start();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        /* When the user releases the mouse button to end the drawn line, finalize the drawing object (it will
         * no longer be changed) and stop the mouse polling thread */
        if(e.getButton() == MouseEvent.BUTTON1){
            drawModel.finalizeStroke(mapModel.getKernelX() + e.getX(), mapModel.getKernelY() + e.getY());
            draw.stop();
        }
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e)  {}
}
