package model;

public class Constant {
    public static String DATA_PREFIX_CO_FLOW_ID = "coflow_id=";
    public static String DATA_PREFIX_FLOW_COUNT = "flow_count=";
    public static String DATA_PREFIX_FLOW_ID    = "flow_id=";
    public static String DATA_PREFIX_DATA_SIZE  = "data_size=";
    public static String DATA_PREFIX_SRC_IP     = "src_ip=";
    public static String DATA_PREFIX_SRC_PORT   = "src_port=";
    public static String DATA_PREFIX_DST_IP     = "dst_ip=";
    public static String DATA_PREFIX_DST_PORT   = "dst_port=";

    public static int BUFF_SIZE = 1024;
    public static int ClientPort = 5002;
    public static int ServerPort = 5001;
    public static int ackNumber = 10;
    public static int hash(int coflowID,int flowId){
        return 65535 - coflowID * 100 - flowId;
    }
}
