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

import java.util.concurrent.*;
import java.io.IOException;

public class SubscribeThread extends Thread{

    private String _name = null;
    private CCNHandle _handle = null;
    private LinkedBlockingQueue _lbq = null;
    private CCNReader _reader = null;
    private boolean _isRunning = false;

    public SubscribeThread(String name, CCNHandle handle, LinkedBlockingQueue lbq){
        this._name = name;
        this._handle = handle;
        this._lbq = lbq;
        try{
            this._reader = new CCNReader(handle);
        }
        catch(ConfigurationException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void stopAllSub(){
        _isRunning = false;
    }

    public void run(){
        String curMsg = null;
        _isRunning = true;
        while(_isRunning){
            curMsg = receive();
            if(null == curMsg){
                continue;
            }
            try{
                _lbq.put(new MsgItem(_name, curMsg));
            }
            catch(InterruptedException ex){
                ex.printStackTrace();
            }
            try{
                Thread.sleep(1500);
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    

    private String receive(){
        try{
            ContentName contentName = ContentName.fromURI(Protocol.LIGHT_PUB_PREFIX + _name);
            Interest interest = new Interest(contentName);
            System.out.println("**************" + contentName.toURIString());
            //every receive waits for only 5 seconds, cause we gonna need to stop this thread in the middle of execution
            ContentObject co = _reader.get(interest, 5000);
            if(null == co){
                return null;
            }
            String ans = new String(co.content());
            System.out.println("Got data : " + ans);
            return ans;
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
