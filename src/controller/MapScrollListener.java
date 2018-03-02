package controller;

import model.MapModel;

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
        int smaller = (int) (oldKernelSize * 1.5);
        int larger =  (int) (oldKernelSize / 1.5);
        if(e.getWheelRotation() < 0){
//            int delta = oldKernelSize - larger;
//            mapModel.updateKernelX(mapModel.getKernelX() + (delta / 2) );
//            mapModel.updateKernelY(mapModel.getKernelY() + (delta / 2) );


            mapModel.updateKernelSize(larger);
        }else{

            mapModel.updateKernelSize(smaller);
        }
    }
}
