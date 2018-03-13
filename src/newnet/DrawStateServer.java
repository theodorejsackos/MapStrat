package newnet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DrawStateServer extends ServerSocket implements Runnable{

    private ConcurrentHashMap<String, Group> sessions;

    public DrawStateServer() throws IOException {
        super();
        sessions = new ConcurrentHashMap<>();

        new Thread(this).start();
    }

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
                safeRun();
            }catch (IOException e){
                e.printStackTrace();
                System.exit(1);
            }
        }

        private void safeRun() throws IOException {

            // Respond to the client with a status message
            Message status = Message.status(client.id, group.getNumPeers());

            while(true){
                Message incoming = Message.get(client.ois);
            }
        }
    }

    @Override
    public void run() {
        try {
            /* Accept all incoming connections and pass them off to the GroupHandler threads */
            while(true) {
                Host client = (Host) accept();
                client.ois = new ObjectInputStream(client.getInputStream());
                client.oos = new ObjectOutputStream(client.getOutputStream());
                Message m = Message.get(client.ois);
                if(m.isJoin()){
                    client.id = m.getGroupId();
                    new ClientHandler(client).start();
                }else{
                    client.close();
                }


            }
        } catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException{
        DrawStateServer mss = new DrawStateServer();
    }
}
