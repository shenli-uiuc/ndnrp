package ccnps.sub;

import ccnps.util.*;
import ccnps.protocol.*;

import java.util.*;
import java.sql.Timestamp;
import java.util.concurrent.*;

import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

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

import org.ccnx.ccn.utils.CommonSecurity;


public class HSSubscriber {

    private CCNHandle _handle = null;   
    private String _name = null;
    private HashSet<String> _subSet = null;
    private LinkedBlockingQueue<MsgItem> _lbq = null;
    private ArrayList<SubscribeThread> _threadList = null;
    private CCNReader _subReader = null;
    private CCNReader _recReader = null;
    private StrValidator _strValidator = null;

    public HSSubscriber(String name, CCNHandle handle){
        this._handle = handle;
        this._name = name;
        this._subSet = new HashSet<String>();
        this._lbq = new LinkedBlockingQueue<MsgItem>();
        this._threadList = new ArrayList<SubscribeThread>();
        this._strValidator = new StrValidator();        

        try{
            this._subReader = new CCNReader(this._handle);
            this._recReader = new CCNReader(this._handle);
        }
        catch(ConfigurationException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    } 

    public synchronized boolean subscribe(String pubName){
        if(_subSet.contains(pubName)){
            //already subscribing to the name
            return false;
        }
        _subSet.add(pubName);

        try{
            ContentName contentName = ContentName.fromURI(Protocol.HEAVY_SUB_PREFIX +  _name + "/" + pubName);
            Interest interest = new Interest(contentName);
            System.out.println("**************" + contentName.toURIString());
            //every receive waits for only 5 seconds, cause we gonna need to stop this thread in the middle of execution
            ContentObject co = _subReader.get(interest, 20000);
            if(null == co){
                System.out.println("Subscribe interest time out. The HSServer is not responding!");
                return false;
            }
            String ans = new String(co.content());
            System.out.println("Got data In subscribe : " + ans);
            if(ans.equals(Protocol.SUCCESS))
                return true;
        }
        catch(MalformedContentNameStringException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }

        return false;
    }

    //this should be blocking
    public String receive(){
        try{
            Thread.sleep(1000);
            while(true){
                ContentName contentName = ContentName.fromURI(Protocol.HEAVY_PUB_PREFIX + _name);
                Interest interest = new Interest(contentName);
                System.out.println("***************" + contentName.toURIString());
                ContentObject co = _recReader.get(interest, 5000); 
                if(null == co)
                    continue;
                return _strValidator.fromValid(new String(co.content()));   
            }
        }
        catch(MalformedContentNameStringException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        catch(InterruptedException ex){
            ex.printStackTrace();
        }
        return null;
    }


    //this is non-blocking, post is done by a separate thread
    public void post(String msg){
        PostThread pt = new PostThread(Protocol.HEAVY_POST_PREFIX + _name, _strValidator.toValid(msg), _handle);
        pt.start();
    }



    public static void main(String argv[]){
        try{
            HSSubscriber subscriber = new HSSubscriber("Bob", CCNHandle.open());
            subscriber.subscribe("Alice");
            while(true){
                System.out.println(subscriber.receive());
            }
        }
        catch(ConfigurationException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }


}
