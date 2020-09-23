import model.Parameters;

<<<<<<< HEAD
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
=======
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
>>>>>>> dev
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * send packets
 * @author yy
 */

public class Client {
    private static final String DATA_PREFIX_CO_FLOW_ID = "co_flow_id=";
    private static final String DATA_PREFIX_FLOW_COUNT = "flow_count=";
    private static final String DATA_PREFIX_DATA_SIZE = "data_size=";
    private static final int BUFF_SIZE = 1024;
    private static volatile AtomicInteger dataSize;
    private static boolean isReceived=false;

    public static void main(String[] args) {
        try {
            Parameters parameters = Parameters.getParameters(args);
            checkParam(parameters);

            String ip = parameters.getDestIp();
            InetAddress destIp = InetAddress.getByName(ip);
            int flowId = parameters.getFlowId();
            int flowCount = parameters.getFlowCount();
            int coFlowId = parameters.getCoFlowId();
            dataSize = new AtomicInteger(parameters.getDataSize() * 1024 * 1024);
            DatagramSocket ds = new DatagramSocket(coFlowId);
            new SendWorker(ds, destIp, coFlowId, flowId, flowCount).start();
            new Listener().start();
            System.out.println("complete sending packets");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something wrong happened");
        }
    }

    private static void checkParam(Parameters parameters) throws Exception {
        if (null == parameters || null == parameters.getDestIp()) {
            throw new Exception("Invalid parameter");
        }
    }

    static class Listener extends Thread {
        public void run() {
            try {
<<<<<<< HEAD
//                DatagramSocket ds = new DatagramSocket(5002);
                while(true) { //
                    // 数据缓冲区:
                    System.out.println("start listening");
//                    byte[] buffer = new byte[1024];
//                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//                    ds.receive(packet); // 收取一个UDP数据包

                    DatagramChannel channel = DatagramChannel.open();
                    channel.socket().bind(new InetSocketAddress(5002));
                    ByteBuffer buf = ByteBuffer.allocate(64);
                    buf.clear();
                    SocketAddress s=channel.receive(buf);
                    System.out.println(s.toString());

                    if(isReceived==false&&s!=null){
=======
                DatagramSocket ds = new DatagramSocket(5002);
                while(true) { //
                    // 数据缓冲区:
                    System.out.println("start listening");
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    ds.receive(packet); // 收取一个UDP数据包
                    if(isReceived==false){
>>>>>>> dev
                        isReceived=true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("exception happened during listening");
            }
        }
    }

    static class SendWorker extends Thread {
        private final int coFlowId;
        private final int flowId;
        private final InetAddress destIp;
        private final DatagramSocket ds;
        private final int flowCount;
//        private String data;

        SendWorker(DatagramSocket ds, InetAddress destIp, int coFlowId, int flowId, int flowCount) {
            this.ds = ds;
            this.destIp = destIp;
            this.coFlowId = coFlowId;
            this.flowId = flowId;
            this.flowCount = flowCount;
        }

        @Override
        public void run() {
            int i = 0;
            isReceived=false;
            try {
                int buffSize = BUFF_SIZE;
                int send_port=65535 - coFlowId * 100 - flowId;
                DatagramSocket ds = new DatagramSocket(send_port); // 指定发送端口

                while (isReceived==false) {
//                    byte[] buffer = new byte[1024];//这三行代码是为了监听服务端返回的终结数据包
//                    DatagramPacket ending_packet = new DatagramPacket(buffer, buffer.length);
//                    ds.receive(ending_packet);
                    String data = destIp.toString()+DATA_PREFIX_CO_FLOW_ID + coFlowId + ";" +
                            DATA_PREFIX_FLOW_COUNT + flowCount + ";" +
                            DATA_PREFIX_DATA_SIZE + buffSize;
                    byte[] buff = Arrays.copyOf(data.getBytes(), buffSize);
                    DatagramPacket packet = new DatagramPacket(buff, 0, buff.length, destIp, 5001);
                    ds.send(packet);
//                        i++;
                    System.out.println(i++);
<<<<<<< HEAD
                    if(i>10){  //这里测试一下我的停止发包的函数
                        packet = new DatagramPacket(buff, 0, buff.length, destIp, 5002);
                        ds.send(packet);
                    }
=======
//                    if(i>10000){  //这里测试一下我的停止发包的函数
//                        packet = new DatagramPacket(buff, 0, buff.length, destIp, 5002);
//                        ds.send(packet);
//                    }
>>>>>>> dev
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("exception happened during sending, coFlowId = " + coFlowId + " flowId = " + flowId);
            } finally {
                System.out.println("coflow" + coFlowId + " flow" + flowId + " complete sending");
                System.out.println(i);//发包数量

            }
        }
    }
}
