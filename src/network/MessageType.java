package network;

import java.io.Serializable;

public enum MessageType implements Serializable {

    HANDSHAKE (0),
    HEARTBEAT (1),
    WITHDRAW  (2),
    NEW_STROKE(3),
    NEW_STAMP (4),
    CLEAR(5),
    UNDO (6),
    REDO (7);

    private final int type;
    MessageType(int type){
        this.type = type;
    }

    public int get(){
        return type;
    }
}
