package ccnps.sub;

import ccnps.util.*;
import ccnps.protocol.*;

import org.ccnx.ccn.CCNFilterListener;
import org.ccnx.ccn.CCNHandle;
import org.ccnx.ccn.config.ConfigurationException;
import org.ccnx.ccn.impl.support.Log;
import org.ccnx.ccn.io.CCNReader;
import org.ccnx.ccn.io.CCNWriter;
import org.ccnx.ccn.protocol.CCNTime;
import org.ccnx.ccn.protocol.ContentName;
import org.ccnx.ccn.protocol.Exclude;
import org.ccnx.ccn.protocol.ExcludeComponent;
import org.ccnx.ccn.protocol.Interest;
import org.ccnx.ccn.protocol.ContentObject;
import org.ccnx.ccn.protocol.MalformedContentNameStringException;

import java.io.IOException;

public class PostThread extends Thread{

    private String _name = null;
    private String _msg = null;
    private CCNHandle _handle = null;

    public PostThread(String name, String msg, CCNHandle handle){
        this._name = name;
        this._msg = msg;
        this._handle = handle;
    }
    
    public void run(){
        post();
    }

    //encode the message into the interest
    public String post(){
        try{
            ContentName contentName = ContentName.fromURI(_name + "/" + _msg);
            Interest interest = new Interest(contentName);
            System.out.println("**************" + contentName.toURIString());
            CCNReader reader = new CCNReader(_handle);
            ContentObject co = reader.get(interest, 20000);
            if(null == co){
                System.out.println("Post interest timed out, server is not responding!");
                return Protocol.TIME_OUT;
            }
            String ans = new String(co.content());
            System.out.println("In Post - Got data : " + ans);
            return ans;
        }
        catch (ConfigurationException e) {
            System.out.println("ConfigurationException in CCNQuerySender-sendQuery: " + e.getMessage());
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("IOException in CCNQuerySender-sendQuery: " + e.getMessage());
            e.printStackTrace();
        }
        catch (MalformedContentNameStringException e) {
            System.out.println("MalformedContentNameStringException in CCNQuerySender-sendQuery : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

}
