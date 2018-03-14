package newnet;

import com.sun.istack.internal.NotNull;
import model.DrawableObject;
import util.GroupUtilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

/** class Message --
 *  The message class facilitates communication between the map server and each of the connected
 *  clients. Here we will refer to the clients as 'members' or 'group members', each of which must
 *  belong to exactly one group.
 *
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 */
public class Message implements Serializable {

    /* Bookkeeping information, Mandatory for all messages that these fields are used */
    @NotNull
    final MessageType type;
    @NotNull
    final long        gid;

    /* State information, may be null and unused */
    private List<DrawableObject> state;    // For updating a client's state
    private DrawableObject       newItem;  // For adding to the server's state
    private int                  numPeers; // For status update messages (session/group information).

    private Message(MessageType t, long gid){
        this.type = t;
        this.gid  = gid;
    }

    public static Message join(String group){
        return join(GroupUtilities.groupIdFromName(group));
    }

    public static Message join(long group){
        return new Message(MessageType.JOIN_GROUP, group);
    }

    public static Message leave(String group){
        return leave(GroupUtilities.groupIdFromName(group));
    }

    public static Message leave(long group){
        return new Message(MessageType.LEAVE_GROUP, group);
    }

    public static Message status(String group, int numPeers){
        return status(GroupUtilities.groupIdFromName(group), numPeers);
    }

    public static Message status(long group, int numPeers){
        Message m  = new Message(MessageType.STATUS, group);
        m.numPeers = numPeers;
        return m;
    }

    public static Message refresh(String group, List<DrawableObject> state){
        Message m = new Message(MessageType.REFRESH, GroupUtilities.groupIdFromName(group));
        m.state = state;
        return m;
    }

    public static Message get(Host from){
        try{
            return (Message) from.ois.readObject();
        } catch (IOException | ClassNotFoundException e){
            return null;
        }
    }

    public void send(Host to){
        try{
            to.oos.writeObject(this);
        } catch (IOException e){
            System.err.println("Failed to send to destination in group " + to.id);
            System.err.println("Intended message: '" + this.toString() + "'");
        }
    }

    public String getGroupId(){
        return GroupUtilities.groupNameFromId(gid);
    }

    public boolean isJoin(){
        return type == MessageType.JOIN_GROUP;
    }

    public boolean isLeave(){
        return type == MessageType.LEAVE_GROUP;
    }

    public boolean isUpdate(){
        return type == MessageType.UPDATE;
    }

    public boolean isRefresh(){
        return type == MessageType.REFRESH;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.type.name());
        sb.append(": ");
        sb.append(GroupUtilities.groupNameFromId(this.gid));
        sb.append(" (");
        sb.append(this.gid);
        sb.append(")\n");

        if(this.type == MessageType.STATUS){
            sb.append("NumPeers: ");
            sb.append(numPeers);
            sb.append("\n");
        }

        //TODO: If message type == UPDATE or REFRESH append appropriate information

        return sb.toString();
    }
}
