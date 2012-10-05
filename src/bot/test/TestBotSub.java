package ndnrp.bot.test;

import ndnrp.bot.*;
import ndnrp.protocol.*;
import ndnrp.util.*;
import ndnrp.ipsrc.client.*;
import ndnrp.ndnsrc.sub.*;

import org.ccnx.ccn.CCNHandle;
import org.ccnx.ccn.config.ConfigurationException;

import java.io.*;

public class TestBotSub{
    public static final String NAME = "Alice";

    public static void main(String args[]){
        try{
            IPClient ipSub = new IPClient(Protocol.SERVER_IP, Protocol.SERVER_PORT,
                    NAME, IPClient.SUBSCRIBER);
            LSSubscriber lsSub = new LSSubscriber(NAME, CCNHandle.open());
            for(int i = 0; i < TestBot.BOT_NUM; ++i){
                ipSub.subscribe(BotConfig.NAME_PREFIX + i);
                lsSub.subscribe(BotConfig.NAME_PREFIX + i);
            }        
            while(true){
                System.out.println("ipSub - " + ipSub.receive());
                System.out.println("lsSub - " + lsSub.receive());
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
