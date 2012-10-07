package ndnrp.ndnsrc.sub;

import ndnrp.util.*;
import ndnrp.protocol.*;

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
    private String _fullName = null;
    private CCNHandle _handle = null;
    private LinkedBlockingQueue _lbq = null;
    private CCNReader _reader = null;
    private boolean _isRunning = false;
    private StrValidator _strValidator = null;
    private StatMonitor _statMonitor = null;

    public SubscribeThread(String name, CCNHandle handle, LinkedBlockingQueue lbq, StatMonitor statMonitor){
        this._name = name;
        this._fullName = Protocol.LIGHT_PUB_PREFIX + _name;
        this._handle = handle;
        this._lbq = lbq;
        this._statMonitor = statMonitor;
        this._strValidator = new StrValidator();

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
        _statMonitor.reportInterest(_fullName);
        while(_isRunning){
            curMsg = receive();
            if(null == curMsg){
                continue;
            }
            try{
                _lbq.put(new MsgItem(_name, _strValidator.fromValid(curMsg)));
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
            ContentName contentName = ContentName.fromURI(_fullName);
            Interest interest = new Interest(contentName);
            System.out.println("**************" + contentName.toURIString());
            //every receive waits for only 5 seconds, cause we gonna need to stop this thread in the middle of execution
            ContentObject co = _reader.hermesGet(interest, 5000);
            if(null == co){
                return null;
            }
            String ans = new String(co.content(), Protocol.ENCODING);
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
