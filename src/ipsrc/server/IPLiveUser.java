package ndnrp.ipsrc.server;

import java.net.*;

public class IPLiveUser{
    private String _name = null;
    private Socket _socket = null;
    private IPSubHandler _thread = null;

    public IPLiveUser(String name, Socket socket, IPSubHandler thread ){
        this._name = name;
        this._socket = socket;
        this._thread = thread;
    }

    public String getName(){
        return _name;
    }

    public Socket getSocket(){
        return _socket;
    }

    public IPSubHandler getHandler(){
        return _thread;
    }

    public void setSocket(Socket socket){
        _socket = socket;
    }

    public void setHandler(IPSubHandler thread){
        _thread = thread;
    }
}
