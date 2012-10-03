package ndnrp.ipsrc.client;

import ndnrp.protocol.*;
import ndnrp.util.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class IPClient{
    public static final int PUBLISHER = 1 << 0;
    public static final int SUBSCRIBER = 1 << 1;

    private String _name = null;
    private HashSet<String> _subSet = null;
    private Socket _socket = null;

    public IPClient(String name, int type){
        this._name = name;
        this._subSet = new HashSet<String>();
        if(type | SUBSCRIBER){
            _socket = new Socket(Protocol.SERVER_IP, Protocol.SERVER_PORT);
        }
    }    


    public synchronized boolean subscribe(String pubName){
        return true;
    }

    public String receive(){
        return null;
    }

    public void post(String msg){
    }
}
