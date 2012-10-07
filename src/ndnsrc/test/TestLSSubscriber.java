package ndnrp.ndnsrc.test;

import ndnrp.ndnsrc.sub.*;
import ndnrp.util.*;

import org.ccnx.ccn.CCNHandle;

public class TestLSSubscriber{
    public static void main(String args[]){
        int i = 0;
        CCNHandle handle = null;
        try{
            handle = CCNHandle.open();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        StatMonitor sm = new StatMonitor();
        LSSubscriber subscriber = new LSSubscriber(args[0], handle, sm);
        for(i = 1; i < args.length; ++i){
            subscriber.subscribe(args[i]);
        }
        while(true){
            System.out.println(subscriber.receive());
        }
    } 
}
