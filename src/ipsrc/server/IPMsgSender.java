package ndnrp.ipsrc.server;

import ndnrp.protocol.*;
import ndnrp.util.*;

import java.net.*;
import java.io.*;

public class IPMsgSender{

    public static synchronized String send(Socket socket, String msg){
        try{
            Thread.sleep(Protocol.SERVER_DELAY);
            OutputStream out = socket.getOutputStream();
            out.write(msg.getBytes(Protocol.ENCODING)); 
            out.flush();
            //out.close();
            return Protocol.SUCCESS;
        }
        catch(UnsupportedEncodingException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        catch(InterruptedException ex){
            ex.printStackTrace();
        }
        return Protocol.ERROR;
    }

}
