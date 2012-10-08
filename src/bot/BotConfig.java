package ndnrp.bot;

public class BotConfig{
    public static final int MIN_MSG = 10;
    public static final int MAX_MSG = 20;
    public static final int SUB_INTERVAL = 2000; //ms
    public static final String NAME_PREFIX = "Bot";

    private int _num = 0;
    private int _minWait = 10;
    private int _maxWait = 20;

    public BotConfig(){
        
    }

    public void setNum(int num){
        _num = num;
    }

    public void setMinWait(int minWait){
        _minWait = minWait;
    }

    public void setMaxWait(int maxWait){
        _maxWait = maxWait;
    }

    public int getNum(){
        return _num;
    }

    public int getMinWait(){
        return _minWait;
    } 

    public int getMaxWait(){
        return _maxWait;
    }
}
