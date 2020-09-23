package model;

import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;

public class TransmissionData {
    public static final String SF = ";";// split flag
    public int coflow_id, flow_count;
    public int flow_id, data_size;
    public String src_ip, dst_ip;
    public int src_port, dst_port;

    public TransmissionData(){
        this(-1,-1,-1,-1,null,null,-1,-1);
    }
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
        String[] splits = string.split(SF);
        return getTransmissionData(splits);
    }


    public static TransmissionData getTransmissionData(String[] splits){
        TransmissionData data = new TransmissionData();
        for(String split : splits){
            String key = split.split("=")[0];
            String value = split.split("=")[1];
            switch (key + "="){
                case Constant.DATA_PREFIX_CO_FLOW_ID:
                    data.coflow_id = Integer.parseInt(value);
                    break;
                case Constant.DATA_PREFIX_DATA_SIZE:
                    data.data_size = Integer.parseInt(value);
                    break;
                case Constant.DATA_PREFIX_DST_IP:
                    data.dst_ip = value;
                    break;
                case Constant.DATA_PREFIX_DST_PORT:
                    data.dst_port = Integer.parseInt(value);
                    break;
                case Constant.DATA_PREFIX_FLOW_COUNT:
                    data.flow_count = Integer.parseInt(value);
                    break;
                case Constant.DATA_PREFIX_FLOW_ID:
                    data.flow_id = Integer.parseInt(value);
                    break;
                case Constant.DATA_PREFIX_SRC_IP:
                    data.src_ip = value;
                    break;
                case Constant.DATA_PREFIX_SRC_PORT:
                    data.src_port = Integer.parseInt(value);
                    break;
            }
        }

        return data;
    }



    @Override
    public String toString() {
        return "coflow_id=" + coflow_id + SF +
                "flow_count=" + flow_count + SF +
                "flow_id=" + flow_id +SF +
                "data_size=" + data_size + SF +
                "src_ip=" + src_ip + SF +
                "dst_ip=" + dst_ip + SF +
                "src_port=" + src_port + SF +
                "dst_port=" + dst_port + SF;
    }

    @Override
    public int hashCode() {
        return Constant.hash(coflow_id,flow_id);
    }

    public static void main(String[] args) {
        TransmissionData tData = new TransmissionData(
                1,2,
                3,4,
                "10.0.0.1","10.0.0.1",
                7,8
        );
        System.out.println("原始数据生成");
        System.out.println(tData);
        System.out.println("使用Split Flag对上述字符串执行split方法");
        String str = tData.toString();
        System.out.println(Arrays.toString(str.split(SF)));

        System.out.println("测试getTransmissionData方法");
        System.out.println(TransmissionData.getTransmissionData(tData.toString()));

        byte[] buff = Arrays.copyOf((tData.toString() + "5145613sA84BJKLAJLLK").getBytes(), Constant.BUFF_SIZE);
        str = new String(buff);
        System.out.println(str);
        String[] tmp =  str.split(SF);
        StringBuilder strBuff = new StringBuilder();
        for(int i = 0; i < Constant.NUMBER_OF_DATA_PREFIX;i++){
            strBuff.append(tmp[i]);
            strBuff.append(SF);
        }
        System.out.println(TransmissionData.getTransmissionData(strBuff.toString()));

        System.out.println("测试使用split作为参数的getTransmissionData方法");
        System.out.println(TransmissionData.getTransmissionData(
                Arrays.copyOfRange(tmp,0,Constant.NUMBER_OF_DATA_PREFIX)
        ));
    }
}