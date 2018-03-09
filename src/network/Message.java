package network;

import model.DrawableObject;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Message implements Serializable {

    private MessageType type;
    private String sessionPayload;
    private List<DrawableObject> objectPayload;

    private Message(MessageType t, String session){
        this.type = t;
        this.sessionPayload = session;
    }

    public static Message handshake(String session) throws IOException {
        return new Message(MessageType.HANDSHAKE, session);
    }

    public static Message withraw(String session) {
        return new Message(MessageType.WITHDRAW, session);
    }

    public static Message clear(String session) {
        return new Message(MessageType.CLEAR, session);
    }

    public void send(Socket dest){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(dest.getOutputStream());
            oos.writeObject(this);
        }
        catch (IOException e){
            System.err.println("Failed to send a " + type + " message to client");
            e.printStackTrace();
            System.exit(1);
        }
    }


    public static Message readMessage(ObjectInputStream ois){
        try{
            return (Message) ois.readObject();
        }catch(ClassNotFoundException e){
            System.err.println("Bad datagram; not a Message object.");
            e.printStackTrace();
            System.exit(1);
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
