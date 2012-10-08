package ndnrp.util;

import java.util.*;
import java.util.Collections.*;

public class StatMonitor{
    public static final int MSG_BUF_LEN = 1000;
    public static final int FACE_MEM = 8000;
    public static final int EXTRA_NAME_LEN = 30;

    private int _aMsg = 0;
    private int _wMsg = 0;
    private double _oFP = 0;
    private int _faceCnt = 0;
    private int _hMem = 0;
    private int _cMem = 0;
    private double _cFP = 0;
    private boolean [] _msgBuf = null;
    private int _rotateIndex = 0;
    private int _wMsgCurCnt = 0;
    private Set _intSet = null;

    public StatMonitor(){
        this._msgBuf = new boolean[MSG_BUF_LEN];
        _intSet = Collections.synchronizedSet(new HashSet<String>());
    }

    public synchronized void reportMsg(boolean isWrong){
        ++_aMsg;
        if(isWrong){
            ++_wMsg;
            ++_wMsgCurCnt;
        }
        if(_msgBuf[_rotateIndex]){
            --_wMsgCurCnt;
        }
        _msgBuf[_rotateIndex] = isWrong;
        _rotateIndex = (_rotateIndex + 1) % MSG_BUF_LEN;
    }

    public synchronized void reportFace(boolean isCreate){
        if(isCreate){
            ++_faceCnt;
        }
        else{
            --_faceCnt;
        }
        _hMem = _faceCnt * FACE_MEM;
    }

    public synchronized int reportInterest(String name){
        if(_intSet.contains(name)){
            return 0;
        }
        //0. DTags for interest
        int allMem = 4;
        
        allMem += (name.length() + EXTRA_NAME_LEN);
        //1. comps
        int lastIndex = 0;
        int count =0;
        
        while(lastIndex != -1){

            lastIndex = name.indexOf("/",lastIndex);
            if( lastIndex != -1){
                ++count;
                ++lastIndex;
            }
        }
        //comps + DTags
        allMem += (count * 8);

        //2. interest_entry
        //2.1 ielinks: 16
        //2.2 strategy: 24
        //2.2 pit_face_item: 40
        //2.3 ccn_scheduled_event: 16
        //2.- others: 20
        allMem += 116;

        _cMem += allMem;

        return allMem;
    }

    public int getAllMsg(){
        return _aMsg;
    }

    public int getWrongMsg(){
        return _wMsg;
    }

    public int getHermesMem(){
        return _hMem;
    }

    public int getCCNxMem(){
        return _cMem * 8;
    }

    public double getOverallFP(){
        if(0 == _aMsg){
            return 0;
        }
        else{
            return _wMsg / (double)(_aMsg);
        }
    }

    public double getCurrentFP(){
        return _wMsgCurCnt / (double)(MSG_BUF_LEN);
    }
}
