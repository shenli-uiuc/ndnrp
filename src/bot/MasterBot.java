package ndnrp.bot;

import ndnrp.protocol.*;
import ndnrp.util.*;

import org.ccnx.ccn.CCNHandle;

public class MasterBot{
    private int _botNum = 0;
    private int _minWait = 0;
    private int _maxWait = 0;
    private String _ip = null;
    private int _port = 0;
    private CCNHandle _handle = null;
    private Bot [] _bots = null;

    public MasterBot(int botNum, int minWait, int maxWait, CCNHandle handle, String ip, int port){
        this._botNum = botNum;
        this._minWait = minWait;
        this._maxWait = maxWait;
        this._handle = handle;
        this._bots = new Bot[_botNum];
        this._ip = ip;
        this._port = port;
    }

    public void start(){
        for(int i = 0 ; i < _botNum; ++i){
            _bots[i] = new Bot(_ip, _port, i,
                            _minWait, _maxWait, _handle);
            _bots[i].setDaemon(true);
            _bots[i].on();
        }
    }

    public void stop(){
        for(int i = 0 ; i < _botNum; ++i){
            _bots[i].off();
        } 
    }

    
}
