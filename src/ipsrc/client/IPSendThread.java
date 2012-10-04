package ndnrp.ipsrc.client;

import ndnrp.protocol.*;
import ndnrp.util.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class IPSendThread extends Thread{
    private Socket _socket = null;
    private String _msg = null;

    public IPSendThread(Socket socket, String msg){
        this._socket = socket;
        this._msg = msg;
    }

    public void run(){
        try{
            OutputStream out = _socket.getOutputStream();
            out.write(_msg.getBytes(Protocol.ENCODING));
            out.flush();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

}
