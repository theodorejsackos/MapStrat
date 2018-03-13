package newnet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class Host extends Socket {
    public ObjectInputStream ois;
    public ObjectOutputStream oos;
    public String id;
}

