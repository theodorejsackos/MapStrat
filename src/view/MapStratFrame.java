package view;

import model.DrawModel;
import model.MapModel;

import javax.swing.*;
import java.awt.*;

public class MapStratFrame extends JFrame {

    private MapModel  mapModel;
    private DrawModel drawModel;

    private DrawingCanvas canvas;

    public MapStratFrame(MapModel map, DrawModel draw){
        this.mapModel  = map;
        this.drawModel = draw;

        canvas = new DrawingCanvas(map);
        mapModel.addObserver(canvas);
        this.add(canvas, BorderLayout.CENTER);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 822));
        //setResizable(false);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        this.pack();
    }

}
