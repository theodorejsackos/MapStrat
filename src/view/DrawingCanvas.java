package view;

import model.DrawModel;
import model.DrawableObject;
import model.MapModel;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class DrawingCanvas extends JPanel implements Observer {

    private MapModel mapModel;
    private DrawModel drawModel;
    Dimension d;

    public DrawingCanvas(MapModel mm, DrawModel dm){
        this.mapModel  = mm;
        this.drawModel = dm;
        mapModel.initKernel(0, 0, Math.min(getSize().width, getSize().height));
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Dimension d = getSize();
        int squareDimension = Math.min(d.width, d.height);
        g.drawImage(mapModel.getKernel(), 0, 0, squareDimension, squareDimension, null);

        for(DrawableObject drawable : drawModel.getAll()){
            drawable.render(g);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }
}
