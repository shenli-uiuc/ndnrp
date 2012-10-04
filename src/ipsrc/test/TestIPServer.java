package ndnrp.ipsrc.test;

import ndnrp.ipsrc.server.*;
import ndnrp.ipsrc.client.*;
import ndnrp.protocol.*;
import ndnrp.util.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class TestIPServer{
    public static void main(String [] args){
        IPServer ips = new IPServer(Protocol.SERVER_IP, Protocol.SERVER_PORT);
        ips.start();
    }
}
