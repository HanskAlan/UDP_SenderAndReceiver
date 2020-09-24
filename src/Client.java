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
 * @author yy,hsk,aberror
 */

public class Client {
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

            CountDownLatch countDownLatch = new CountDownLatch(2);
            // 先启动listen，再启动send
            System.out.printf("Start the client(%d,%d)\n",coFlowId,flowId);
            new ListenWorker(countDownLatch,coFlowId,flowId).start();
            new SendWorker(countDownLatch, destIp,srcIp, coFlowId, flowId, flowCount, parameters.getRate()).start();
            countDownLatch.await();
//            System.out.printf("Close the client(%d,%d)\n",coFlowId,flowId);
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
        CountDownLatch countDownLatch;

        ListenWorker(CountDownLatch countDownLatch, int coFlowId, int flowId) {
            this.coFlowId = coFlowId;
            this.flowId = flowId;
            this.countDownLatch = countDownLatch;
        }
        public void run() {
            try {
                DatagramChannel channel = DatagramChannel.open();
                int receive_port = 10000 + Constant.hash(coFlowId,flowId);
                channel.socket().bind(new InetSocketAddress(receive_port));
                System.out.println("start listening");

                ByteBuffer buf = ByteBuffer.allocate(256); // 这个地方我不确定会不会埋雷，发包的大小应该是以这个256为准的吧
                buf.clear();
                SocketAddress s = channel.receive(buf); // 没有接受到的时候会挂起
                byte[] b = buf.array();
                System.out.println("Receive ack packet : " + new String(b));
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("exception happened during listening");
            } finally {
                isReceived = true;
                countDownLatch.countDown();
            }
        }
    }

    static class SendWorker extends Thread {
        private final int coFlowId;
        private final int flowId;
        private final InetAddress destIp;
        private final InetAddress srcIp;//这里是后来加的，和上面的不对称，所以显得很丑
//        private final DatagramSocket ds;
        private final int flowCount;
        private final long rate;
        //        private String data;
        private final CountDownLatch countDownLatch;

        SendWorker(CountDownLatch countDownLatch, InetAddress destIp, InetAddress srcIp, int coFlowId, int flowId, int flowCount, long rate) {
//            this.ds = ds;
            this.destIp = destIp;
            this.srcIp = srcIp;
            this.coFlowId = coFlowId;
            this.flowId = flowId;
            this.flowCount = flowCount;
            this.rate = rate;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            int i = 0;
            isReceived=false;
            try {
                int send_port=65535 - coFlowId * 100 - flowId;
                DatagramSocket ds = new DatagramSocket(send_port); // 指定发送端口
//                String srcIP=ds.getLocalAddress().toString();
                String dstIPtmp=destIp.toString();
                String dstIP=dstIPtmp.substring(1,dstIPtmp.length());//这里是为了去掉ip里面奇怪的斜杠
                String srcIPtmp=srcIp.toString();
                String srcIP=srcIPtmp.substring(1,srcIPtmp.length());


                int destPort = Constant.SERVER_PORT;
                long startTime = System.currentTimeMillis(),nowTime = startTime;
                while (!isReceived) {
//                    byte[] buffer = new byte[1024];//这三行代码是为了监听服务端返回的终结数据包
//                    DatagramPacket ending_packet = new DatagramPacket(buffer, buffer.length);
//                    ds.receive(ending_packet);
//                    String data = destIp.toString()+DATA_PREFIX_CO_FLOW_ID + coFlowId + ";" +
//                            DATA_PREFIX_FLOW_COUNT + flowCount + ";" +
//                            DATA_PREFIX_DATA_SIZE + buffSize;
                    String data = new TransmissionData(coFlowId,flowCount,flowId,dataSize.intValue(),srcIP,dstIP,send_port,destPort).toString();//里面调用了很多丑的不行的方法。。。

                    byte[] buff = Arrays.copyOf(data.getBytes(), Constant.BUFF_SIZE);
                    DatagramPacket packet = new DatagramPacket(buff, 0, buff.length, destIp, 5001);
                    ds.send(packet);
                    i++;
//                    if(i++ % 1 == 0){
//                        System.out.println(i);
//                    }

//                    if(i>1000){  //这里测试一下我的停止发包的函数
//                        packet = new DatagramPacket(buff, 0, buff.length, destIp, destPort);
//                        ds.send(packet);
//                    }
                    // 控制传输速度
                    nowTime = System.currentTimeMillis();
                    if(((long) i * buff.length) / (nowTime - startTime) > rate * 1000L){
                        // intelij的提示忙等待，不清楚是怎么回事
                        System.out.printf("time : %d,packet : %d\n",System.currentTimeMillis() - startTime,i);
                        Thread.sleep(Constant.CLIENT_SLEEP_TIME);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("exception happened during sending, coFlowId = " + coFlowId + " flowId = " + flowId);
            } finally {
                System.out.println("coflow" + coFlowId + " flow" + flowId + " complete sending");
                System.out.println("Total packet : " + i + ", and total transmission : " + i * Constant.BUFF_SIZE);
                countDownLatch.countDown();
            }
        }
    }
}
