package newnet;

import model.DrawableObject;

import java.io.IOException;
import java.util.*;

public class Group {
    private final String               gid;
    private final List<Host>           clients;
    private final List<DrawableObject> state;

    public Group(String groupId){
        this.gid = groupId;
        this.clients  = Collections.synchronizedList(new ArrayList<>());
        this.state    = Collections.synchronizedList(new ArrayList<>());
    }

    public void addMember(Host h){
        clients.add(h);
    }

    public int getNumPeers(){
        return clients.size();
    }
    public String getGid(){ return gid; }

    public void drop(Host client){
        clients.remove(client);
        try {
            client.close();
        } catch (IOException e){
            System.err.println("\t" + client + " failed to be closed.");
        }
    }

    /* Update all members with the newest group membership information -- number of peers in particular */
    public void statusBroadcast(){
        Message s = Message.status(gid, clients.size());

        for(Host client : clients)
            s.send(client);
    }

    public void refreshUnicast(Host client){
        Message.refresh(gid, state).send(client);
    }

    public void updateState(DrawableObject o){
        state.add(o);

        for(Host client : clients){
            Message.update(gid, o).send(client);
        }
    }
}
