package util;

import java.awt.*;

public class CoordinateUtilities {

    /** absPosFromRel -- Convert a point coordinate (like the x component of a mouse position)
     * to the absolute position of that coordinate on the background image given the relative
     * position described by the current kernel position and mouse position inside the drawing
     * canvas.
     *
     * @param kernelCoord The single component coordinate of the kernel (for example, x coord of kernel)
     * @param pointCoord  The single component coordinate of the point to convert (for example, the x
     *                    coordinate of the mouse position)
     * @param kernelWidth The width of the current kernel window
     * @param canvasWidth The width of the element (like drawing canvas) the point coordinate is relative
     *                    to.
     * @return The absolute position of the point coordinate in the background image.
     */
    private static int absPosFromRel(int kernelCoord, int pointCoord, int kernelWidth, int canvasWidth){
        return kernelCoord + (int) ((pointCoord * 1.0 / canvasWidth) * kernelWidth);
    }

    private static int relPosFromAbs(int kernelCoord, int pointCoord, int kernelWidth, int canvasWidth){
        return (int) (canvasWidth * ((pointCoord - kernelCoord) * 1.0 / kernelWidth));
    }

    /** relPointFromAbs -- Given the coordinates of the current kernel, the size of the kernel,
     * the size of the canvas being drawn on or rendered to, and a point to convert: convert the
     * given point from a position relative to the canvas represented by the given kernel to an
     * absolute position within within the background image.
     *
     * @param kx    The x coordinate of the kernel
     * @param ky    The y coordinate of the kernel
     * @param rel   The relative point position within the canvas to be converted to an absolute
     * @param ksize The size of the kernel
     * @param csize The size of the canvas to be relative to
     * @return
     */
    public static Point absPointFromRel(int kx, int ky, Point rel, int ksize, int csize){
        int absx = absPosFromRel(kx, rel.x, ksize, csize);
        int absy = absPosFromRel(ky, rel.y, ksize, csize);
        return new Point(absx, absy);
    }

    /** relPointFromAbs -- Given the coordinates of the current kernel, the size of the kernel,
     * the size of the canvas being drawn on or rendered to, and a point to convert: convert the
     * given point from an absolute position within within the background image to a position relative
     * to the canvas represented by the given kernel.
     *
     * @param kx    The x coordinate of the kernel
     * @param ky    The y coordinate of the kernel
     * @param abs   The absolute point position to be converted
     * @param ksize The size of the kernel
     * @param csize The size of the canvas to be relative to
     * @return
     */
    public static Point relPointFromAbs(int kx, int ky, Point abs, int ksize, int csize){
        int relx = relPosFromAbs(kx, abs.x, ksize, csize);
        int rely = relPosFromAbs(ky, abs.y, ksize, csize);
        return new Point(relx, rely);
    }
}
