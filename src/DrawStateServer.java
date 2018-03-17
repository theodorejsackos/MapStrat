
import newnet.Group;
import newnet.Host;
import newnet.Message;
import util.GroupUtilities;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class DrawStateServer extends ServerSocket implements Runnable{

    private ConcurrentHashMap<String, Group> sessions;

    public static void main(String[] args) throws IOException{
        int port = 0;
        if(Arrays.asList(args).contains("-p")){
            port = Integer.parseInt(args[Arrays.asList(args).indexOf("-p") + 1]);
        }
        new DrawStateServer(port);
    }

    private DrawStateServer(int port) throws IOException {
        super(8080);
        sessions = new ConcurrentHashMap<>();
        new Thread(this).start();
    }

    /** DrawStateServer.run() --
     *
     * The server socket will run a thread accepting clients and distributing the
     * management work to other threads.
     */
    public void run() {
        System.out.println("DrawStateServer listening on port " + this.getLocalPort());
        try {
            while(true) {
                /* Accept all incoming connections and pass them off to the ClientHandler threads */
                Socket c = accept();
                new ClientHandler(c).start();
            }
        } catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    /* class ClientHandler --
     * Receives a client socket connection that has already been established,
     * facilitates the server processing logic for requests from and updates to
     * the given client. */
    private class ClientHandler extends Thread {

        private Group group;
        private Host client;

        private final Socket c;

        private volatile boolean connected;

        public ClientHandler(Socket c) {
            this.c = c;
        }

        private void stopHandlingAndCleanup(Host client){
            connected = false;
            if(group != null) {
                group.drop(client);

                    /* If the group is empty, delete it and allow the state to be garbage collelcted. */
                if (group.getNumPeers() == 0 && group.getGid() != null)
                    sessions.remove(group.getGid());
            }
        }

        @Override
        public void run(){
            try {
                client = new Host(c);
                connected = true;
                handleClient();
            } catch (SocketException e){
                /* Connection can be reset, causing Message.get() to throw a socket exception. This is
                 * caused by clients disconnecting abruptly. Drop the client, end this thread and remove
                 * the client from its member group. */
                System.err.println("ClientHandler for " + client + " received connection reset, dropping client.");
                stopHandlingAndCleanup(client);
            } catch (IOException e){
                System.err.println("ClientHandler " + this.getId() + " failed to create the associated streams for the host.");
            }
        }

        private void stopRunning(){
            connected = false;
        }

        private void handleClient() throws SocketException {
            while(connected){
                Message m = Message.get(client);
                if(m == null) {
                    System.err.println("Garbage message received from " + client + " severing connection.");
                    connected = false;
                    group.drop(client);
                    continue;
                }

                //System.err.printf("[Group %s]: Client handler received ", client.id);
                switch(m.type){
                    case JOIN_GROUP:
                        /* Get the group ID that the client is trying to connect to */
                        client.setGroup(m.getGroupId());

                        /* Check the group membership status */
                        String gid = client.id;
                        if (sessions.containsKey(gid)){
                            /* If the group already exists, add the connecting client to that group */
                            sessions.get(gid).addMember(client);
                            group = sessions.get(gid);
                        } else {
                            /* If the group does not yet exist, create it then add the connecting client to it */
                            Group g = new Group(client.id);
                            g.addMember(client);
                            sessions.put(gid, g);
                            group = g;
                        }

                        /* Respond to the client with a status message indicating that the group join was successful */
                        System.err.println("Responding with STATUS to client connecting to group " + client.id);
                        group.statusBroadcast();

                        /* Inform this particular client of the current group drawing state */
                        group.refreshUnicast(client);
                        break;
                    case LEAVE_GROUP:
                        /* Notify the client that they have left the group (numpeers = 0) */
                        Message.status(client.id, 0).send(client);
                        /* Remove the client from the group and close the connection */
                        stopHandlingAndCleanup(client);
                        break;
                    case UPDATE:
                        /* An update that was received from one host should be shared to all other hosts */
                        group.updateState(m.getStroke());
                        break;

                    case STATUS:
                        System.err.println("STATUS message should not be sent to the server, outgoing only");
                        break;
                    case REFRESH:
                        System.err.println("REFRESH message should not be sent to the server, outgoing only");
                        break;
                }
            }
        }
    }
}
