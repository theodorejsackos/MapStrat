package newnet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Host extends Socket {
    public ObjectInputStream ois;
    public ObjectOutputStream oos;
    public String id;

    public Host(String host, int port) throws IOException{
        super(host, port);
    }
}

