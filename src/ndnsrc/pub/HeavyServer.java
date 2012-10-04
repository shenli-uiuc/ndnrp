package ndnrp.ndnsrc.pub;

import ndnrp.util.*;
import ndnrp.protocol.*;

import java.util.*;
import java.lang.*;
import org.ccnx.ccn.io.CCNVersionedOutputStream;

import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SignatureException;

import org.ccnx.ccn.CCNFilterListener;
import org.ccnx.ccn.CCNHandle;
import org.ccnx.ccn.config.ConfigurationException;
import org.ccnx.ccn.impl.support.Log;
import org.ccnx.ccn.io.*;
import org.ccnx.ccn.profiles.CommandMarker;
import org.ccnx.ccn.profiles.SegmentationProfile;
import org.ccnx.ccn.profiles.VersioningProfile;
import org.ccnx.ccn.profiles.metadata.MetadataProfile;
import org.ccnx.ccn.profiles.nameenum.NameEnumerationResponse;
import org.ccnx.ccn.profiles.nameenum.NameEnumerationResponse.NameEnumerationResponseMessage;
import org.ccnx.ccn.profiles.nameenum.NameEnumerationResponse.NameEnumerationResponseMessage.NameEnumerationResponseMessageObject;
import org.ccnx.ccn.profiles.security.KeyProfile;
import org.ccnx.ccn.protocol.CCNTime;
import org.ccnx.ccn.protocol.ContentName;
import org.ccnx.ccn.protocol.Exclude;
import org.ccnx.ccn.protocol.ExcludeComponent;
import org.ccnx.ccn.protocol.Interest;
import org.ccnx.ccn.protocol.MalformedContentNameStringException;

public class HeavyServer implements CCNFilterListener{
    
    private Interest _interest = null;
    private CCNHandle _handle = null;
    private Publisher _pub = null;
    private CCNWriter _writer = null;

    private Hashtable<String, HashSet<String>> _pubSet = null; 

    public HeavyServer(){

        _pubSet = new Hashtable<String, HashSet<String>>();
        try{
            _handle = CCNHandle.open();
            _pub = new Publisher(CCNHandle.open());
            _writer = new CCNWriter(_handle);
        }
        catch(ConfigurationException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }


    public void start(){
        // All we have to do is say that we're listening on our main prefix.
        try{
            _handle.registerFilter(ContentName.fromURI(Protocol.HEAVY_POST_PREFIX), this);
            _handle.registerFilter(ContentName.fromURI(Protocol.HEAVY_SUB_PREFIX), this);
        }
        catch(MalformedContentNameStringException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private synchronized String enrollSub(String publisher, String subscriber){
        HashSet<String> subSet = _pubSet.get(publisher);
        if(null == subSet){
            subSet = new HashSet<String> ();
            subSet.add(subscriber);
            _pubSet.put(publisher, subSet);
            return Protocol.SUCCESS;
        }
        else if(subSet.contains(subscriber)){
            return Protocol.SUB_ALREADY;
        }
        else{
            subSet.add(subscriber);
            return Protocol.SUCCESS;
        }
    }

    private String propagatePost(String publisher, String msg){
        HashSet<String> subSet = _pubSet.get(publisher);
        if(null == subSet){
            return Protocol.PUB_NOT_EXIST;
        }
        else{
            PropagatePostThread ppt = new PropagatePostThread(subSet, publisher, msg);
            ppt.start();
            return Protocol.SUCCESS;
        }
    }

    class PropagatePostThread extends Thread{
        private HashSet<String> _subSet = null;
        private String _publisher = null;
        private String _msg = null;

        public PropagatePostThread(HashSet<String> subSet, String publisher, String msg){
            this._subSet = subSet;
            this._publisher = publisher;
            this._msg = msg;
        }

        public void run(){
            String sub = null;
            Iterator it = _subSet.iterator();
            while(it.hasNext()){
                sub = (String)it.next();
                _pub.publish(Protocol.HEAVY_PUB_PREFIX + sub, _publisher + ": " + _msg);
            }
        }
    }

    private String processPost(Interest interest){
        String strMsg = interest.name().toURIString().substring(Protocol.HEAVY_POST_PREFIX.length());
        int splitIndex = strMsg.indexOf("/");
        if(splitIndex < 0 || splitIndex + 1 == strMsg.length()){
            return Protocol.MSG_FORMAT_ERROR;
        }
        String usr = strMsg.substring(0, splitIndex);
        String msg = strMsg.substring(splitIndex + 1, strMsg.length());
        return propagatePost(usr, msg);
    }

    private String processSub(Interest interest){
        String strMsg = interest.name().toURIString().substring(Protocol.HEAVY_SUB_PREFIX.length());
        int splitIndex = strMsg.indexOf("/");
        String sub = strMsg.substring(0, splitIndex);
        String pub = strMsg.substring(splitIndex + 1, strMsg.length());
        return enrollSub(pub, sub); 
    }


    public boolean handleInterest(Interest interest) {
        System.out.println("===========================received Interest : " + interest.name().toURIString() + "\n");

        String msg = interest.name().toURIString();
        if (SegmentationProfile.isSegment(interest.name()) && !SegmentationProfile.isFirstSegment(interest.name())) {
            System.out.println("Got an interest for something other than a first segment, ignoring : " + msg);
            return false;
        } 
        else if (MetadataProfile.isHeader(interest.name())) {
            System.out.println("Got an interest for the first segment of the header, ignoring : " + msg);
            return false;
        }     
    
        String res = null;
        if(msg.substring(0, Protocol.HEAVY_POST_PREFIX.length()).equals(Protocol.HEAVY_POST_PREFIX)){
            //someone is posting a new tweet
            res = processPost(interest); 
        }
        else if(msg.substring(0, Protocol.HEAVY_SUB_PREFIX.length()).equals(Protocol.HEAVY_SUB_PREFIX)){
            //someone is subscribing to a publisher
            res = processSub(interest);
        }
        else{
            //unrecognised interest
            res = Protocol.SERVICE_NOT_EXIST;
        }
        
        
        
        try{
            _writer.addOutstandingInterest(interest);
            _writer.hermesPut(interest.name(), res.getBytes(Protocol.ENCODING), 1, interest);
        }
        catch(SignatureException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        //split string
        return true;
    }

    /**
     * Turn off everything.
     * @throws IOException 
     */
    public void shutdown() throws IOException {
        if (null != _handle) {
            try{
                _handle.unregisterFilter(ContentName.fromURI(Protocol.HEAVY_POST_PREFIX), this);
                _handle.unregisterFilter(ContentName.fromURI(Protocol.HEAVY_SUB_PREFIX), this);
                System.out.println("CCNQueryListener Closed!\n");
            }
            catch(MalformedContentNameStringException ex){
                ex.printStackTrace();
            }
        }
    }


    public static void main(String args[]){
        HeavyServer server = new HeavyServer();
        server.start();
    }

}


