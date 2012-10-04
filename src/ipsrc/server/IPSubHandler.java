package ndnrp.ipsrc.server;

import ndnrp.protocol.*;
import ndnrp.util.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class IPSubHandler extends Thread{
    public static final int BUF_LEN = 1024;
    private Socket _socket = null;
    private Hashtable<Stirng, String> _followMap = null;
    private byte[] _buf = null;

    public IPSubHandler(Socket socket, Hashtable<String, String> followMap){
        this._socket = socket;
        this._followMap = followMap;
        this._buf = new byte[BUF_LEN];
    }


    public void run(){
        InputStream in = socket.getInputStream();
        int cnt = 0;
        String msg = null;
        while(true){
            cnt = in.receive(_buf, 0, BUF_LEN);
            msg = new String(_buf, 0, cnt, Protocol.ENCODING);
            processSub(msg);
        }

    }

    private synchronized String processSub(String msg){
        String data = msg.substring(Protocol.HEAVY_SUB_PREFIX.length());
        int splitIndex = data.indexOf("/");
        String sub = data.substring(0, splitIndex);
        String pub = data.substring(splitIndex + 1, data.length());

        System.out.println("Subscribing : " + sub + ", " + pub);

        HashSet<String> subSet = _followMap.get(pub);
        if(null == subSet){
            subSet = new HashSet<String>();
            _followMap.put(pub, subSet);
        }
        if(subSet.contains(sub)){
            return Protocol.SUB_ALREADY;
        }
        subSet.add(sub);
        return Protocol.SUCCESS;

    }

}
