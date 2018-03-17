package newnet;

import model.DrawableObject;

import java.io.IOException;
import java.util.*;

public class Group {
    private final String gid;
    private int numPeers;

    private final List<Host> clients;
    private final List<DrawableObject> state;

    private long lastBroadcast;
    private final int BROADCAST_LIMITER_MILIS = 250;

    public Group(String groupId){
        this.gid = groupId;
        this.numPeers = 0;
        this.clients  = Collections.synchronizedList(new ArrayList<>());
        this.state    = Collections.synchronizedList(new ArrayList<>());
        lastBroadcast = System.currentTimeMillis();
    }

    /* broadcastAllowed() -- rate-limits broadcasts to all group members to at most
    * 4 times per second.*/
    public boolean broadcastAllowed(){
        if(System.currentTimeMillis() - lastBroadcast < BROADCAST_LIMITER_MILIS)
            return false;

        lastBroadcast = System.currentTimeMillis();
        return true;
    }

    public void addMember(Host h){
        peerAdded();
        clients.add(h);
    }

    public synchronized int getNumPeers(){
        return numPeers;
    }

    private void peerAdded(){
        numPeers++;
    }

    private void peerRemoved(){
        numPeers--;
    }

    public void drop(Host client) throws IOException {
        peerRemoved();
        clients.remove(client);
        client.close();
    }

    /* Update all members with the newest group membership information -- number of peers in particular */
    public void statusBroadcast(){
        //if(!broadcastAllowed())
        //    return;

        Message s = Message.status(gid, numPeers);

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
