package ndnrp.ipsrc.test;

import ndnrp.ipsrc.server.*;
import ndnrp.ipsrc.client.*;
import ndnrp.protocol.*;
import ndnrp.util.*;

import java.net.*;
import java.io.*;

public class TestIPPub{
    public static void main(String args[]){
        IPClient ipc = new IPClient(Protocol.SERVER_IP, Protocol.SERVER_PORT,
                "Alice", IPClient.PUBLISHER);
        try{
            int i = 0;
            while(true){
                String msg = "msg from Alice id = " + i;
                ipc.post(msg);
                System.out.println("Alice Published: " + msg);
                Thread.sleep(2000);
                ++i;
            }
        }
        catch(InterruptedException ex){
            ex.printStackTrace();
        }   
    }
}
