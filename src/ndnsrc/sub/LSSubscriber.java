package ndnrp.ndnsrc.sub;

import ndnrp.util.*;
import ndnrp.protocol.*;

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


public class LSSubscriber {

    private CCNHandle _handle = null;   
    private String _name = null;
    private HashSet<String> _subSet = null;
    private LinkedBlockingQueue<MsgItem> _lbq = null;
    private ArrayList<SubscribeThread> _threadList = null;
    private StrValidator _strValidator = null;
    private SubscribeBotThread _sbt = null;
    private StatMonitor _statMonitor = null;

    public LSSubscriber(String name, CCNHandle handle, StatMonitor statMonitor){
        this._handle = handle;
        this._name = name;
        this._statMonitor = statMonitor;
        this._subSet = new HashSet<String>();
        this._lbq = new LinkedBlockingQueue<MsgItem>();
        this._threadList = new ArrayList<SubscribeThread>();
        this._strValidator = new StrValidator();
        this._sbt = new SubscribeBotThread(_handle, _lbq, _subSet, _statMonitor);
    } 

    public synchronized boolean isSubscribing(String name){
        return _subSet.contains(name);
    }

    public synchronized boolean subscribe(String name){
        if(_subSet.contains(name)){
            //already subscribing to the name
            return false;
        }
        _subSet.add(name);
        SubscribeThread st = new SubscribeThread(name, _handle, _lbq, _statMonitor);
        _threadList.add(st);
        st.setDaemon(true);
        st.start();
        return true;
    }

    public synchronized boolean subscribeBot(String name){
        if(!_sbt.isRunning()){
            _sbt.setDaemon(true);
            _sbt.start();
        }
        return _sbt.subscribe(name);
    }

    public synchronized boolean unsubscribeBot(String name){
        return _sbt.unsubscribe(name);
    }

    //this should be blocking
    public String receive(){
        MsgItem msgItem = null;
        try{
            msgItem = _lbq.take();
        }
        catch(InterruptedException ex){
            ex.printStackTrace();

        }
        return msgItem.getPublisher() + ": " + msgItem.getMsg();
    }


    //this is non-blocking, post is done by a separate thread
    public void post(String msg){
        PostThread pt = new PostThread(Protocol.LIGHT_POST_PREFIX + _name, _strValidator.toValid(msg), _handle);
        pt.setDaemon(true);
        pt.start();
    }

    public void botPost(String msg){
        PostThread bpt = new PostThread(Protocol.LIGHT_BOT_POST_PREFIX + _name, msg, _handle);
        bpt.setDaemon(true);
        bpt.start();
    }


    public static void main(String argv[]){
        try{
            StatMonitor statMonitor = new StatMonitor();
            LSSubscriber subscriber = new LSSubscriber("Bob", CCNHandle.open(), statMonitor);
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
