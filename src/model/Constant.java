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
    public static int CLIENT_PORT = 5002;
    public static int SERVER_PORT = 5001;
    public static int ACK_NUMBER = 10;
    public static int hash(int coflowID,int flowId){
        return coflowID * 60 + flowId;
    }
}
