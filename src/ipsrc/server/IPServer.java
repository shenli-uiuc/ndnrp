package ndnrp.ipsrc.server;

import ndnrp.protocol;

import java.net.*;
import java.io.*;

public class IPServer{

    private String _url = null;
    private int _port = 0;    

    public IPServer(String url, int port){
        this._url = url;
        this._port = port;
    }

    public void start(){
        ServerSocket ss = new ServerSocket();
        InetSocketAddress addr = new InetSocketAddress(_url, _port);
        Socket socket = null;

        ss.bind(addr);

        if(ss.isBound()){
            while(true){
                socket = server.accept();
                System.out.println("Got one connection");
                IPReqHandler rh = new IPReqHandler(socket);
                rh.start();
            }
        }
        else{
            System.out.println("Server socket binding failed!");
            return;
        }
    }

    public static void main(String args[]){
        IPServer ips = new IPServer(Protocol.SERVER_IP, Protocol.SERVER_PORT);
        ips.start();
    }
}
