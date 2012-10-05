package ndnrp.ipsrc.client;

import ndnrp.protocol.*;
import ndnrp.util.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class IPClient{
    public static final int PUBLISHER = 1;
    public static final int SUBSCRIBER = 2;
    public static final int BUF_LEN = 1024;

    private String _ip = null;
    private int _port = 0;
    private int _type = 0;
    private String _name = null;
    private HashSet<String> _subSet = null;
    private Socket _socket = null;

    private byte[] _buf = null;

    public IPClient(String ip, int port, String name, int type){
        this._ip = ip;
        this._port = port;
        this._type = type;
        this._name = name;
        this._subSet = new HashSet<String>();
        this._buf = new byte[BUF_LEN];
        if((type & SUBSCRIBER) > 0){
            this._socket = getSocket();
            listen();
        }
    }    

    private Socket getSocket(){
        try{
            System.out.println("Creating new socket");
            return new Socket(_ip, _port);
        }
        catch(UnknownHostException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public boolean isSubscribing(String pubName){
        return _subSet.contains(pubName);
    }

    public boolean subscribeBot(String pubName){
        return subscribe(pubName);
    }

    public boolean subscribe(String pubName){
        if(isSubscribing(pubName)){
            return false;
        }
        if(null == _socket){
            _socket= getSocket();
        }
        _subSet.add(pubName);
        String subMsg = Protocol.HEAVY_SUB_PREFIX + _name + "/" + pubName;
        send(_socket, subMsg, false);
        return true;
    }

    public boolean unsubscribeBot(String pubName){
        return unsubscribe(pubName);
    }

    public boolean unsubscribe(String pubName){
        if(!isSubscribing(pubName)){
            return false;
        }
        if(null == _socket){    
            _socket = getSocket();
        }
        _subSet.remove(pubName);
        String unsMsg = Protocol.HEAVY_UNSUB_PREFIX + _name + "/" + pubName;
        send(_socket, unsMsg, false);
        return true;
    }

    private void send(Socket socket, String msg, boolean close){
        if(null == socket || socket.isClosed()){
            System.out.println("Error in IPClient.send: socket is null");
            return;
        }
        IPSendThread sth = new IPSendThread(socket, msg, close);
        sth.setDaemon(true);
        sth.start();
    }

    public void listen(){
        if(null == _socket || _socket.isClosed()){
            _socket = getSocket();
        }
        String listenMsg = Protocol.HEAVY_LISTEN_PREFIX + _name;
        send(_socket, listenMsg, false);
    }

    public String receive(){
        try{
            if(null == _socket || _socket.isClosed()){
                _socket = getSocket();
            }
            InputStream in = _socket.getInputStream();
            int cnt = 0;

            cnt = in.read(_buf, 0, BUF_LEN);            
            if(cnt >= BUF_LEN){
                System.out.println("In IPClient.listen: _buf overflow");
                return null;
            }
            return new String(_buf, 0, cnt, Protocol.ENCODING);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public void post(String msg){
        Socket socket = getSocket();

        String postMsg = Protocol.HEAVY_POST_PREFIX + _name + "/" + msg;
        send(socket, postMsg, (socket != _socket));
        socket = null;
    }
}
