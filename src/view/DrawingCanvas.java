package view;

import model.MapModel;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class DrawingCanvas extends JPanel implements Observer {

    private MapModel model;
    Dimension d;

    public DrawingCanvas(MapModel m){
        this.model = m;
        m.initKernel(0, 0, Math.min(getSize().width, getSize().height));
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Dimension d = getSize();
        int squareDimension = Math.min(d.width, d.height);
        g.drawImage(model.getKernel(), 0, 0, squareDimension, squareDimension, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }
}
