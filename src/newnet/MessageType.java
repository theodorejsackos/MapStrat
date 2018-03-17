package newnet;

import java.io.Serializable;

public enum MessageType implements Serializable {

    JOIN_GROUP(0),
    LEAVE_GROUP(1),
    STATUS(2),
    UPDATE(3);

    private final int type;
    MessageType(int type){
        this.type = type;
    }

    public int get(){
        return type;
    }
}
