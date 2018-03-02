package view;

import model.DrawModel;
import model.MapModel;

import javax.swing.*;
import java.awt.*;

public class MapStratFrame extends JFrame {

    private DrawingCanvas canvas;

    public MapStratFrame(MapModel map, DrawModel draw){

        /* Create the background map view and add it to this window */
        canvas = new DrawingCanvas(map, draw);
        map.addObserver(canvas);
        this.add(canvas, BorderLayout.CENTER);

        /* Set the operations that this window supports */
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 422));
        setSize(new Dimension(800, 822));
        //setResizable(false);

        /* Center this window on the screen when the program is started */
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }

}
