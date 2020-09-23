//import model.Constant;
//import model.Parameters;
//import model.TransmissionData;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.util.Arrays;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * 手动发包，测试用
// * @author Huan
// */
//public class PacketSender {
//    private static final String DATA_PREFIX_CO_FLOW_ID = "co_flow_id=";
//    private static final String DATA_PREFIX_FLOW_COUNT = "flow_count=";
//    private static final String DATA_PREFIX_DATA_SIZE = "data_size=";
//    private static final int BUFF_SIZE = 1024;
//    private static volatile AtomicInteger dataSize;
//
//    public static void main(String[] args) {
//        try {
//            TransmissionData tData = new TransmissionData(
//                    1,1,1,1,
//                    "192.168.2.119",
//                    "192.168.2.188",
//                    65535 - Constant.hash(1,1),
//                    5001
//            );
//
//            DatagramSocket socket = new DatagramSocket(65535 - tData.hashCode());
//            for(int i = 0; i < tData.data_size; ){
//                try {
//                    byte[] buff = Arrays.copyOf(tData.toString().getBytes(), Constant.BUFF_SIZE);
//                    // Client的接受端口是10000 + hash
//                    DatagramPacket packet = new DatagramPacket(
//                            buff, 0, buff.length,
//                            InetAddress.getByName(tData.dst_ip),
//                            tData.dst_port
//                    );
//                    socket.send(packet);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
