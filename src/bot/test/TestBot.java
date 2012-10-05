package ndnrp.bot.test;

import ndnrp.bot.*;

import org.ccnx.ccn.CCNHandle;
import org.ccnx.ccn.config.ConfigurationException;

import java.io.*;

public class TestBot{
    public static int BOT_NUM = 10;

    public static void main(String args[]){
        try{
            MasterBot mb = new MasterBot(BOT_NUM, BOT_NUM, BOT_NUM * 2, CCNHandle.open());
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
