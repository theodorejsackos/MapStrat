package newnet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Host {
    public Socket socket;
    public ObjectInputStream ois;
    public ObjectOutputStream oos;
    public String id;

    public Host(Socket s) throws IOException {
        socket = s;
        oos    = new ObjectOutputStream(socket.getOutputStream());
        ois    = new ObjectInputStream(socket.getInputStream());
    }

    public Host(Socket s, String group) throws IOException {
        this(s);
        id = group;
    }

    public Host(String host, int port) throws IOException {
        this(new Socket(host, port));
    }


    public Host(String host, int port, String group) throws IOException {
        this(host, port);
        id = group;
    }

    public void close() throws IOException {
        socket.close();
        ois = null;
        oos = null;
    }

    public void setGroup(String gid){
        this.id = gid;
    }

    @Override
    public String toString(){
        return socket.toString() + " in group " + id;
    }
}

