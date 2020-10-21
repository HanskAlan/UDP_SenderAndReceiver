import model.Constant;
import model.TransmissionData;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Server {
    static Map<Integer, TransmissionData> tDataMap = new HashMap<>();
    static Map<Integer, Integer> receiveDataMap = new HashMap<>();
    static Map<Integer, Long> lastPrintMap = new HashMap<>(); // 最后一次打印
    static Map<Integer, Long> timeOutMap = new HashMap<>(); // 在这个Map中的数据将在10秒后删除
    static Map<Integer, DatagramSocket> socketMap = new HashMap<>(); // 在这个Map中的数据将在10秒后删除


    public static void main(String[] args) {
        try {
            DatagramSocket socket;

            System.out.println("Server is running and listen to " + Constant.SERVER_PORT);
            socket = new DatagramSocket(Constant.SERVER_PORT);

            long lastCleanTime = System.currentTimeMillis();

            // noinspection InfiniteLoopStatement 服务器本就是一直持续的,
            while(true){
                DatagramPacket packet = new DatagramPacket(new byte[Constant.BUFF_SIZE], Constant.BUFF_SIZE);
                try {
                    // 据说此方法接收到数据报之前会一直阻塞
                    socket.receive(packet);
                    byte[] data = packet.getData();

                    // 解析收到的数据
                    String[] splits = new String(data).split(TransmissionData.SF);
                    if(splits.length < Constant.NUMBER_OF_DATA_PREFIX){ // 不合法数据直接抛弃
                        System.out.println("The UDP format is illegal.");
                        continue;
                    }
                    TransmissionData tData = TransmissionData.getTransmissionData(
                            Arrays.copyOfRange(splits,0, Constant.NUMBER_OF_DATA_PREFIX)
                    );


                    int newSum = 0,key = 65535 - tData.hashCode();

                    // 0. 周期性删除过时记录
                    if(System.currentTimeMillis() - lastCleanTime > Constant.TIME_OUT_LIMIT){
                        cleanMap();
                        lastCleanTime = System.currentTimeMillis();
                    }

                    // 1. 首先判断是否在在TimeOutMap中，假如在TimeOutMap则立即应答ACK,并且更新TimeOutMap时间
                    if(timeOutMap.containsKey(key)){
                        ACK(tData,socketMap.get(key));
                        timeOutMap.put(key,System.currentTimeMillis());
                        // 统计传输的总数据量，我想看看ACK到底及不及时
                        newSum = receiveDataMap.get(key) + packet.getLength();
                        receiveDataMap.put(key,newSum);
                        continue; // 不执行后面的部分
                    }

                    // 2. 然后判断现在的tDataMap是否包含这个键。假如不包含这个键，则添加新的记录到tDataMap和和receiveDataMap中
                    if(!tDataMap.containsKey(key)){
                        System.out.printf(
                                "Flow(%d,%d) from %s:%d is established %d\n",
                                tData.coflow_id,tData.flow_id,tData.src_ip,tData.src_port,System.currentTimeMillis()
                        );
                        tDataMap.put(key, tData);
                        lastPrintMap.put(key,System.currentTimeMillis());
                        newSum = packet.getLength();
                    } else {
                        // 否则累加已经接受到的数据量
                        newSum = receiveDataMap.get(key) + packet.getLength();
                        long time = System.currentTimeMillis();

                        // 每间隔一段时间进行打印
                        if(time > lastPrintMap.get(key) + Constant.PRINT_INTERVAL){
                            lastPrintMap.put(key,System.currentTimeMillis());
                            System.out.printf(
                                    "Flow(%d,%d) %d %d %f %d\n",
                                    tData.coflow_id,tData.flow_id,
                                    newSum,tData.data_size,
                                    1.0 * newSum / tData.data_size,
                                    System.currentTimeMillis()
                            );
                        }
                    }
                    receiveDataMap.put(key,newSum);

                    // 3. 假如在receive记录中记录的已经接收到的数据量超过了data_size,则发送应答信号。
                    // 并且在timeOutMap和socketMap中添加记录
                    if(newSum > tData.data_size){
                        System.out.printf(
                                "Flow(%d,%d) from %s:%d is completed, and sending ack %d\n",
                                tData.coflow_id,tData.flow_id,tData.src_ip,tData.src_port,System.currentTimeMillis()
                        );
                        socketMap.put(key, ACK(tData,null)); // 备份socket
                        timeOutMap.put(key,System.currentTimeMillis());
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
    private static DatagramSocket ACK(TransmissionData tData,DatagramSocket socket) throws IOException{
        if (socket == null) {
            try {
                // 传输到Client的65535 - hash处
                socket = new DatagramSocket(65535 - tData.hashCode());
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        byte[] buff = Arrays.copyOf(tData.toString().getBytes(), Constant.BUFF_SIZE);
        // Client的接受端口是10000 + hash
        DatagramPacket packet = new DatagramPacket(
                buff, 0, buff.length,
                InetAddress.getByName(tData.src_ip),
                10000 + tData.hashCode()
        );
        assert socket != null;
        socket.send(packet);
        return socket;
    }

    /**
     * 清除已经完成的流的记录
     */
    private static void cleanMap(){
        if(timeOutMap.size() == 0)return;

        // 先获取需要删除的Key
        ArrayList<Integer> removeKey = new ArrayList<>();
        for(int key : timeOutMap.keySet())
            if(System.currentTimeMillis() - timeOutMap.get(key) > Constant.TIME_OUT_LIMIT)
                removeKey.add(key);

        if(removeKey.size() == 0)return; // 减少开销


        System.out.println("Clear the record of the completed flow");
        // 删除数据
        for(int key : removeKey){
            TransmissionData tData = tDataMap.remove(key);
            System.out.printf(
                    "Flow(%d,%d) has transfer %d B\n",
                    tData.coflow_id,tData.flow_id,
                    receiveDataMap.remove(key)
            );
            timeOutMap.remove(key);
            socketMap.remove(key).close(); // 要记得关闭socket
            System.out.printf(
                    "Flow(%d,%d)'record is removed\n",
                    tData.coflow_id,tData.flow_id
            );
            lastPrintMap.remove(key);
        }
    }
}


