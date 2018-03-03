package view;

import model.DrawModel;
import model.MapModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class ControlPanel extends JPanel implements Observer{

    private MapModel mapModel;
    private DrawModel drawModel;

    private JPanel icons;
    private JLabel crate, tree, house, rock;

    private JButton colorButton;
    private JColorChooser color;

    private JPanel sizes;
    private JButton small, medium, large;

    private ButtonGroup mapSelection;
    private JRadioButton normal, gis, topo;

    public ControlPanel(MapModel mm, DrawModel dm){
        this.mapModel = mm;
        this.drawModel = dm;

        // Set the preferred size of this control panel
        this.setMinimumSize(new Dimension(200, 400));
        this.setSize(new Dimension(200, 400));
        this.setPreferredSize(new Dimension(200, 400));

        /* Link the "Choose Color" button to a JColorPicker popup and update the model
         * based on what was selected */
        color = new JColorChooser();
        colorButton = new JButton("Choose Color");
        colorButton.addActionListener((ActionEvent e) ->
                drawModel.setColor(JColorChooser.showDialog(null, "Change Color", colorButton.getBackground()))
        );
        this.add(colorButton);

        /* Initialize the panel of the three size buttons to modify the drawing line
         * thickness. Start by creating each of the buttons with the icon inside */
        try {
            small = new JButton(new ImageIcon(ImageIO.read(new File("res/small.png"))));
            medium = new JButton(new ImageIcon(ImageIO.read(new File("res/medium.png"))));
            large = new JButton(new ImageIcon(ImageIO.read(new File("res/large.png"))));
        }catch(IOException e){
            System.err.println("Failed to load brush size images for buttons, defaulting to text.");
            small = new JButton("Small");
            medium = new JButton("Medium");
            large = new JButton("Large");
        }

        /* Create a container view to list all three on the same row */
        sizes  = new JPanel();
        sizes.setLayout(new GridLayout(1, 3));
        sizes.add(small);
        sizes.add(medium);
        sizes.add(large);
        Dimension preferred = new Dimension(64, 32);
        small.setPreferredSize(preferred);
        medium.setPreferredSize(preferred);
        large.setPreferredSize(preferred);

        /* When clicked on they will update the DrawModel state */
        small.addActionListener((ActionEvent e)  -> drawModel.setSize(DrawModel.SMALL));
        medium.addActionListener((ActionEvent e) -> drawModel.setSize(DrawModel.MEDIUM));
        large.addActionListener((ActionEvent e)  -> drawModel.setSize(DrawModel.LARGE));
        this.add(sizes);

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

    /* Given a button to be selected, the size of that selection, and the other two size buttons
     * to be 'unselected' change the background appropriately */
    private void selectButton(JButton select, JButton unselect1, JButton unselect2){
        select.setBackground(new Color(200, 200, 200));
        unselect1.setBackground(null);
        unselect2.setBackground(null);
    }

    @Override
    public void update(Observable o, Object arg) {
        /* Update the state of the selected size button */
        switch(drawModel.getSize()){
            case DrawModel.SMALL:
                selectButton(small, medium, large);
                break;
            case DrawModel.MEDIUM:
                selectButton(medium, small, large);
                break;
            case DrawModel.LARGE:
                selectButton(large, medium, small);
                break;
            default:
                break;
        }

        colorButton.setBackground(drawModel.getColor());
    }
}
