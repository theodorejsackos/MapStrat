package model;

import network.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class SessionModel {

    private String sessionID;
    private String sessionName; // User-given name of session
    private boolean active;
    private Server host;

    private class Server extends Socket {
        public ObjectInputStream ois;
        public ObjectOutputStream oos;
        public String id;
    }

    public SessionModel(){
        active = false; // Session on local machine is always non-active (local only)

        UUID session = UUID.randomUUID();
        System.out.println(session);
        sessionID = session.toString();
    }

    public void joinSession(String sessionId){
        /* If the user is already in a session with the server, clear all drawing events before
         * withdrawing from the session (timeout will eventually occur but this is cleaner) */
        if(isActive()){
            Message.clear(sessionID).send(host);
            Message.withraw(sessionID).send(host);
            sessionID = null;
            try {
                host.close();
            } catch( IOException e) {
                System.err.println("Failed to close connection with the host");
                e.printStackTrace();
            } catch(NullPointerException e){
                System.err.println("Attempted to close non-existant session with host");
                e.printStackTrace();
            }
            host = null;
        }
        sessionID = sessionId;
        /* Connect to the host machine, share session information, begin routing all changes through the
         * network */
    }

    public boolean isActive(){
        return active;
    }
}
