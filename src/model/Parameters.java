package model;


import java.util.HashMap;
import java.util.Map;

/**
 * @author yy
 */
public class Parameters {

    private static final String DEST_IP = "dest_ip";

    private static final String SRC_IP = "src_ip";

    private static final String FLOW_ID = "flow_id";

    private static final String FLOW_COUNT = "flow_count";

    private static final String DATA_SIZE = "data_size";

    private static final String CO_FLOW_ID = "co_flow_id";


    /**
     * 目的主机IP
     */
    private String destIp;

    private String srcIp;

    /**
     * 子流Id，使用报文的目的端口进行表示
     */
    private int flowId = 18888;

    /**
     * 子流数量
     */
    private int flowCount = 1;

    /**
     * 数据大小，单位MB
     */
    private int dataSize = 1;

    /**
     * 使用报文的源协流Id
     */
    private int coFlowId = 8888;

    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public int getFlowId() {
        return flowId;
    }

    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }

    public int getFlowCount() {
        return flowCount;
    }

    public void setFlowCount(int flowCount) {
        this.flowCount = flowCount;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getCoFlowId() {
        return coFlowId;
    }

    public void setCoFlowId(int coFlowId) {
        this.coFlowId = coFlowId;
    }


    public static Parameters getParameters(String[] args) {
        if (null == args || args.length == 0) {
            return null;
        }
        Map<String, String> params = new HashMap<>();
        Parameters parameters = new Parameters();
        for (String str : args) {
            String[] param = str.split("=");
            if (param.length != 2) {
                return null;
            }
            params.put(param[0], param[1]);
        }
        if (null != params.get(DEST_IP)) {
            parameters.setDestIp(params.get(DEST_IP));
        }
        if (null != params.get(SRC_IP)) {
            parameters.setSrcIp(params.get(SRC_IP));
        }
        if (null != params.get(FLOW_ID)) {
            parameters.setFlowId(Integer.parseInt(params.get(FLOW_ID)));
        }
        if (null != params.get(FLOW_COUNT)) {
            parameters.setFlowCount(Integer.parseInt(params.get(FLOW_COUNT)));
        }
        if (null != params.get(DATA_SIZE)) {
            parameters.setDataSize(Integer.parseInt(params.get(DATA_SIZE)));
        }
        if (null != params.get(CO_FLOW_ID)) {
            parameters.setCoFlowId(Integer.parseInt(params.get(CO_FLOW_ID)));
        }
        return parameters;
    }
}
