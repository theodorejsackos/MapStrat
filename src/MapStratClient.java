import controller.MapMoveListener;
import controller.MapScrollListener;
import model.DrawModel;
import model.MapModel;
import view.MapStratFrame;

import javax.swing.*;

public class MapStratClient {
    public static void main(String[] args){
        MapModel  mapModel  = new MapModel(MapModel.MAP_DEFAULT);
        DrawModel drawModel = new DrawModel();

        JFrame window = new MapStratFrame(mapModel, drawModel);
        window.setVisible(true);

        window.addMouseWheelListener(new MapScrollListener(mapModel));
        MapMoveListener mapControl = new MapMoveListener(mapModel);
        window.addMouseListener(mapControl);
        window.addMouseMotionListener(mapControl);
    }
}
