package ndnrp.util;

import ndnrp.protocol.*;

import java.util.*;

public class StatMonitor{
    public static final int MSG_BUF_LEN = 1000;
    public static final int FACE_MEM = 2500;

    private int _aMsg = 0;
    private int _wMsg = 0;
    private double _oFP = 0;
    private int _faceCnt = 0;
    private int _hMem = 0;
    private int _cMem = 0;
    private double _cFP = 0;
    private boolean [] _msgBuf = null;
    private int _rotateIndex = 0;
    //TODO: keep this cnt for wrong message in _msgBuf for fast cFP calculation
    private int _wMsgCurCnt = 0;

    public StatMonitor(){
        this._msgBuf = new boolean[MSG_BUF_LEN];
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
    }

    public int reportInterest(String name){
        //TODO: calculate the interest cost in CCNx
    }

    public int getAllMsg(){
        return _aMsg;
    }

    public int getWrongMsg(){
        return _wMsg;
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
