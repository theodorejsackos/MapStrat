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
        if(e.getWheelRotation() < 0){
            mapModel.updateKernelSize( (int) (mapModel.getKernelSize() / 1.5) );
        }else{
            mapModel.updateKernelSize( (int) (mapModel.getKernelSize() * 1.5) );
        }
    }
}
