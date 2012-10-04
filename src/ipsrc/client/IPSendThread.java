package ndnrp.ipsrc.client;

import ndnrp.protocol.*;
import ndnrp.util.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class IPSendThread extends Thread{
    private Socket _socket = null;
    private String _msg = null;
    private boolean _close = false;

    public IPSendThread(Socket socket, String msg, boolean close){
        this._socket = socket;
        this._msg = msg;
        this._close = close;
    }

    public void run(){
        try{
            OutputStream out = _socket.getOutputStream();
            out.write(_msg.getBytes(Protocol.ENCODING));
            out.flush();
            if(_close){
                out.close();
                _socket.close();
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

}
