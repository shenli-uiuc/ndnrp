package ndnrp.ipsrc.server;

import ndnrp.protocol.*;
import ndnrp.util.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class IPReqHandler extends Thread{
    private static final int BUF_LEN = 1024;

    private Socket _socket = null;
    private byte[] _buf = null;
    private Hashtable<String, HashSet<String> > _followMap = null;
    private Hashtable<String, IPLiveUser> _socketMap = null; 

    public IPReqHandler(Socket socket, 
                        Hashtable<String, HashSet<String> >followMap, 
                        Hashtable<String, IPLiveUser> socketMap){
        this._socket = socket;
        this._followMap = followMap;
        this._socketMap = socketMap;
        this._buf = new byte[BUF_LEN];
    }    

    private String processListen(String msg){
        String data = msg.substring(Protocol.HEAVY_LISTEN_PREFIX.length());
        IPLiveUser lu = null;
        if(null != _socketMap.get(data)){
            lu = _socketMap.get(data);
            if(lu.getSocket() == _socket){
                return Protocol.LISTEN_ALREADY;
            }
            else{
                lu.setSocket(_socket);
                //I only keep the socket, the server push is on-demand
                return Protocol.SUB_SOCK_UPDATE;
            }
        }
        else{
            //create a not IPLiveUser
            lu = new IPLiveUser(data, _socket);
            _socketMap.put(data, lu);
            return Protocol.SUCCESS;
        }
    }

    private String processSub(String msg){
        String data = msg.substring(Protocol.HEAVY_SUB_PREFIX.length());
        int splitIndex = data.indexOf("/");
        String sub = data.substring(0, splitIndex);
        String pub = data.substring(splitIndex + 1, data.length());

        Hashset<String> subSet = _followMap.get(pub);
        if(null == subSet){
            subSet = new Hashset<String>();
            _followMap.put(pub, subSet);
        }
        if(subSet.contains(sub)){
            return Protocol.SUB_ALREADY;
        }
        subSet.add(sub); 
        return Protocol.SUCCESS; 

    }

    private String processUnsub(String msg){
        String data = msg.substring(Protocol.HEAVY_UNSUB_PREFIX.length());
        int splitIndex = data.indexOf("/");
        String sub = data.substring(0, splitIndex);
        String pub = data.substring(splitIndex + 1, data.length());

        Hashset<String> subSet = _followMap.get(pub);
        if(null == subSet){
            return Protocl.PUB_NOT_EXIST;
        }        
        else if(subSet.contains(sub)){
            //do unsubscribe
            subSet.remove(sub);
            return Protocol.SUCCESS;
        }
        else{
            return Protocol.SUB_NOT_EXIST;
        }
    }

    // need to share some outgoing class to make it a bottleneck
    private synchronized String processPost(String msg){
        String data = msg.substring(Protocol.HEAVY_POST_PREFIX.length());
        int splitIndex = data.indexOf("/");
        String pub = data.substring(0, splitIndex);
        String postMsg = data.substring(splitIndex + 1, data.length());

        Hashset<String> subSet = _followMap.get(pub);
        if(null == subSet){
            subSet = new Hashset<String>();
            _followMap.put(pub, subSet);
            return SUCCESS;
        }
        //TODO: logic not complete
        
        return null;
    }

    public void run(){
        InputStream in = _socket.getInputStream();      
        int cnt = 0, off = 0;
        cnt = in.read(buf, 0, BUF_LEN);
        off += cnt;
        while(cnt > 0 && off < BUF_LEN){
            cnt = in.read(buf, off, BUF_LEN - off);
            off += cnt;
        }  
        if(off >= BUF_LEN){
            System.out.println("The buffer is not large enough!");
            return;
        }

        String msg = new String(buf);
        String res = null;
        if(msg.substring(0, Protocol.HEAVY_POST_PREFIX.length()).equals(Protocol.HEAVY_POST_PREFIX)){
            //someone is posting a new tweet
            res = processPost(msg); 
        }
        else if(msg.substring(0, Protocol.HEAVY_SUB_PREFIX.length()).equals(Protocol.HEAVY_SUB_PREFIX)){
            //someone is subscribing to a publisher
            res = processSub(msg);
        }
        else if(msg.substring(0, Protocol.HEAVY_LISTEN_PREFIX.length()).equals(Protocol.HEAVY_SUB_PREFIX)){
            res = processListen(msg);
        }
        else{
            //unrecognised interest
            res = Protocol.SERVICE_NOT_EXIST;
        }
    }
}
