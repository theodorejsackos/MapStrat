package newnet;

import model.DrawableObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Group {
    private String groupId;
    private int numPeers;

    private Set<Host> clients;
    private List<DrawableObject> state;

    public Group(String groupId){
        this.groupId  = groupId;
        this.numPeers = 0;
        this.clients  = new TreeSet<>();
        this.state    = new ArrayList<>();
    }

    public void addMember(Host h){
        clients.add(h);
    }

    public int  getNumPeers(){
        return numPeers;
    }

    public void peerAdded(){
        numPeers++;
    }

    public void peerRemoved(){
        numPeers--;
    }
}
