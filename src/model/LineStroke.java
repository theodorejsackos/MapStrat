package model;


import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/** The LineStroke class represents the sequence of points accumulated as the
 * user drags the mouse in a drawing event. As the mouse moves to new positions
 * points are added to a list of visited points. When the line is rendered on the
 * canvas, a line is drawn from p0 to p1, p1 to p2, etc. to form the entire line
 * referred to as a "Stroke".
 *
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 * @see view.DrawingCanvas
 * @see model.DrawableObject
 * @see model.DrawModel
 * @see controller.MapDrawListener
 */
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
        /* A line needs at least two points */
        if(stroke.size() < 2)
            return;

        /* Save off the old color being used */
        Graphics2D g2d = (Graphics2D) g;
        Color old = g2d.getColor();

        /* Set the graphic's drawing state to the color of this line with the size of this line */
        g2d.setColor(c);
        g2d.setStroke(new BasicStroke(size));

        /* For each sequential pair of points in the line, draw a graphics line between the two */
        Point last = stroke.get(0);
        for(int i = 1; i < stroke.size(); i++) {
            g.drawLine(last.x, last.y, stroke.get(i).x, stroke.get(i).y);
            last = stroke.get(i);
        }

        /* Load the old color */
        g2d.setColor(old);
    }

    /* Add a visited point to the list of points representing this stroke */
    public void addStroke(int x, int y){
        stroke.add(new Point(x, y));
    }
}
