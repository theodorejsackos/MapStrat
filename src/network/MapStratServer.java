package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapStratServer extends ServerSocket implements Runnable{

    private ConcurrentHashMap<String, List<Client>> sessions;

    public MapStratServer() throws IOException {
        super();
        sessions = new ConcurrentHashMap<>();

        new Thread(this).start();
    }

    private class Client extends Socket {
        public ObjectInputStream ois;
        public ObjectOutputStream oos;
        public String id;
    }

    private class SessionHandler extends Thread {

        private final Client client;
        private final String session;

        public SessionHandler(Client client) throws IOException {
            this.client  = client;
            session = client.ois.readUTF();  // Expects client to send raw session string for initialization.
            System.out.println("Server accepted group-join for " + session);
            if (sessions.containsKey(session))
                sessions.get(session).add(client);
            else {
                ArrayList<Client> group = new ArrayList<>();
                group.add(client);
                sessions.put(session, group);
            }
        }

        @Override
        public void run(){
            try{
                safeRun();
            }catch (IOException e){
                e.printStackTrace();
                System.exit(1);
            }
        }

        private void safeRun() throws IOException {

            /* Respond to the client with their id relative to the server */
            Message handshakeReply = Message.handshake(client.id);
            handshakeReply.send(client);

            while(true){
                Message incoming = Message.readMessage(client.ois);
            }
        }
    }

    @Override
    public void run() {
        try {
            /* Accept all incoming connections and pass them off to the SessionHandler threads */
            while(true) {
                Client client = (Client) accept();
                client.ois = new ObjectInputStream(client.getInputStream());
                client.oos = new ObjectOutputStream(client.getOutputStream());
                client.id = UUID.randomUUID().toString();
                new SessionHandler(client).start();
            }
        } catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException{
        MapStratServer mss = new MapStratServer();
    }
}
