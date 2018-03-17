package model;


import util.CoordinateUtilities;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
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
public class LineStroke extends DrawableObject implements Serializable {
    
    /* The sequence of points that form the stroke  */
    private List<Point> stroke;

    /* The color of the stroke to be drawn */
    private final Color color;
    private final int size;

    LineStroke(Color color, int size){
        super();
        this.color = color;
        this.size = size;
        stroke = new ArrayList<>();
    }

    /** renderRelativeToKernel -- The line being drawn should be appropriately scaled
     * to only show the part of the line that is actually in the current kernel. The
     * lines drawn on the map remain fixed to those positions, regardless of kernel size
     * or location. This method manages the conversion of the points of this line from
     * their absolute positions to the relative positions within the current kernel
     *
     * @param g     The graphics object the line should be rendered on
     * @param kx    The top left x component of the kernel position
     * @param ky    The top left y component of the kernel position
     * @param ksize The width of the kernel (always square)
     * @param csize The width of the drawing canvas the line should be relatively adjusted to
     *              (again, always square).
     */
    public void renderRelativeToKernel(Graphics g, int kx, int ky, int ksize, int csize){
         /* A line needs at least two points */
        if(stroke.size() < 2)
            return;

        /* Save off the old color being used */
        Graphics2D g2d = (Graphics2D) g;

        /* Set the graphic's drawing state to the color of this line with the size of this line */
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(size));

        /* For each sequential pair of points in the line, draw a graphics line between the two */
        Point last = stroke.get(0);
        for(int i = 1; i < stroke.size(); i++) {
            Point lastRel = CoordinateUtilities.relPointFromAbs(kx, ky, last, ksize, csize);
            Point currRel = CoordinateUtilities.relPointFromAbs(kx, ky, stroke.get(i), ksize, csize);
            g.drawLine(lastRel.x, lastRel.y, currRel.x, currRel.y);
            last = stroke.get(i);
        }
    }

    /* Add a visited point to the list of points representing this stroke */
    public void addStroke(int x, int y){
        stroke.add(new Point(x, y));
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(100);
        sb.append("LineStroke(");
        sb.append(size);
        sb.append(", [");
        sb.append(color.getRed());
        sb.append(", ");
        sb.append(color.getGreen());
        sb.append(", ");
        sb.append(color.getBlue());
        sb.append("], ");
        sb.append(stroke.size());
        sb.append(" points");
        sb.append(" )");
        return sb.toString();
    }
}
