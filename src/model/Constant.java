package model;

public class Constant {
    public static final String DATA_PREFIX_CO_FLOW_ID = "coflow_id=";
    public static final String DATA_PREFIX_FLOW_COUNT = "flow_count=";
    public static final String DATA_PREFIX_FLOW_ID    = "flow_id=";
    public static final String DATA_PREFIX_DATA_SIZE  = "data_size=";
    public static final String DATA_PREFIX_SRC_IP     = "src_ip=";
    public static final String DATA_PREFIX_SRC_PORT   = "src_port=";
    public static final String DATA_PREFIX_DST_IP     = "dst_ip=";
    public static final String DATA_PREFIX_DST_PORT   = "dst_port=";

    public static int NUMBER_OF_DATA_PREFIX = 8;
    public static int BUFF_SIZE = 1024;
    public static int SERVER_PORT = 5001;
    public static Long TIME_OUT_LIMIT = 5 * 1000L; // 单位ms
    public static int CLIENT_SLEEP_TIME = 50; // 单位ms
    public static int PRINT_INTERVAL = 200; // 打印间隔时间，单位ms
    public static int hash(int coflowID,int flowId){
        return coflowID * 60 + flowId;
    }
}
