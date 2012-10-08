package ndnrp.ipsrc.server;

import ndnrp.protocol.*;
import ndnrp.util.*;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Collections.*;

public class IPServer{

    private String _url = null;
    private int _port = 0;    
    private Hashtable<String, Set > _followMap = null;
    private Hashtable<String, IPLiveUser> _userMap = null;

    public IPServer(String url, int port){
        this._url = url;
        this._port = port;
        this._followMap = new Hashtable<String, Set >();
        this._userMap = new Hashtable<String, IPLiveUser>();
    }

    public void start(){
        ServerSocket ss = null;
        InetSocketAddress addr = new InetSocketAddress(_url, _port);
        Socket socket = null;

        try{
            ss = new ServerSocket();
            ss.bind(addr);

            if(ss.isBound()){
                System.out.println("IPServer running");
                while(true){
                    socket = ss.accept();
                    System.out.println("Got one connection");
                    IPReqHandler rh = new IPReqHandler(socket, _followMap, _userMap);
                    rh.setDaemon(true);
                    rh.start();
                }
            }
            else{
                System.out.println("Server socket binding failed!");
                return;
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String args[]){
        IPServer ips = new IPServer(Protocol.SERVER_IP, Protocol.SERVER_PORT);
        ips.start();
    }
}
