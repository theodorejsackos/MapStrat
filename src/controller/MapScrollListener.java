package controller;

import model.MapModel;
import view.MapStratFrame;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


/** The MapScrollListener responds to map zooming events. When the user scrolls up on the mouse
 * wheel the kernel's size is decreased and the position of the kernel is adjusted relative to the
 * current position of the user's mouse (the smaller kernel attempts to center itself on the user's
 * mouse). When the mouse wheel is scrolled down, the kernel's size is increased. It zooms out equally
 * in all directions regardless of the mouses position. These decisions were made to emulate the behavior
 * of the in-game map of PUBG.
 *
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 * @see model.MapModel
 */
public class MapScrollListener implements MouseWheelListener{

    private MapModel mapModel;

    public MapScrollListener(MapModel mapModel){
        this.mapModel = mapModel;
    }

    /* Scales the size of the map kernel in response to the user scrolling the mouse wheel.
     * MouseWheelDown events zoom out on the map, whereas MouseWheelUp events zoom in. There
     * is a maximum granularity in both small and large sizes that the model manages. Otherwise
     * the scaling is exponential. */
    public void mouseWheelMoved(MouseWheelEvent e) {
        int mouseX = e.getX(), mouseY = e.getY();
        int oldKernelSize = mapModel.getKernelSize();
        int larger  = (int) (oldKernelSize * 1.5);
        int smaller = (int) (oldKernelSize / 1.5);

        /* Get the position of the mouse in the drawing canvas in euclidian coordinate space
         * normalized to unit length. */
        MapStratFrame msf = (MapStratFrame) e.getComponent();
        double percentX = Math.min(1.0, (1.0 * mouseX / msf.getDrawingCanvas().getWidth()));
        double percentY = Math.min(1.0, (1.0 * mouseY / msf.getDrawingCanvas().getHeight()));

        if(e.getWheelRotation() > 0) {
            int delta = oldKernelSize - larger;
            int newKernelX = mapModel.getKernelX() - Math.abs(delta) / 2;
            int newKernelY = mapModel.getKernelY() - Math.abs(delta) / 2;
            mapModel.updateKernel(newKernelX, newKernelY, larger);
        }else {
            int delta = oldKernelSize - smaller;
            int newKernelX = mapModel.getKernelX() + ((int) (percentX * delta));
            int newKernelY = mapModel.getKernelY() + ((int) (percentY * delta));
            mapModel.updateKernel(newKernelX, newKernelY, smaller);
        }
    }
}
