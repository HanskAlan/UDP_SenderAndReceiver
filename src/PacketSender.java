import model.Parameters;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * send packets
 * @author yy
 */
public class PacketSender {
    private static final String DATA_PREFIX_CO_FLOW_ID = "co_flow_id=";
    private static final String DATA_PREFIX_FLOW_COUNT = "flow_count=";
    private static final String DATA_PREFIX_DATA_SIZE = "data_size=";
    private static final int BUFF_SIZE = 1024;
    private static volatile AtomicInteger dataSize;

    public static void main(String[] args) {
        try {
            Parameters parameters = Parameters.getParameters(args);
            checkParam(parameters);

            String ip = parameters.getDestIp();
            InetAddress destIp = InetAddress.getByName(ip);
            int startFlowId = parameters.getFlowId();
            int flowCount = parameters.getFlowCount();
            int coFlowId = parameters.getCoFlowId();
            dataSize = new AtomicInteger(parameters.getDataSize() * 1024 * 1024);

            CountDownLatch countDownLatch = new CountDownLatch(flowCount);
            DatagramSocket ds = new DatagramSocket(coFlowId);
            for (int i = 1; i <= flowCount; i++) {
                int flowId = startFlowId + i - 1;
                new SendWorker(countDownLatch, ds, destIp, coFlowId, flowId, flowCount).start();
            }
            countDownLatch.await();
            System.out.println("数据包发送完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkParam(Parameters parameters) throws Exception {
        if (null == parameters || null == parameters.getDestIp()) {
            throw new Exception("Invalid parameter");
        }
    }

    static class SendWorker extends Thread {

        private final CountDownLatch countDownLatch;
        private final int coFlowId;
        private final int flowId;
        private final InetAddress destIp;
        private final DatagramSocket ds;
        private final int flowCount;
//        private String data;

        SendWorker(CountDownLatch countDownLatch, DatagramSocket ds, InetAddress destIp, int coFlowId, int flowId, int flowCount){
            this.countDownLatch = countDownLatch;
            this.ds = ds;
            this.destIp = destIp;
            this.coFlowId = coFlowId;
            this.flowId = flowId;
			this.flowCount = flowCount;
        }

        @Override
        public void run() {
            try {
                while (dataSize.get() > 0) {
                    int buffSize;
                    if (dataSize.get() > BUFF_SIZE) {
                        buffSize = BUFF_SIZE;
                        dataSize.getAndAdd(-BUFF_SIZE);
                    } else {
                        buffSize = dataSize.get();
                        dataSize.getAndSet(0);
                    }
                    String data = DATA_PREFIX_CO_FLOW_ID + coFlowId +";"+
                            DATA_PREFIX_FLOW_COUNT + flowCount + ";"+
                            DATA_PREFIX_DATA_SIZE + dataSize;
                    byte[] buff = Arrays.copyOf(data.getBytes(), buffSize);
                    DatagramPacket packet = new DatagramPacket(buff, 0, buff.length, destIp, flowId);
                    ds.send(packet);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("数据发送异常, coFlowId = " + coFlowId + " flowId = " + flowId);
            } finally {
                System.out.println("协流" + coFlowId + " 子流" + flowId + " 发送完成");
                countDownLatch.countDown();
            }
        }
    }
}
