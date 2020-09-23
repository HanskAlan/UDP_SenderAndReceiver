import model.Constant;
import model.TransmissionData;

import java.io.*;
import java.lang.reflect.Array;
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
            System.out.println("Server is running and waiting for udp data");
            DatagramSocket socket;
            Map<Integer, TransmissionData> tDataMap = new HashMap<>();
            Map<Integer, Integer> receiveDataMap = new HashMap<>();

            socket = new DatagramSocket(Constant.SERVER_PORT);

            // noinspection 服务器本就是一直持续的
            while(true){
                DatagramPacket packet = new DatagramPacket(new byte[Constant.BUFF_SIZE], Constant.BUFF_SIZE);
                try {
                    // 据说此方法接收到数据报之前会一直阻塞
                    socket.receive(packet);

                    SocketAddress socketAddress = packet.getSocketAddress();
                    System.out.println("UDP包的接受IP" + socketAddress.toString());

                    byte[] data = packet.getData();

                    // 这里应该没有问题了
                    String[] splits = new String(data).split(TransmissionData.SF);
                    TransmissionData tData = TransmissionData.getTransmissionData(
                            Arrays.copyOfRange(splits,0,Constant.NUMBER_OF_DATA_PREFIX)
                    );

                    int newSum;
                    if(!tDataMap.containsKey(tData.hashCode())){
                        tDataMap.put(tData.hashCode(), tData);
                        newSum = packet.getLength();
                    } else {
                        newSum = receiveDataMap.get(tData.hashCode()) + packet.getLength();
                    }
                    receiveDataMap.put(tData.hashCode(),newSum);
                    if(newSum > tData.data_size){
                        ACK(tData);
                        tDataMap.remove(tData.hashCode());
                        receiveDataMap.remove(tData.hashCode());
                    }

                } catch (IOException e) {
                    // 打印异常消息
                    System.err.println("Server发生异常,socket接受数据包失败");
                    e.printStackTrace();

                    // 重启socket
                    System.out.println("正在尝试重启socket");
                    socket.close();
                    socket = new DatagramSocket(Constant.SERVER_PORT);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送应答信号
     * @param tData TransmissionData
     */
    private static void ACK(TransmissionData tData){
        try {
            DatagramSocket socket = new DatagramSocket(tData.hashCode());
            for(int i = 0; i < Constant.ACK_NUMBER; i++){
                try {
                    byte[] buff = Arrays.copyOf(tData.toString().getBytes(), Constant.BUFF_SIZE);
                    DatagramPacket packet = new DatagramPacket(
                        buff, 0, buff.length,
                        InetAddress.getByName(tData.src_ip),
                        Constant.CLIENT_PORT
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


