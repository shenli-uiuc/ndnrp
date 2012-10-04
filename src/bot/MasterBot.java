package ndnrp.bot;

import ndnrp.protocol.*;
import ndnrp.util.*;

import org.ccnx.ccn.CCNHandle;

public class MasterBot{
    private int _botNum = 0;
    private int _minWait = 0;
    private int _maxWait = 0;
    private CCNHandle _handle = null;
    private Bot [] _bots = null;

    public MasterBot(int botNum, int minWait, int maxWait, CCNHandle handle){
        this._botNum = botNum;
        this._minWait = minWait;
        this._maxWait = maxWait;
        this._handle = handle;
        this._bots = new Bot[_botNum];
    }

    public void start(){
        for(int i = 0 ; i < _botNum; ++i){
            _bots[i] = new Bot(Protocol.SERVER_IP, Protocol.SERVER_PORT, i,
                            _minWait, _maxWait, _handle);
            _bots[i].on();
        }
    }

    public void stop(){
        for(int i = 0 ; i < _botNum; ++i){
            _bots[i].off();
        } 
    }

}
