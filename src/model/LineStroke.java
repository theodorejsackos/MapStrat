package model;


import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class LineStroke extends DrawableObject {

    private Color c;
    private List<Point> stroke;
    public LineStroke(int globalX, int globalY, int size, Color c){
        super(globalX, globalY, size);
        this.c = c;
        stroke = new LinkedList<>();
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Color old = g2d.getColor();
        g2d.setColor(c);
        g2d.setStroke(new BasicStroke(size));
        if(stroke.size() < 2)
            g2d.fillOval(this.x, this.y, this.size, this.size);
        else{
            Point last = stroke.get(0);
            for(int i = 1; i < stroke.size(); i++) {
                g.drawLine(last.x, last.y, stroke.get(i).x, stroke.get(i).y);
                last = stroke.get(i);
            }
        }
        g2d.setColor(old);
    }

    public void addStroke(int x, int y){
        stroke.add(new Point(x, y));
    }
}
