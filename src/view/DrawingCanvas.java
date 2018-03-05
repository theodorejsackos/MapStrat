package view;

import model.DrawModel;
import model.DrawableObject;
import model.MapModel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

/** The DrawingCanvas contains the backgound map image and all drawings that
 * are added to the map. This method overrides the paintComponent method of the
 * JPanel parent class to do all rendering actions.
 *
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 * @see model.MapModel
 * @see model.DrawModel
 */
public class DrawingCanvas extends JPanel implements Observer {

    private MapModel mapModel;
    private DrawModel drawModel;

    public DrawingCanvas(MapModel mm, DrawModel dm){
        this.mapModel  = mm;
        this.drawModel = dm;
        mapModel.initKernel(0, 0, Integer.MAX_VALUE); // Maximize the size of the kernel, show the whole map
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        /* While the large image files are being loaded from disk, display a loading animation */
        Dimension d = getSize();
        if(mapModel.isLoading()){

            /* The background should be all black */
            g.setColor(new Color(0, 0, 0));
            g.fillRect(0, 0, d.width, d.height);

            /* display the loading sprite centered on the canvas */
            BufferedImage loadingKernel = mapModel.getKernel();
            g.drawImage(loadingKernel,
                    (d.width / 2) - loadingKernel.getWidth() / 2,   // The x position of the top-left of the sprite
                    (d.height / 2) - loadingKernel.getHeight() / 2, // The y positoin of the top-left of the sprite
                    loadingKernel.getWidth(),                       // The sprite's width
                    loadingKernel.getHeight(),                      // The sprite's height
                    null);
            return;
        }

        /* For the large image this is a HUGE bottleneck. Need to switch to tiling the large image. */
        int squareDimension = Math.min(d.width, d.height);
        g.drawImage(mapModel.getKernel(), 0, 0, squareDimension, squareDimension, null);

        /* Render all drawn objects */
        for(DrawableObject drawable : drawModel.getAll()){
            int drawX = drawable.getX(), drawY = drawable.getY();
            drawable.renderRelativeToKernel(g,
                        mapModel.getKernelX(), mapModel.getKernelY(), mapModel.getKernelSize(), this.getWidth());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }
}
