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
    private static final String RATE = "rate";
    private static final String PERCENT = "percent";

    /**
     * 目的主机IP
     */
    private String destIp;

    /**
     * 源主机IP
     */
    private String srcIp;

    /**
     * 子流Id
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

    /**
     * 传输速率（内部以MB/s表示，外部以B/s表示）默认1M/s
     */
    private long rate = 1;

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    private double percent = 1;

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

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
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
        if (null != params.get(RATE)){
            parameters.setRate(Long.parseLong(params.get(RATE)));
        }
        if (null != params.get(PERCENT)){
            parameters.setPercent(Double.parseDouble(params.get(PERCENT)));
        }
        return parameters;
    }
}
