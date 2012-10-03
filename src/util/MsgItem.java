package ndnrp.util;

public class MsgItem{
    private String _msg = null;
    private String _pubName = null;

    public MsgItem(String pubName, String msg){
        this._msg = msg;
        this._pubName = pubName;
    }

    public String getMsg(){
        return this._msg;
    }

    public String getPublisher(){
        return this._pubName;
    }
}
