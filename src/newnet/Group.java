package newnet;

import model.DrawableObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Group {
    private String gid;
    private int numPeers;

    private List<Host> clients;
    private List<DrawableObject> state;

    public Group(String groupId){
        this.gid = groupId;
        this.numPeers = 0;
        this.clients  = new ArrayList<>();
        this.state    = new ArrayList<>();
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

    public void updateUnicast(Host client){
        Message.refresh(gid, state).send(client);
    }

    public void updateState(DrawableObject o){
        state.add(o);
        System.out.println(state);
        updateBroadcast();
    }

    public void updateBroadcast(){
        Message m = Message.refresh(gid, state);

        for(Host client : clients)
            m.send(client);
    }
}
