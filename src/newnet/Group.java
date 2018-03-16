package newnet;

import model.DrawableObject;

import java.io.IOException;
import java.util.*;

public class Group {
    private String gid;
    private int numPeers;

    private List<Host> clients;
    private List<DrawableObject> state;

    private long lastBroadcast;
    private final int BROADCAST_LIMITER_MILIS = 250;

    public Group(String groupId){
        this.gid = groupId;
        this.numPeers = 0;
        this.clients  = new ArrayList<>();
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

    public synchronized void addMember(Host h){
        peerAdded();
        clients.add(h);
    }

    public int  getNumPeers(){
        return numPeers;
    }

    private void peerAdded(){
        numPeers++;
    }

    private void peerRemoved(){
        numPeers--;
    }

    public synchronized void drop(Host client) throws IOException {
        peerRemoved();
        clients.remove(client);
        client.close();
    }

    /* Update all members with the newest group membership information -- number of peers in particular */
    public void statusBroadcast(){
        Message s = Message.status(gid, numPeers);

        for(Host client : clients)
            s.send(client);
    }

    public void refreshUnicast(Host client){
        Message.refresh(gid, state).send(client);
    }

    public void updateState(DrawableObject o){
        state.add(o);
        System.out.println(state);
        refreshBroadcast();
    }

    public void refreshBroadcast(){
        Message m = Message.refresh(gid, state);

        for(Host client : clients)
            m.send(client);
    }
}
