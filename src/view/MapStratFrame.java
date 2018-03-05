package view;

import controller.MapDrawListener;
import controller.MapMoveListener;
import controller.MapScrollListener;
import model.DrawModel;
import model.MapModel;

import javax.swing.*;
import java.awt.*;

/** The MapStratFrame is the top-level window container for the applicataion's view elements.
 * The MapStratFrame contains a drawable, scrollable, zoomable canvas displaying a portion
 * of the world map (the 'kernel') and a control panel on the right for changing drawing
 * settings.
 *
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 * @see view.DrawingCanvas
 * @see view.ControlPanel
 */
public class MapStratFrame extends JFrame {

    private static final int PREFERRED_CANVAS_SIZE = 800, PREFERRED_CONTROL_SIZE = 200;

    private DrawingCanvas canvas;
    private ControlPanel  control;

    public MapStratFrame(MapModel map, DrawModel draw){

        /* Create the background map view and add it to this window */
        canvas = new DrawingCanvas(map, draw);
        map.addObserver(canvas);  // When the map or kernel changes, update the canvas
        draw.addObserver(canvas); // When artifacts are drawn, update the canvas
        this.add(canvas, BorderLayout.CENTER);

        /* Create the control panel view on the right side. */
        control = new ControlPanel(map, draw);
        draw.addObserver(control); // When the control configuration changes, update the view elements
        this.add(control, BorderLayout.EAST);

        /* Set the operations that this window supports */
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.getContentPane().setPreferredSize(new Dimension(PREFERRED_CANVAS_SIZE + PREFERRED_CONTROL_SIZE, PREFERRED_CANVAS_SIZE));
        this.pack();

        /* Center this window on the screen when the program is started */
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        /* Register the event listeners for this application */
        this.addMouseWheelListener(new MapScrollListener(map));        // Listens to zoom in and out events (scrolling)
        MapMoveListener mapControl = new MapMoveListener(map);         // Listens to map movement events (right clicking)
        this.addMouseListener(new MapDrawListener(map, draw, canvas)); // Listen to drawing events (left clicks)
        this.addMouseListener(mapControl);
        this.addMouseMotionListener(mapControl);
    }

    public JPanel getDrawingCanvas(){
        return canvas;
    }
}
