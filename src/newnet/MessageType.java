package newnet;

import java.io.Serializable;

public enum MessageType implements Serializable {
    JOIN_GROUP,
    LEAVE_GROUP,
    STATUS,
    UPDATE,
    REFRESH;
}
