import controller.MapMoveListener;
import controller.MapScrollListener;
import model.DrawModel;
import model.MapModel;
import view.MapStratFrame;

import javax.swing.*;
import java.awt.*;

class TimedEventQueue extends EventQueue {
    @Override
    protected void dispatchEvent(AWTEvent event) {
        long startNano = System.nanoTime();
        super.dispatchEvent(event);
        long endNano = System.nanoTime();

        if (endNano - startNano > 50000000)
            System.out.println(((endNano - startNano) / 1000000)+"ms: "+event);
    }
}

public class MapStratClient {
    public static void main(String[] args){
        //Toolkit.getDefaultToolkit().getSystemEventQueue().push(new TimedEventQueue());

        MapModel  mapModel  = new MapModel(MapModel.MAP_DEFAULT);
        DrawModel drawModel = new DrawModel();

        MapStratFrame window = new MapStratFrame(mapModel, drawModel);
        window.setVisible(true);

        window.addMouseWheelListener(new MapScrollListener(mapModel));
        MapMoveListener mapControl = new MapMoveListener(mapModel, drawModel, window.getDrawingCanvas());
        window.addMouseListener(mapControl);
        window.addMouseMotionListener(mapControl);
    }
}
