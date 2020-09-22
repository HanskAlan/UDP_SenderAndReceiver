package model;

import java.util.Arrays;

public class TransmissionData {
    int coflow_id, flow_count;
    int flow_id, data_size;
    String src_ip, dst_ip;
    int src_port, dst_port;

    private TransmissionData(){}
    public TransmissionData(
            int coflowId, int flowCount,
            int flowId, int dataSize,
            String srcIP,String dstIP,
            int srcPort,int dstPort
    ){
        this.coflow_id = coflowId;
        this.flow_count = flowCount;
        this.flow_id = flowId;
        this.data_size = dataSize;

        this.src_ip = srcIP;
        this.src_port = srcPort;

        this.dst_ip = dstIP;
        this.dst_port = dstPort;
    }

    public static TransmissionData getTransmissionData(String string){
        TransmissionData data = new TransmissionData();
        String[] split = string.split(", ");
        data.coflow_id = Integer.parseInt(split[0].split("=")[1]);
        data.flow_count = Integer.parseInt(split[1].split("=")[1]);
        data.flow_id = Integer.parseInt(split[2].split("=")[1]);
        data.data_size = Integer.parseInt(split[3].split("=")[1]);
        data.src_ip = split[4].split("=")[1];
        data.dst_ip = split[5].split("=")[1];
        data.src_port = Integer.parseInt(split[6].split("=")[1]);
        data.dst_port = Integer.parseInt(split[7].split("=")[1]);
        return data;
    }

    @Override
    public String toString() {
        return "coflow_id=" + coflow_id +
                ", flow_count=" + flow_count +
                ", flow_id=" + flow_id +
                ", data_size=" + data_size +
                ", src_ip=" + src_ip +
                ", dst_ip=" + dst_ip +
                ", src_port=" + src_port +
                ", dst_port=" + dst_port ;
    }

    public static void main(String[] args) {
        TransmissionData transmissionData = new TransmissionData(
                1,2,
                3,4,
                "10.0.0.1","10.0.0.1",
                7,8
        );
        System.out.println(transmissionData);
        String str = transmissionData.toString();
        System.out.println(Arrays.toString(str.split(", ")));
        System.out.println(TransmissionData.getTransmissionData(transmissionData.toString()));
    }
}