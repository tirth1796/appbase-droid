package client;

import org.java_websocket.handshake.ServerHandshake;

public abstract class Stream{

    public  void onOpen(ServerHandshake handshakedata){} ;
    
    public void onClose(int code, String reason, boolean remote){} ;
    
    public void onError(Exception ex){}

	public abstract void onMessage(String message);
}
