package controller;

import model.MapModel;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MapScrollListener implements MouseWheelListener{

    private MapModel mapModel;

    public MapScrollListener(MapModel mapModel){
        this.mapModel = mapModel;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.getWheelRotation() < 0){
            mapModel.updateKernelSize( (int) (mapModel.getKernelSize() / 1.5) );
        }else{
            mapModel.updateKernelSize( (int) (mapModel.getKernelSize()  * 1.5) );
        }
    }
}
