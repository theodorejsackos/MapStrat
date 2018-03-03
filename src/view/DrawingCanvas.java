package view;

import model.DrawModel;
import model.DrawableObject;
import model.MapModel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

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

        Dimension d = getSize();
        if(mapModel.isLoading()){
            g.setColor(new Color(0, 0, 0));
            g.fillRect(0, 0, d.width, d.height);
            BufferedImage loadingKernel = mapModel.getKernel();
            g.drawImage(loadingKernel,
                    (d.width / 2) - loadingKernel.getWidth() / 2,
                    (d.height / 2) - loadingKernel.getHeight() / 2,
                    loadingKernel.getWidth(), loadingKernel.getHeight(), null);
            return;
        }

        /* HUUUUGF 100ms BOTTLENECK NEED TO MINIMIZE THE SIZE OF THIS BACKGROUND DEPENDING ON
         * THE KERNEL */
        int squareDimension = Math.min(d.width, d.height);
        g.drawImage(mapModel.getKernel(), 0, 0, squareDimension, squareDimension, null);

        for(DrawableObject drawable : drawModel.getAll()){
            int drawX = drawable.getX(), drawY = drawable.getY();
            if(mapModel.pointInsideKernel(drawX, drawY))
                drawable.render(g);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }
}
