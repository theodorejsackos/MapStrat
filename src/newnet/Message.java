package newnet;

import model.DrawableObject;
import util.GroupUtilities;
import version.Version;

import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
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
    public final MessageType type;
    public final long        gid;

    /* State information, may be null and unused */
    private List<DrawableObject> state;     //  For updating a client's state
    private DrawableObject       newStroke; //  For adding to the server's state
    private int                  numPeers;  //  For STATUS update messages (session/group information).
    private String               version;   //  For STATUS update to share the current server version with clients. This
                                            // allows clients to be informed when their version is out of date and needs
                                            // to be re-downloaded

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
        m.version  = Version.VERSION;
        return m;
    }

    public static Message refresh(String group, List<DrawableObject> state){
        Message m = new Message(MessageType.REFRESH, GroupUtilities.groupIdFromName(group));
        m.state = state;
        return m;
    }

    public static Message update(String group, DrawableObject o){
        Message m = new Message(MessageType.UPDATE, GroupUtilities.groupIdFromName(group));
        m.newStroke = o;
        return m;
    }

    public static Message get(Host from) throws SocketException {
        try{
            Message m = (Message) from.ois.readObject();
            System.out.print("Message.get() from " + from + ":\n\t");
            System.out.println(m);
            System.out.flush();
            return m;
        } catch (IOException | ClassNotFoundException e){
            System.err.println(e.getClass() + ": in get()");
            return null;
        }
    }

    public void send(Host to){
        try{
            System.out.println("Message.send() to " + to + ":\n\t" + this);
            System.out.flush();
            to.oos.writeObject(this);
            to.oos.flush();
        } catch (IOException e){
            System.err.println("Failed to send to destination in group " + to.id);
            System.err.println("Intended message: '" + this.toString() + "'");
        }
    }

    public String getGroupId(){
        return GroupUtilities.groupNameFromId(gid);
    }

    public DrawableObject getStroke(){
        assert(type == MessageType.UPDATE);
        assert(newStroke != null);
        return newStroke;
    }

    public int getNumPeers(){
        return numPeers;
    }


    public String getVersion() {
        return version;
    }

    public List<DrawableObject> getState() {
        return state;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.type.name());
        sb.append(": group ");
        sb.append(GroupUtilities.groupNameFromId(this.gid));
        sb.append(" (");
        sb.append(this.gid);
        sb.append(")\n");

        switch(this.type){
            case STATUS:
                sb.append("\tNumPeers: ");
                sb.append(numPeers);
                sb.append("\n");
                break;

            case UPDATE:
                sb.append("\tnewStroke: ");
                sb.append(newStroke);
                sb.append("\n");
                break;

            case REFRESH:
                sb.append("\tState: ");
                sb.append(state.size());
                sb.append(" drawable objects. [");
                for(int i = 0; i < Math.min(3, state.size()); i++){
                    sb.append(state.get(i));
                    sb.append(", ");
                }
                sb.append("]\n");
                break;

            case JOIN_GROUP:  // No extra info
            case LEAVE_GROUP:
            default:
                break;
        }

        return sb.toString();
    }
}
