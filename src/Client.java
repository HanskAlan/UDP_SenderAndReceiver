import model.Parameters;
import model.TransmissionData;
import model.Constant;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

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

            String dstip = parameters.getDestIp();
            InetAddress destIp = InetAddress.getByName(dstip);
            String srcip=parameters.getSrcIp();
            InetAddress srcIp=InetAddress.getByName(srcip);
            int flowId = parameters.getFlowId();
            int flowCount = parameters.getFlowCount();
            int coFlowId = parameters.getCoFlowId();
            dataSize = new AtomicInteger(parameters.getDataSize() * 1024 * 1024);
            DatagramSocket ds = new DatagramSocket(coFlowId);
            new SendWorker(ds, destIp,srcIp, coFlowId, flowId, flowCount).start();
            new ListenWorker(destIp,coFlowId,flowId,flowCount).start();
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

    static class ListenWorker extends Thread {
        private final int coFlowId;
        private final int flowId;
        private final InetAddress destIp;
//        private final DatagramSocket ds;
        private final int flowCount;
//        private String data;

        ListenWorker( InetAddress destIp, int coFlowId, int flowId, int flowCount) {
            //this.ds = ds;
            this.destIp = destIp;
            this.coFlowId = coFlowId;
            this.flowId = flowId;
            this.flowCount = flowCount;
        }
        public void run() {
            try {
//                DatagramSocket ds = new DatagramSocket(5002);
                while(true) { //
                    // 数据缓冲区:
                    System.out.println("start listening");
//                    byte[] buffer = new byte[1024];
//                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//                    ds.receive(packet); // 收取一个UDP数据包

                    DatagramChannel channel = DatagramChannel.open();
                    int receive_port=10000+coFlowId*60+flowId;
                    channel.socket().bind(new InetSocketAddress(receive_port));
                    ByteBuffer buf = ByteBuffer.allocate(256);//这个地方我不确定会不会埋雷，发包的大小应该是以这个256为准的吧
                    buf.clear();
                    SocketAddress s=channel.receive(buf);
                    byte[] b=buf.array();
                    String s1 = new String(b);
                    System.out.println(s1);
                    if(isReceived==false&&s1!=null){
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
        private final InetAddress srcIp;//这里是后来加的，和上面的不对称，所以显得很丑
        private final DatagramSocket ds;
        private final int flowCount;
//        private String data;

        SendWorker(DatagramSocket ds, InetAddress destIp, InetAddress srcIp,int coFlowId, int flowId, int flowCount) {
            this.ds = ds;
            this.destIp = destIp;
            this.srcIp=srcIp;
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
//                String srcIP=ds.getLocalAddress().toString();
                String dstIPtmp=destIp.toString();
                String dstIP=dstIPtmp.substring(1,dstIPtmp.length());//这里是为了去掉ip里面奇怪的斜杠
                String srcIPtmp=srcIp.toString();
                String srcIP=srcIPtmp.substring(1,srcIPtmp.length());

                int destPort=10000+60*coFlowId+flowId;
                while (isReceived==false) {
//                    byte[] buffer = new byte[1024];//这三行代码是为了监听服务端返回的终结数据包
//                    DatagramPacket ending_packet = new DatagramPacket(buffer, buffer.length);
//                    ds.receive(ending_packet);
//                    String data = destIp.toString()+DATA_PREFIX_CO_FLOW_ID + coFlowId + ";" +
//                            DATA_PREFIX_FLOW_COUNT + flowCount + ";" +
//                            DATA_PREFIX_DATA_SIZE + buffSize;
                    String data= new TransmissionData(coFlowId,flowCount,flowId,dataSize.intValue(),srcIP,dstIP,send_port,destPort).toString();//里面调用了很多丑的不行的方法。。。
                    byte[] buff = Arrays.copyOf(data.getBytes(), buffSize);
                    DatagramPacket packet = new DatagramPacket(buff, 0, buff.length, destIp, 5001);
                    ds.send(packet);
//                        i++;
                    System.out.println(i++);

                    if(i>1000){  //这里测试一下我的停止发包的函数
                        packet = new DatagramPacket(buff, 0, buff.length, destIp, destPort);
                        ds.send(packet);
                    }

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
