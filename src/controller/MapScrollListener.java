package controller;

import model.MapModel;
import view.MapStratFrame;

import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

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
            mapModel.initKernel(newKernelX, newKernelY, larger);
        }else {
            int delta = oldKernelSize - smaller;
            int newKernelX = mapModel.getKernelX() + ((int) (percentX * delta));
            int newKernelY = mapModel.getKernelY() + ((int) (percentY * delta));
            mapModel.initKernel(newKernelX, newKernelY, smaller);
        }
    }
}
