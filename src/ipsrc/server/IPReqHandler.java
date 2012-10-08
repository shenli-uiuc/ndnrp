package ndnrp.ipsrc.server;

import ndnrp.protocol.*;
import ndnrp.util.*;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Collections.*;

public class IPReqHandler extends Thread{
    private static final int BUF_LEN = 1024;

    private Socket _socket = null;
    private byte[] _buf = null;
    private Hashtable<String, Set > _followMap = null;
    private Hashtable<String, IPLiveUser> _userMap = null; 

    public IPReqHandler(Socket socket, 
                        Hashtable<String, Set >followMap, 
                        Hashtable<String, IPLiveUser> userMap){
        this._socket = socket;
        this._followMap = followMap;
        this._userMap = userMap;
        this._buf = new byte[BUF_LEN];
    }    

    //TODO: the processListen method need to invoke the IPSubHandler thread
    private String processListen(String msg){
        String data = msg.substring(Protocol.HEAVY_LISTEN_PREFIX.length());
        IPLiveUser lu = null;
        if(null != _userMap.get(data)){
            lu = _userMap.get(data);
            if(lu.getSocket() == _socket){
                return Protocol.LISTEN_ALREADY;
            }
            else{
                if(!lu.getSocket().isClosed()){
                    try{
                        lu.getHandler().stop();
                        lu.getSocket().close();
                    }
                    catch(IOException ex){
                        ex.printStackTrace();
                    }
                }
                lu.setSocket(_socket);
                IPSubHandler thread = new IPSubHandler(_socket, _followMap);
                thread.setDaemon(true);
                lu.setHandler(thread);
                thread.start();
                //I only keep the socket, the server push is on-demand
                //TODO: close the old socket
                return Protocol.SUB_SOCK_UPDATE;
            }
        }
        else{
            //create a not IPLiveUser
            IPSubHandler thread = new IPSubHandler(_socket, _followMap);
            thread.setDaemon(true);
            thread.start();
            lu = new IPLiveUser(data, _socket, thread);
            _userMap.put(data, lu);
            return Protocol.SUCCESS;
        }
    }

    private String processUnsub(String msg){
        String data = msg.substring(Protocol.HEAVY_UNSUB_PREFIX.length());
        int splitIndex = data.indexOf("/");
        String sub = data.substring(0, splitIndex);
        String pub = data.substring(splitIndex + 1, data.length());

        Set subSet = _followMap.get(pub);
        if(null == subSet){
            return Protocol.PUB_NOT_EXIST;
        }        
        else if(subSet.contains(sub)){
            //do unsubscribe
            subSet = Collections.synchronizedSet(subSet);
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

        Set subSet = _followMap.get(pub);
        try{
            if(null == subSet){
                subSet = Collections.synchronizedSet(new HashSet<String>());
                _followMap.put(pub, subSet);
                _socket.close();
                _socket = null;
                return Protocol.SUCCESS;
            }
            else{
                subSet = Collections.synchronizedSet(subSet);
                Iterator it = subSet.iterator();
                String outMsg = pub + ": " + postMsg;
                String sub = null;
                Socket socket = null;
                while(it.hasNext()){
                    sub = (String)it.next();
                    socket = (Socket)_userMap.get(sub).getSocket();
                    IPMsgSender.send(socket, outMsg); 
                }
                _socket.close();
                _socket = null;
                return Protocol.SUCCESS;
            }
        }
        catch(IOException ex){
            return Protocol.ERROR;
        }
    }

    public void run(){
        InputStream in = null;
        int cnt = 0;
        try{
            in = _socket.getInputStream();
            cnt = in.read(_buf, 0, BUF_LEN);
            if(cnt >= BUF_LEN){
                System.out.println("In IPReqHandler.run: _buf overflow");
                return;
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
            return;
        }

        try{
            String msg = new String(_buf, 0, cnt, Protocol.ENCODING);
            String res = null;
            if(msg.substring(0, Protocol.HEAVY_POST_PREFIX.length()).equals(Protocol.HEAVY_POST_PREFIX)){
                //someone is posting a new tweet
                System.out.println("Got Post: " + msg);
                res = processPost(msg); 
            }
            else if(msg.substring(0, Protocol.HEAVY_LISTEN_PREFIX.length()).equals(Protocol.HEAVY_LISTEN_PREFIX)){
                System.out.println("Got listen: " + msg);
                res = processListen(msg);
            }
            else{
                //unrecognised interest
                System.out.println("Got unrecognised msg: " + msg);
                res = Protocol.SERVICE_NOT_EXIST;
            }
        }   
        catch(UnsupportedEncodingException ex){
            ex.printStackTrace();
        }
    }
}
