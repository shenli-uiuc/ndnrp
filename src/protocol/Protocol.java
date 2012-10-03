package ndnrp.protocol;

public class Protocol{
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 20480;

    public static final String LIGHT_POST_PREFIX = "ccnx:/ndnrp/light/post/";
    public static final String LIGHT_PUB_PREFIX = "ccnx:/ndnrp/light/pub/";
    public static final String LIGHT_PREFIX = "ccnx:/ndnrp/light/";

    public static final String HEAVY_POST_PREFIX = "ccnx:/ndnrp/heavy/post/";
    public static final String HEAVY_PUB_PREFIX = "ccnx:/ndnrp/heavy/pub/";
    public static final String HEAVY_SUB_PREFIX = "ccnx:/ndnrp/heavy/sub/";
    public static final String HEAVY_UNSUB_PREFIX = "ccnx:/ndnrp/heavy/unsub/"
    public static final String HEAVY_LISTEN_PREFIX = "ccnx:/ndnrp/heavy/listen/";
    public static final String HEAVY_PREFIX = "ccnx:/ndnrp/heavy/";

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String MSG_FORMAT_ERROR = "message format error";
    public static final String PUB_NOT_EXIST = "publisher is not exist";
    public static final String SUB_NOT_EXIST = "you are not subscribing to the publisher";
    public static final String SUB_ALREADY = "already subscribed";
    public static final String SUB_SOCK_UPDATE = "your connection socket has been updated";
    public static final String SERVICE_NOT_EXIST = "The requested service does not exist";
    public static final String TIME_OUT = "Time out";
    public static final String LISTEN_ALREADY = "you already have a live connection";

    public static final int ONEDAY = 1000 * 60 * 60 * 24;
    public static final Integer MSG_TTL = 1;
}
