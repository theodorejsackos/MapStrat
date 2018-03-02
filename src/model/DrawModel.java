package model;

import java.util.ArrayList;
import java.util.List;

public class DrawModel {

    private List<DrawableObject> drawnObjects = null;

    public DrawModel(){
        drawnObjects = new ArrayList<>(50);
    }

}
