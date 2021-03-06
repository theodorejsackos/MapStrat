package view;

import model.DrawModel;
import model.MapModel;
import version.Version;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Observable;
import java.util.Observer;

/** The ControlPanel is a cluster of view elements controlling (and displaying) the current
 * draw action state (stamping vs. drawing, brush size, color, etc.). The control panel
 * represents the state of the DrawModel, and action listeners registered to these views
 * manage changes to the DrawModel and MapModel (changing the background map).
 *
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 *
 * @see model.DrawModel
 * @see model.MapModel
 */
public class ControlPanel extends JPanel implements Observer{

    private static final Color NOT_SELECTED = new Color(200, 200, 200);

    private MapModel mapModel;
    private DrawModel drawModel;

    private JPanel icons;
    private JLabel crate, tree, house, rock;

    private JButton colorButton;
    private JColorChooser color;

    private JSlider sizeSlider;

    private ButtonGroup mapSelection;
    private JRadioButton normal, gis, topo;

    private JPanel badVersionIndicator;

    public ControlPanel(MapModel mm, DrawModel dm){
        this.mapModel = mm;
        this.drawModel = dm;

        // Set the preferred size of the control panel
        this.setMinimumSize(new Dimension(200, 400));
        this.setSize(new Dimension(200, 400));
        this.setPreferredSize(new Dimension(200, 400));

        initializeColorSelector();
        initializeBrushSelector();
        initializeMapSelector();
        initializeBadVersionIndicator();

        /* Trigger an observer change to initialize the start state of the views */
        drawModel.setSize(drawModel.getSize());
    }

    private void initializeColorSelector(){
        /* Link the "Choose Color" button to a JColorPicker popup and update the model
         * based on what was selected */
        color = new JColorChooser();
        colorButton = new JButton("Choose Color");
        colorButton.addActionListener((ActionEvent e) -> {
                    Color selected = JColorChooser.showDialog(null, "Change Color", colorButton.getBackground());
                    if(selected != null)
                        drawModel.setColor(selected);
                }
        );
        colorButton.setBackground(drawModel.getColor());
        this.add(colorButton);
    }

    private class BrushSizePreview extends JPanel implements Observer {

        public BrushSizePreview(){
            this.setMinimumSize(new Dimension(32, 32));
            this.setSize(new Dimension(32, 32));
            this.setPreferredSize(new Dimension(32, 32));
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            /* Get the size of the preview panel, wipe it clean with a white background */
            Dimension d = getSize();
            g2d.setColor(new Color(255, 255, 255));
            g2d.fillRect(0, 0, d.width, d.height);

            g2d.setColor(drawModel.getColor());
            g2d.setStroke(new BasicStroke(drawModel.getSize()));
            g.drawLine(0, d.height/2, d.width, d.height/2);
        }

        @Override
        public void update(Observable o, Object arg) {
            this.repaint();
        }
    }

    private void initializeBrushSelector(){
        JPanel brushPanel = new JPanel();

        /* Set up the brush size preview */
        BrushSizePreview bsp = new BrushSizePreview();
        drawModel.addObserver(bsp);
        brushPanel.add(bsp);

        /* Setup the Brush size slider */
        sizeSlider = new JSlider(SwingConstants.HORIZONTAL);
        sizeSlider.setMinimum(DrawModel.BRUSH_SIZE_MINIMUM);
        sizeSlider.setMaximum(DrawModel.BRUSH_SIZE_MAXIMUM);

        sizeSlider.setMajorTickSpacing(2);
        sizeSlider.setMinorTickSpacing(1);

        sizeSlider.setPaintTicks(true);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.setPaintLabels(true);

        sizeSlider.setMinimumSize(new Dimension(100, 64));
        sizeSlider.setSize(new Dimension(100, 64));
        sizeSlider.setPreferredSize(new Dimension(100, 64));

        sizeSlider.setValue(drawModel.getSize());

        sizeSlider.addChangeListener((ChangeEvent e) -> {
            JSlider source = (JSlider) e.getSource();
            if(!source.getValueIsAdjusting())
                drawModel.setSize(source.getValue());
        });
        brushPanel.add(sizeSlider);

        this.add(brushPanel);
    }

    private void initializeMapSelector(){
        /* Create the map selector view elements */
        normal = new JRadioButton("Default View");
        gis = new JRadioButton("GIS View");
        topo = new JRadioButton("Topology View");

        /* Create a container view to list them one per row */
        JPanel radioGroup = new JPanel();
        radioGroup.setLayout(new GridLayout(3, 1));
        radioGroup.add(normal);
        radioGroup.add(gis);
        radioGroup.add(topo);

        /* When selected they will update the MapModel, changing which map is selected */
        normal.addActionListener((ActionEvent e) -> mapModel.setMap(MapModel.MAP_DEFAULT));
        gis.addActionListener((ActionEvent e)    -> mapModel.setMap(MapModel.MAP_GIS));
        topo.addActionListener((ActionEvent e)   -> mapModel.setMap(MapModel.MAP_TOPO));

        /* Add the mutual exclusive property to them */
        mapSelection = new ButtonGroup();
        mapSelection.add(normal);
        mapSelection.add(gis);
        mapSelection.add(topo);
        normal.setSelected(true);
        this.add(radioGroup); // add them to the view
    }

    public void initializeBadVersionIndicator(){
        badVersionIndicator = new JPanel(new GridLayout(2, 1));
        badVersionIndicator.add(new JLabel("Bad version number: " + Version.VERSION));
        badVersionIndicator.add(new JLabel("Visit mapgee.us/ to update."));
        badVersionIndicator.setBackground(Color.red);
        badVersionIndicator.setVisible(false);
        this.add(badVersionIndicator);
    }

    @Override
    public void update(Observable o, Object arg) {
        sizeSlider.setValue(drawModel.getSize());
        colorButton.setBackground(drawModel.getColor());

        // A connection was made but the versions are incompatible.
        if(drawModel.getBadVersion() != null){
            badVersionIndicator.setVisible(true);
        }
        this.repaint();
    }
}
