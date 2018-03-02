package model;

import com.sun.istack.internal.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class MapModel extends Observable {
    private static final int SIZE = 8192;
    private static final int MIN_KERNEL_SIZE = 400;
    private static final HashMap<Integer, String> supportedMaps = new HashMap<>();
    public static final Integer MAP_DEFAULT = 1, MAP_GIS = 2, MAP_TOPO = 3;

    @NotNull
    private BufferedImage mapImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
    private Integer selectedMap = MAP_DEFAULT;

    /* The kernel is the actively viewable part of the map that is being displayed by the view */
    private int kernelX, kernelY, kernelSize;

    public MapModel(Integer selected){
        supportedMaps.put(MAP_DEFAULT, "erangel.jpg");
        supportedMaps.put(MAP_GIS,     "erangel-gis.jpg");
        supportedMaps.put(MAP_TOPO,    "erangel-topo.jpg");

        assert(supportedMaps.containsKey(selected));
        this.selectedMap = selected;
        loadBackground(selected);

        kernelSize = kernelX = kernelY = -1;
    }

    private void loadBackground(Integer selected){
        String mapPath = "res/" + supportedMaps.get(selectedMap);
        try{
            mapImage = ImageIO.read(new File(mapPath));
        }catch(IOException e){
            System.err.printf("The image 'res/%s' could not be loaded.\n", mapPath);
            mapImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        }
    }

    /**
     *
     * @param newMap
     */
    public void setMap(Integer newMap){
        assert(supportedMaps.containsKey(newMap));

        this.selectedMap = newMap;
        loadBackground(newMap);
        setChanged();
        notifyObservers();
    }

    /**
     *
     * @return
     */
    public BufferedImage getMap(){
        return mapImage;
    }

    public BufferedImage getKernel(){
        if(kernelInitialized())
            return mapImage.getSubimage(kernelX, kernelY, kernelSize, kernelSize);
        else
            return getMap();
    }

    /**
     *
     * @return
     */
    public int getMapSize(){
        return SIZE;
    }

    private boolean kernelInitialized(){
        return (kernelSize != -1) && (kernelX != -1) && (kernelY != -1);
    }

    public void initKernel(int kernelX, int kernelY, int kernelSize){
        setKernelX(kernelX);
        setKernelY(kernelY);
        setKernelSize(kernelSize);
        setChanged();
        notifyObservers();
    }

    public void setKernelX(int kernelX) {
        if (kernelX < 0)
            this.kernelX = 0;
        else if (kernelX > SIZE - kernelSize)
            this.kernelX = SIZE - kernelSize;
        else
            this.kernelX = kernelX;
    }

    public void setKernelY(int kernelY) {
        if (kernelY < 0)
            this.kernelY = 0;
        else if (kernelY > SIZE - kernelSize)
            this.kernelY = SIZE - kernelSize;
        else
            this.kernelY = kernelY;
    }

    public void setKernelSize(int kernelSize) {
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

    public void updateKernelSize(int kernelSize){
        setKernelSize(kernelSize);
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
}
