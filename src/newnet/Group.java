package newnet;

import model.DrawableObject;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String groupId;
    private int numPeers;

    private List<Host> clients;
    private List<DrawableObject> state;

    public Group(String groupId){
        this.groupId  = groupId;
        this.numPeers = 0;
        this.clients  = new ArrayList<>();
        this.state    = new ArrayList<>();
    }

    public void addMember(Host h){
        clients.add(h);
    }

    public int getNumPeers(){
        return numPeers;
    }
}
