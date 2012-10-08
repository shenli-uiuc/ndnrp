package ndnrp.ndnsrc.sub;

import ndnrp.protocol.*;
import ndnrp.util.*;

import org.ccnx.ccn.CCNHandle;
import org.ccnx.ccn.io.CCNReader;
import org.ccnx.ccn.config.ConfigurationException;
import org.ccnx.ccn.protocol.MalformedContentNameStringException;
import org.ccnx.ccn.protocol.ContentName;
import org.ccnx.ccn.protocol.Interest;
import org.ccnx.ccn.protocol.ContentObject;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class SubscribeBotThread extends Thread{
    private CCNHandle _handle = null;
    private LinkedBlockingQueue _lbq = null;
    private CCNReader _reader = null;
    private HashSet<String> _subSet = null;
    private boolean _isRunning = false;
    private StrValidator _strValidator = null;
    private StatMonitor _statMonitor = null;
    //private FPCounter _fpc = null;

    public SubscribeBotThread(CCNHandle handle, LinkedBlockingQueue lbq, 
                HashSet<String> subSet, StatMonitor statMonitor){
        this._handle = handle;
        this._lbq = lbq;
        this._subSet = subSet;
        this._statMonitor = statMonitor;
        this._strValidator = new StrValidator();
        //this._fpc = fpc;

        try{
            this._reader = new CCNReader(_handle);
        }
        catch(ConfigurationException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }

    }

    public synchronized boolean subscribe(String name){
        /*
        // This if block is removed because we will use this method for refreshing
        if(_subSet.contains(name)){
            //already subscribing to the name
            return false;
        }
        */
        if(!_subSet.contains(name)){
            _subSet.add(name);
        }
        String fullName = Protocol.LIGHT_BOT_SUB_PREFIX + name;
        _statMonitor.reportInterest(fullName);
        SendThread st = new SendThread(fullName);
        st.setDaemon(true);
        st.start();
        return true;
    }

    // This thread will refresh the interest for publishers in _subSet
    private class RefreshThread extends Thread{
        private void refreshAll(){
            Iterator it = _subSet.iterator();

            String name = null;
            while(it.hasNext()){
                name = (String)it.next();
                subscribe(name);
            } 
        }

        public void run(){
            int refreshInterval = Math.max(2000, Protocol.HERMES_REFRESH_INTERVAL - 1000 * 60);
            try{
                while(true){
                    Thread.sleep(refreshInterval);
                    refreshAll();
                }
            }
            catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

    private class SendThread extends Thread{
        private String _name = null;

        public SendThread(String name){
            this._name = name;
        }

        public void run(){
            try{
                ContentName contentName = ContentName.fromURI(_name);
                Interest interest = new Interest(contentName);
                System.out.println("**************" + contentName.toURIString());
                _reader.hermesGet(interest, 1);
            }
            catch (IOException e) {
                System.out.println("IOException in SendThread.run: " + e.getMessage());
                e.printStackTrace();
            }
            catch (MalformedContentNameStringException e) {
                System.out.println("MalformedContentNameStringException in SendThread.run : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean unsubscribe(String name){
        if(!_subSet.contains(name)){
            return false;
        }
        _subSet.remove(name);
        return true;
    }

    public void stopAllSub(){
        _isRunning = false;
    }

    public boolean isRunning(){
        return _isRunning;
    }

    public void run(){
        MsgItem msgItem = null;
        _isRunning = true;
        int splitIndex = 0;
        while(_isRunning){
            msgItem = receive();
            if(null == msgItem){
                continue;
            }
            try{
                if(!_subSet.contains(msgItem.getPublisher())){
                    //fp, isWrong == true
                    _statMonitor.reportMsg(true);
                }
                else{
                    _statMonitor.reportMsg(false);
                }
                _lbq.put(msgItem);
            }
            catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }


    private MsgItem receive(){
        try{
            ContentName contentName = ContentName.fromURI(Protocol.LIGHT_BOT_PUB_PREFIX);
            Interest interest = new Interest(contentName);
            System.out.println("**************" + contentName.toURIString());
            //every receive waits for only 5 seconds, cause we gonna need to stop this thread in the middle of execution
            ContentObject co = _reader.hermesGet(interest, 5000);
            if(null == co){
                return null;
            }
            String ans = new String(co.content(), Protocol.ENCODING);
            String name = co.getContentName().toURIString().substring(Protocol.LIGHT_BOT_PUB_PREFIX.length());
            int splitIndex = name.indexOf("/");
            if(splitIndex > 0){
                name = name.substring(0, splitIndex);
            }
            System.out.println("Got data from " + name  + ": " + ans);
            return new MsgItem(name, _strValidator.fromValid(ans));
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
