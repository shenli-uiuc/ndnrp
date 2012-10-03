package ccnps.test;

import ccnps.sub.*;
import ccnps.util.*;

import org.ccnx.ccn.CCNHandle;

public class TestHSSubscriber{
    public static void main(String args[]){
        int i = 0;
        CCNHandle handle = null;
        try{
            handle = CCNHandle.open();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        HSSubscriber subscriber = new HSSubscriber(args[0], handle);
        for(i = 1; i < args.length; ++i){
            subscriber.subscribe(args[i]);
        }
        while(true){
            System.out.println(subscriber.receive());
        }
    } 
}
