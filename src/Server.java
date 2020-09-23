import model.Constant;
import model.TransmissionData;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Huan
 */
public class Server {

    public static void main(String[] args) {
        try {
            DatagramSocket socket;
            Map<Integer, TransmissionData> tDataMap = new HashMap<>();
            Map<Integer, Integer> receiveDataMap = new HashMap<>();

            System.out.println("Server is running and listen to " + Constant.SERVER_PORT);
            socket = new DatagramSocket(Constant.SERVER_PORT);

            // noinspection InfiniteLoopStatement 服务器本就是一直持续的,
            while(true){
                DatagramPacket packet = new DatagramPacket(new byte[Constant.BUFF_SIZE], Constant.BUFF_SIZE);
                try {
                    // 据说此方法接收到数据报之前会一直阻塞
                    socket.receive(packet);
                    byte[] data = packet.getData();

                    // 这里应该没有问题了
                    String[] splits = new String(data).split(TransmissionData.SF);
                    TransmissionData tData = TransmissionData.getTransmissionData(
                            Arrays.copyOfRange(splits,0,Constant.NUMBER_OF_DATA_PREFIX)
                    );

                    int newSum = 0,key = 65535 - tData.hashCode();
                    // 假如不包含这个键，则记录新的tData
                    if(!tDataMap.containsKey(key)){
                        System.out.printf(
                                "Flow(%d,%d) from %s:%d is established",
                                tData.coflow_id,tData.flow_id,tData.src_ip,tData.src_port
                        );
                        tDataMap.put(key, tData);
                        newSum = packet.getLength();
                    } else {
                        // 否则累加已经接受到的数据量
                        newSum = receiveDataMap.get(key) + packet.getLength();
                    }
                    receiveDataMap.put(key,newSum);

                    // 假如已经接收到的数据量超过的data_size,这发送应答信号
                    if(newSum > tData.data_size){
                        System.out.printf(
                                "Flow(%d,%d) from %s:%d is completed",
                                tData.coflow_id,tData.flow_id,tData.src_ip,tData.src_port
                        );
                        ACK(tData);
                        tDataMap.remove(key);
                        receiveDataMap.remove(key);
                    }

                } catch (IOException e) {
                    // 打印异常消息
                    System.err.println("Server发生异常,socket接受数据包失败");
                    e.printStackTrace();

                    // 重启socket
                    System.out.println("We are trying for reboot the socket");
                    socket.close();
                    socket = new DatagramSocket(Constant.SERVER_PORT);
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket ");
            e.printStackTrace();
        }
    }

    /**
     * 发送应答信号
     * @param tData TransmissionData
     */
    private static void ACK(TransmissionData tData){
        try {
            // 传输到Client的65535 - hash处
            DatagramSocket socket = new DatagramSocket(65535 - tData.hashCode());
            for(int i = 0; i < Constant.ACK_NUMBER; i++){
                try {
                    byte[] buff = Arrays.copyOf(tData.toString().getBytes(), Constant.BUFF_SIZE);
                    // Client的接受端口是10000 + hash
                    DatagramPacket packet = new DatagramPacket(
                            buff, 0, buff.length,
                            InetAddress.getByName(tData.src_ip),
                            10000 + tData.hashCode()
                    );
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}


