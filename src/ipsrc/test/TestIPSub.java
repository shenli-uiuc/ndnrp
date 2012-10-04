package ndnrp.ipsrc.test;

import ndnrp.protocol.*;
import ndnrp.util.*;
import ndnrp.ipsrc.client.*;
import ndnrp.ipsrc.server.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class TestIPSub{
    public static void main(String args[]){
        IPClient ipc = new IPClient(Protocol.SERVER_IP, Protocol.SERVER_PORT,
                                    "Bob", IPClient.SUBSCRIBER);

        ipc.subscribe("Alice");
        System.out.println("start receiving data: ");
        while(true){
            System.out.println("Got Data : " + ipc.receive());
        } 
    }
}
