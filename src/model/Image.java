package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image extends DrawableObject{

    private BufferedImage image = null;
    private String imagePath = null;

    public Image(String path) throws IOException {
        super();
        imagePath = path;
        image = ImageIO.read(new File("res/" + imagePath));
        width  = image.getWidth();
        height = image.getHeight();
    }

    public Image(String path, int x, int y) throws IOException {
        super(x, y);
        imagePath = path;
        image = ImageIO.read(new File("res/" + imagePath));
        width  = image.getWidth();
        height = image.getHeight();
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(image, x - (width / 2), y - (height / 2), null);
    }
}
