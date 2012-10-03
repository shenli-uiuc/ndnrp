package ccnps.pub;

import ccnps.protocol.*;
import ccnps.util.*;

import java.util.*;
import java.lang.*;
import org.ccnx.ccn.io.CCNVersionedOutputStream;

import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import  java.security.SignatureException;

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

public class Publisher{
    private static final Integer CONTENT_LIFE_TIME = 1000; //ms

    private CCNHandle _handle = null;
    private CCNWriter _writer = null;

    public Publisher(CCNHandle handle){
        this._handle = handle;
        try{
            this._writer = new CCNWriter(handle);
        }
        catch(IOException ex){
            System.out.println("CCNWriter construction: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public synchronized boolean publish(String prefix, String msg){

        try{
            //Interest interest = new Interest(prefix);
            //_writer.addOutstandingInterest(interest);
            Thread.sleep(1000);
            _writer.put(ContentName.fromURI(prefix), msg, Protocol.MSG_TTL);
        }
        catch(SignatureException ex){
            ex.printStackTrace();
            return false;
        }
        catch(MalformedContentNameStringException ex){
            ex.printStackTrace();
            return false;
        }
        catch(IOException ex){
            ex.printStackTrace();
            return false;
        }
        catch(InterruptedException ex){
            ex.printStackTrace();
            return false;
        }
        return true;

    }

    public static void main(String args[]){
        try{
            Publisher publisher = new Publisher(CCNHandle.open());
            publisher.publish(Protocol.LIGHT_PUB_PREFIX + "Alice", "Alice's new tweet");
        }
        catch(Exception ex){
            System.out.println("Exception in Publisher.main : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}


