package ndnrp.util;

import ndnrp.protocol.*;

import java.util.*;

public class StatMonitor{
    public static final int MSG_BUF_LEN = 1000;
    public static final int FACE_MEM = 2500;
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
    private HashSet<String> intSet = null;

    public StatMonitor(){
        this._msgBuf = new boolean[MSG_BUF_LEN];
        intSet = new HashSet<String>;
    }

    public void reportMsg(boolean isWrong){
        ++_aMsg;
        if(isWrong){
            ++_wMsg;
            ++_wMsgCurCnt;
        }
        if(msgBuf[_rotateIndex]){
            --_wMsgCurCnt;
        }
        msgBuf[_rotateIndex] = isWrong;
        _rotateIndex = (_rotateIndex + 1) % MSG_BUF_LEN;
    }

    public void reportFace(boolean isCreate){
        if(isCreate){
            ++_faceCnt;
        }
        else{
            --_faceCnt;
        }
        _hMem = _faceCnt * FACE_MEM;
    }

    public int reportInterest(String name){
        if(_intSet.contains(name)){
            return 0;
        }
        int allMem = 0;
        
        allMem += (name.length() + EXTRA_NAME_LEN);
        //1. comps
        int lastIndex = 0;
        int count =0;

        while(lastIndex != -1){

            lastIndex = str.indexOf("/",lastIndex);

            if( lastIndex != -1){
                count ++;
            }
        }
        allMem += (count * 4);

        //2. interest_entry
        //2.1 ielinks
        allMem += 16;
        //2.2 strategy
        allMem += 24;
        //2.2 pit_face_item
        allMem += (28 + 12);
        //2.3 ccn_scheduled_event
        allMem += 16;
        //2.- others
        allMem += 20;

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
            return _wMsg / _aMsg;
        }
    }

    public double getCurrentFP(){
        return _wMsgCurCnt / MSG_BUF_LEN;
    }
}
