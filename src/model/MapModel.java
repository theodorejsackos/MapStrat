package model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;

/** The MapModel represents the state of the background image and the subimage being displayed
 * from that background (referred to as the 'kernel'). This class manages a slew of things.
 * In this class is a cached copy of each background image file, loaded from disk on initialization.
 * While the loading of the images from disk happens, a loading sprite is animated and replaces
 * the background image for all getter methods. The drawing canvas will ask for the current
 * background image kernel; if the images have not yet been loaded from disk, the loading
 * kernel will be displayed and animated until that loading finishes. This model also manages the
 * position and size of the image kernel (always square), and returns the correct subimage
 * of the background based on the current position of the kernel.
 *
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 *
 */
public class MapModel extends Observable {
    private static final int SIZE = 4096;
    private static final int MIN_KERNEL_SIZE = 200;
    private static final int loadingKernelY = 0, loadingKernelSize = 128;
    private static final HashMap<Integer, String> supportedMaps = new HashMap<>();
    public static final int MAP_DEFAULT = 1, MAP_GIS = 2, MAP_TOPO = 3;

    /* Cache each of the backgrounds in memory to prevent long delays when switching between the maps. */
    private static BufferedImage cachedDefault = null, cachedGis = null, cachedTopo = null;
    private static BufferedImage loadingRing = null;
    private boolean loading = true;
    private int loadingKernelX = 0;
    private Timer ringAnimator = null;

    private class BackgroundCacher extends Thread {

        private int desired;
        BackgroundCacher(int desiredBackground){
            this.desired = desiredBackground;
        }

        public void run(){
            cacheBackground(MAP_DEFAULT);
            cacheBackground(MAP_GIS);
            cacheBackground(MAP_TOPO);

            setMap(desired);
            loading = false;
            ringAnimator.stop();
        }
    }

    private BufferedImage mapImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);

    /* The kernel is the actively viewable part of the map that is being displayed by the view */
    private int kernelX, kernelY, kernelSize;

    public MapModel(Integer selected) {
        supportedMaps.put(MAP_DEFAULT, "erangel-med.jpg");
        supportedMaps.put(MAP_GIS, "erangel-gis-med.jpg");
        supportedMaps.put(MAP_TOPO, "erangel-topo-med.jpg");

        assert (supportedMaps.containsKey(selected));

        ringAnimator = new Timer(200, (ActionEvent e) -> {
            if(loadingRing == null){
                try{
                    loadingRing = ImageIO.read(new File("res/loading.png"));
                }catch(IOException ex){
                    System.err.printf("The loading ring 'res/%s' could not be loaded.\n", "res/loading.png");
                    loadingRing = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
                }
            }
            loadingKernelX = (loadingKernelX + 128) % (12 * 128); //animation frames 0, 1, 2, 3, 4, 5
            setChanged();
            notifyObservers();
        });
        ringAnimator.start();

        BackgroundCacher bc = new BackgroundCacher(selected);
        bc.start();

        kernelSize = kernelX = kernelY = -1;
    }

    private static void cacheBackground(int selected){
        String mapPath = "res/" + supportedMaps.get(selected);
        switch(selected){
            case MAP_DEFAULT:
                if(cachedDefault != null)
                    return;
                try{
                    cachedDefault = ImageIO.read(new File(mapPath));
                }catch(IOException e){
                    System.err.printf("The default map 'res/%s' could not be loaded.\n", mapPath);
                    cachedDefault = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
                }
                break;
            case MAP_GIS:
                if(cachedGis != null)
                    return;
                try{
                    cachedGis = ImageIO.read(new File(mapPath));
                }catch(IOException e){
                    System.err.printf("The GIS map 'res/%s' could not be loaded.\n", mapPath);
                    cachedGis = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
                }
                break;

            case MAP_TOPO:
                if(cachedTopo != null)
                    return;
                try{
                    cachedTopo = ImageIO.read(new File(mapPath));
                }catch(IOException e){
                    System.err.printf("The topology map 'res/%s' could not be loaded.\n", mapPath);
                    cachedTopo = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
                }
                break;
        }
    }

    /** Changes the background image being rendered on the drawing canvas.
     *
     * @param newMap The map code; should be one of MapModel.{MAP_DEFAULT, MAP_GIS, MAP_TOPO}
     */
    public void setMap(Integer newMap){
        assert(supportedMaps.containsKey(newMap));

        switch(newMap){
            case MAP_DEFAULT:
                mapImage = cachedDefault;
                break;
            case MAP_GIS:
                mapImage = cachedGis;
                break;
            case MAP_TOPO:
                mapImage = cachedTopo;
                break;
        }
        setChanged();
        notifyObservers();
    }

    public BufferedImage getKernel(){
        if(loading && loadingRing != null){
            return loadingRing.getSubimage(loadingKernelX, loadingKernelY, loadingKernelSize, loadingKernelSize);
        }

        return mapImage.getSubimage(kernelX, kernelY, kernelSize, kernelSize);
    }

    public void updateKernel(int kernelX, int kernelY, int kernelSize){
        if(kernelSize < MIN_KERNEL_SIZE)
            return;

        /* Setting the new kernel size before the x and y position is important for zoom-in with
         * large kernels. If this is not done first, then the zoom in will use the old kernel size
         * in the calculations of the new x,y positions which may yield incorrect positioning. */
        setKernelSize(kernelSize);
        setKernelX(kernelX);
        setKernelY(kernelY);
        setChanged();
        notifyObservers();
    }

    private void setKernelX(int kernelX) {
        if (kernelX < 0)
            this.kernelX = 0;
        else if (kernelX > SIZE - kernelSize)
            this.kernelX = SIZE - kernelSize;
        else
            this.kernelX = kernelX;
    }

    private void setKernelY(int kernelY) {
        if (kernelY < 0)
            this.kernelY = 0;
        else if (kernelY > SIZE - kernelSize)
            this.kernelY = SIZE - kernelSize;
        else
            this.kernelY = kernelY;
    }

    private void setKernelSize(int kernelSize) {
        kernelSize = Math.min(SIZE, kernelSize);
        if (kernelSize < MIN_KERNEL_SIZE)
            this.kernelSize = MIN_KERNEL_SIZE;
        else if (kernelX + kernelSize > SIZE && kernelY + kernelSize > SIZE) {
            this.kernelSize = kernelSize;
            this.kernelX = SIZE - kernelSize;
            this.kernelY = SIZE - kernelSize;
        }
        else if (kernelX + kernelSize > SIZE) {
            this.kernelSize = kernelSize;
            this.kernelX = SIZE - kernelSize;
        }
        else if(kernelY + kernelSize > SIZE){
            this.kernelSize = kernelSize;
            this.kernelY = SIZE - kernelSize;
        }
        else
            this.kernelSize = kernelSize;
    }

    public void updateKernelX(int kernelX){
        if(kernelSize == SIZE)
            return;

        setKernelX(kernelX);
        setChanged();
        notifyObservers();
    }

    public void updateKernelY(int kernelY){
        if(kernelSize == SIZE)
            return;

        setKernelY(kernelY);
        setChanged();
        notifyObservers();
    }

    public int getKernelX() {
        return kernelX;
    }

    public int getKernelY() {
        return kernelY;
    }

    public int getKernelSize() {
        return kernelSize;
    }

    public boolean isLoading(){
        return loading;
    }
}
