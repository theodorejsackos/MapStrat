package view;

import controller.MapDrawListener;
import controller.MapMoveListener;
import controller.MapScrollListener;
import model.DrawModel;
import model.MapModel;
import model.SessionModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;

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

    private MenuBar menu;
    private DrawingCanvas canvas;
    private ControlPanel  control;

    public MapStratFrame(MapModel map, DrawModel draw, SessionModel sesh){


        /* Create the Menu Bar and the Session menu option */
        menu = new MenuBar(sesh);

        /*menu = new JMenuBar();*/
        JMenu session = new JMenu("Session");
        JMenuItem join = new JMenuItem("Join Session...");
        JMenuItem leave = new JMenuItem("Leave Session");
        leave.setEnabled(false);
        session.add(join);
        session.add(leave);
        menu.add(session);
        this.add(menu, BorderLayout.NORTH);

        join.addActionListener((ActionEvent e) -> {

            JTextField server = new JTextField();
            JTextField group  = new JTextField();

            JPanel querier = new JPanel(new GridLayout(5, 1));
            querier.add(new JLabel("Server location (host:port):"));
            querier.add(server);
            querier.add(Box.createHorizontalStrut(15));
            querier.add(new JLabel("Session ID (group ID):"));
            querier.add(group);

            int result = JOptionPane.showConfirmDialog(null, querier, "Connect to a group on a server", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {

                //TODO: How will this be handeled? Ideally don't put this responsibility on the user, just connect to a  well-known domain and port
                String host = server.getText();//.split(":")[0];
                int    port = 8080;//Integer.parseInt(server.getText().split(":")[1]);
                String gid  = group.getText();

                draw.connect(host, port, gid);
                leave.setEnabled(true);
                join.setEnabled(false);
            }
        });

        leave.addActionListener((ActionEvent e) -> {
            leave.setEnabled(false);
            join.setEnabled(true);
            draw.disconnect();
        });

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
