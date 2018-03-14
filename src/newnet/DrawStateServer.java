package newnet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

public class DrawStateServer extends ServerSocket implements Runnable{

    private ConcurrentHashMap<String, Group> sessions;

    public static void main(String[] args) throws IOException{
        new DrawStateServer();
    }

    private DrawStateServer() throws IOException {
        super();
        sessions = new ConcurrentHashMap<>();
        new Thread(this).start();
    }

    /** DrawStateServer.run() --
     *
     * The server socket will run a thread accepting clients and distributing the
     * management work to other threads.
     */
    public void run() {
        try {
            while(true) {
                /* Accept all incoming connections and pass them off to the ClientHandler threads */
                hostAccept();
            }
        } catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void hostAccept() throws IOException{
        /* Accept a host and wrap input and output streams */
        Host client = (Host) accept();
        client.ois = new ObjectInputStream(client.getInputStream());
        client.oos = new ObjectOutputStream(client.getOutputStream());

        /* Connecting host is responsible for sharing the group id they want to join */
        Message m = Message.get(client);
        if(m != null && m.isJoin()){
            client.id = m.getGroupId();
            new ClientHandler(client).start();
        }
        /* Wrong handshake message, or invalid messages should have the host dropped */
        else{
            System.err.println("Dropping host attempting to connect with bad handshake");
            client.close();
        }
    }



    /* class ClientHandler --
     * Receives a client socket connection that has already been established,
     * facilitates the server processing logic for requests from and updates to
     * the given client. */
    private class ClientHandler extends Thread {

        private final Group group;
        private final Host client;

        public ClientHandler(Host h) throws IOException {
            this.client = h;

            String gid = client.id;

            System.out.println("Server accepted group-join for " + gid);
            if (sessions.containsKey(gid)){
                sessions.get(gid).addMember(client);
                group = sessions.get(gid);
            }else {
                Group g = new Group(client.id);
                g.addMember(client);
                sessions.put(gid, g);
                group = g;
            }
        }

        @Override
        public void run(){
            try{
                handleClient();
            }catch (IOException e){
                e.printStackTrace();
                System.exit(1);
            }
        }

        private void handleClient() throws IOException {

            // Respond to the client with a status message
            Message status = Message.status(client.id, group.getNumPeers());
            status.send(client);

            while(true){
                Message m = Message.get(client);
                if(m == null) {
                    System.err.println("Garbage message received from " + client);
                    continue;
                }

                System.out.printf("[Group %s]: Client handler received ", client.id);
                switch(m.type){
                    case JOIN_GROUP:
                        System.err.println("JOIN_GROUP message");
                        break;
                    case LEAVE_GROUP:
                        System.err.println("LEAVE_GROUP message");
                        /* Notify the client that they have left the group (numpeers = 0) */
                        Message.status(client.id, 0).send(client);
                        /* Remove the client from the group and close the connection */
                        group.drop(client);
                        break;
                    case STATUS:
                        System.err.println("STATUS message");
                        break;
                    case UPDATE:
                        System.err.println("UPDATE message");
                        break;
                    case REFRESH: // Client requesting most up-to date drawing state
                        System.err.println("REFRESH message");
                        group.updateUnicast(client);
                        break;
                }
            }
        }
    }
}
