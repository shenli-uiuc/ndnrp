package ndnrp.ipsrc.server;

import ndnrp.protocol.*;
import ndnrp.util.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class IPSubHandler extends Thread{
    public static final int BUF_LEN = 1024;
    private Socket _socket = null;
    private Hashtable<String, HashSet<String> > _followMap = null;
    private byte[] _buf = null;

    public IPSubHandler(Socket socket, Hashtable<String, HashSet<String> > followMap){
        this._socket = socket;
        this._followMap = followMap;
        this._buf = new byte[BUF_LEN];
    }


    public void run(){
        try{
            InputStream in = _socket.getInputStream();
            int cnt = 0;
            String msg = null;
            while(true){
                cnt = in.read(_buf, 0, BUF_LEN);
                if(cnt >= BUF_LEN){
                    System.out.println("In IPSubHandler.run: buffer overflow");
                    return;
                }
                msg = new String(_buf, 0, cnt, Protocol.ENCODING);
                if(msg.substring(0, Protocol.HEAVY_SUB_PREFIX.length()).equals(Protocol.HEAVY_SUB_PREFIX)){
                    //someone is subscribing to a publisher
                    System.out.println("Got Sub: " + msg);
                    processSub(msg);
                }
                else if(msg.substring(0, Protocol.HEAVY_UNSUB_PREFIX.length()).equals(Protocol.HEAVY_UNSUB_PREFIX)){
                    System.out.println("Got unSub: " + msg);
                    processUnsub(msg);
                }
            }
        }
        catch(UnsupportedEncodingException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private String processUnsub(String msg){
        String data = msg.substring(Protocol.HEAVY_UNSUB_PREFIX.length());
        int splitIndex = data.indexOf("/");
        String sub = data.substring(0, splitIndex);
        String pub = data.substring(splitIndex + 1, data.length());

        HashSet<String> subSet = _followMap.get(pub);
        if(null == subSet){
            return Protocol.PUB_NOT_EXIST;
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
