package ndnrp.ndnsrc.test;

import ndnrp.ndnsrc.sub.*;
import ndnrp.util.*;

import org.ccnx.ccn.CCNHandle;

public class TestHSPost{
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
        subscriber.post(args[1]); 
    } 
}
