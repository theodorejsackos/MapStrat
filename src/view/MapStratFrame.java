package view;

import controller.MapDrawListener;
import controller.MapMoveListener;
import controller.MapScrollListener;
import model.DrawModel;
import model.MapModel;
import newnet.Message;
import util.GroupUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/** The MapStratFrame is the top-level window container for the applicataion's view elements.
 * The MapStratFrame contains a drawable, scrollable, zoomable canvas displaying a portion
 * of the world map (the 'kernel') and a control panel on the right for changing drawing
 * settings, and showing the state of the connection to the draw server (if any).
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
        /* Create the Menu Bar and the Session menu option */
        initializeMenuBar(draw);

        /* Create the background map view and add it to this window */
        initializeCanvas(map, draw);

        /* Create the control panel view on the right side. */
        initializeControl(map, draw);

        /* Set the window size to fit the content, location to the center of the screen, load the icon displayed in the
         * window's frame and/or in the taskbar, and set the close behavior when the X is clicked (kill everything) */
        initializeWindowSettings();

        /* Register the event listeners for this application */
        initializeWindowListeners(map, draw);
    }

    /**
     * Construct the relevant MenuBar objects to represent the "Session" tab with the "Join Session..."
     * and "Leave session" items. This dropdown menu system is the primary interface to join a drawing
     * group ("session") and to leave it. These items have action event listeners that respond by triggering
     * the prompt-and-connect dialog (join) or the disconnect (leave) actions.
     * @param draw
     */
    private void initializeMenuBar(DrawModel draw){
        JMenuBar menu = new JMenuBar();
        JMenu session = new JMenu("Session");
        JMenuItem join = new JMenuItem("Join Session...");
        JMenuItem leave = new JMenuItem("Leave Session");
        leave.setEnabled(false);
        session.add(join);
        session.add(leave);
        menu.add(session);
        this.add(menu, BorderLayout.NORTH);

        join.addActionListener((ActionEvent e) -> {

            /* This text field displays a hint to the user what the expected input is, but only
             * materializes the content that the user actually types in. */
            HintTextField group  = new HintTextField("https://mapgee.us/GROUP_ID");

            /* Create a custom query panel to display on the prompted dialog */
            JPanel querier = new JPanel(new GridLayout(7, 1));
            JLabel prompt = new JLabel("Enter a group invite link or group ID. They will look something like:");
            querier.add(prompt);
            querier.add(new JLinkLabel("https://mapgee.us/jAbfHa"));
            querier.add(new JLinkLabel("http://mapgee.us/jAbfHa"));
            querier.add(new JLinkLabel("mapgee.us/jAbfHa"));
            querier.add(new JLinkLabel("jAbfHa"));
            querier.add(Box.createVerticalStrut(5));
            querier.add(group);

            String  gid;
            String  validation;
            boolean invalid;
            int     dialogSelection;

            dialogSelection = JOptionPane.showConfirmDialog(null, querier, "Connect to a group on a server", JOptionPane.OK_CANCEL_OPTION);
            gid        = group.getText();
            validation = GroupUtilities.validateGroup(gid);
            invalid    = validation.equals("");
            prompt.setText("<html><font color='red'>That group ID link was not valid. This is what valid links look like:</font></html>");

            while (dialogSelection == JOptionPane.OK_OPTION && invalid) {
                group.setText(""); // Clear the old input from the hint text field (thus revealing the hint again)
                dialogSelection = JOptionPane.showConfirmDialog(null, querier, "Connect to a group on a server", JOptionPane.OK_CANCEL_OPTION);

                gid        = group.getText();
                validation = GroupUtilities.validateGroup(gid);
                invalid    = validation.equals("");

            }

            if(dialogSelection == JOptionPane.OK_OPTION){
                int    port = 8080;
                draw.connect(port, gid);
                leave.setEnabled(true);
                join.setEnabled(false);
            }
        });

        leave.addActionListener((ActionEvent e) -> {
            draw.disconnect();
            leave.setEnabled(false);
            join.setEnabled(true);
        });
    }

    private void initializeCanvas(MapModel map, DrawModel draw){
        canvas = new DrawingCanvas(map, draw);
        map.addObserver(canvas);  // When the map or kernel changes, update the canvas
        draw.addObserver(canvas); // When artifacts are drawn, update the canvas
        this.add(canvas, BorderLayout.CENTER);
    }

    private void initializeControl(MapModel map, DrawModel draw){
        control = new ControlPanel(map, draw);
        draw.addObserver(control); // When the control configuration changes, update the view elements
        this.add(control, BorderLayout.EAST);
    }

    private void initializeWindowSettings(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.getContentPane().setPreferredSize(
                new Dimension(PREFERRED_CANVAS_SIZE + PREFERRED_CONTROL_SIZE, // Width, size of canvas + control
                        PREFERRED_CANVAS_SIZE)                                // height, size of canvas
        );

        /* Set the frame icon */
        try {
            setIconImage(ImageIO.read(getClass().getResource("/MapStratIcon.png")));
            repaint();
        } catch (IOException e) {
            System.err.println("Failed to load image icon");
        }

        this.pack();

        /* Center the frame on the main screen */
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }

    private void initializeWindowListeners(MapModel map, DrawModel draw){
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
