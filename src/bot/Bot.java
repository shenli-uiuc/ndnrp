package ndnrp.bot;

import ndnrp.ipsrc.client.*;
import ndnrp.ndnsrc.sub.*;
import ndnrp.protocol.*;
import ndnrp.util.*;

import org.ccnx.ccn.CCNHandle;

import java.net.*;
import java.util.*;
import java.io.*;

public class Bot extends Thread{
    public static final int MIN_STR_LEN = BotConfig.MIN_MSG;
    public static final int MAX_STR_LEN = BotConfig.MAX_MSG;
    public static final char[] CHAR_SET = 
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray(); 

    private CCNHandle _handle = null;
    private String _ip = null;
    private int _id = 0;
    private int _port = 0;
    private IPClient _ipSub = null;
    private LSSubscriber _lsSub = null;
    private int _minWait = 0;
    private int _maxWait = 0;
    private String _name = null;
    private Random _rnd = null;
    private char [] _charBuf = null;
    private StatMonitor _statMonitor = null;

    private boolean _isRunning = false;

    public Bot(String ip, int port, int id, 
                int minWait, int maxWait, CCNHandle handle, StatMonitor statMonitor){
        this._handle = handle;
        this._ip = ip;
        this._port = port;
        this._id = id;
        this._statMonitor = statMonitor;
        this._name = BotConfig.NAME_PREFIX + _id;
        this._minWait = minWait;
        this._maxWait = maxWait;
        this._isRunning = false;
        this._rnd = new Random(id);        
        this._charBuf = new char[MAX_STR_LEN];

        this._lsSub = new LSSubscriber(_name, _handle, _statMonitor);
        this._ipSub = new IPClient(_ip, _port, _name, IPClient.PUBLISHER);
    }

    public void on(){
        _isRunning = true;
        this.start();
    }

    public void off(){
        _isRunning = false;
    }

    private String getRndStr(){
        int rndLen = _rnd.nextInt(MAX_STR_LEN - MIN_STR_LEN) + MIN_STR_LEN;
        for (int i = 0; i < rndLen; i++){
            _charBuf[i] = CHAR_SET[_rnd.nextInt(CHAR_SET.length)];
        }
        return new String(_charBuf, 0, rndLen);
    }

    private int getRndLen(){
        return (_rnd.nextInt(_maxWait - _minWait) + _minWait) * 1000;
    }

    public void run(){
        String rndStr = null;
        try{
            while(_isRunning){
                rndStr = getRndStr();
                _lsSub.botPost(rndStr);
                _ipSub.botPost(rndStr);
                Thread.sleep(getRndLen());
            }
        }
        catch(InterruptedException ex){
            ex.printStackTrace();
        }
    }

}
