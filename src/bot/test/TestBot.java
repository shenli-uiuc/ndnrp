package ndnrp.bot.test;

import ndnrp.bot.*;
import ndnrp.protocol.*;
import ndnrp.util.*;

import org.ccnx.ccn.CCNHandle;
import org.ccnx.ccn.config.ConfigurationException;

import java.io.*;

public class TestBot{
    public static int BOT_NUM = 10;

    public static void main(String args[]){
        try{
            StatMonitor sm = new StatMonitor();
            MasterBot mb = new MasterBot(BOT_NUM, BOT_NUM, BOT_NUM * 2, CCNHandle.open(),
                                    Protocol.SERVER_IP, Protocol.SERVER_PORT, sm);
            mb.start();
        }
        catch(ConfigurationException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
