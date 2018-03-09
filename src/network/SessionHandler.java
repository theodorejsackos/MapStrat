package network;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SessionHandler extends Thread {

    private final String session;
    private List<Socket> sessionClients;

    public SessionHandler(String session){
        this.session = session;
        sessionClients = new ArrayList<>();
    }

    @Override
    public void run(){

    }
}
