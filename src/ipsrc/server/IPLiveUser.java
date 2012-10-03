package ndnrp.ipsrc.server;

import java.net.*;

public class IPLiveUser{
    private String _name = null;
    private Socket _socket = null;

    public IPLiveUser(String name, Socket socket){
        this._name = name;
        this._socket = socket;
    }

    public String getName(){
        return _name;
    }

    public Socket getSocket(){
        return _socket;
    }

    public void setSocket(Socket socket){
        _socket = socket;
    }
}