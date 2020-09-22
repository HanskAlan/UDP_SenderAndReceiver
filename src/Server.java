//import model.Constant;
//import model.Parameters;
//
//import java.io.*;
//import java.net.*;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * send packets
// * @author yy
// */
//public class Server {
//    public static void main(String[] args) {
//        try {
//            System.out.println("Server is running and waiting for udp data");
//            DatagramSocket socket = null;
//            Map<Integer,byte[]> byteMap = new HashMap<>();
//            Map<Integer,Integer> sum
//
//            socket = new DatagramSocket(Constant.ServerPort);
//            while(true){
//                DatagramPacket packet = new DatagramPacket(new byte[Constant.BUFF_SIZE], Constant.BUFF_SIZE);
//                try {
//                    // 据说此方法接收到数据报之前会一直阻塞
//                    socket.receive(packet);
//                    SocketAddress socketAddress = packet.getSocketAddress();
//                    System.out.println("UDP包的接受IP" + socketAddress.toString());
//
//                    byte[] data = packet.getData();
//                    int coflowID = 1;
//                    int flowID = 1;
//                    byteMap.put(coflowID * 100 + flowID,data);
//                } catch (IOException e) {
//                    System.err.println("Server发生异常,socket接受数据包失败");
//                    e.printStackTrace();
//                    socket.close();
//                    socket = new DatagramSocket(Constant.ServerPort);
//                }
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void ACK(){
//
//    }
//}
//
//class dataSave{
//    int coflowId,flowCount;
//    int flowId,dataSize;
//    String srcIP,dstIP;
//    int srcPort,dstPort;
//    dataSave(int coflowId,int flowId,int dataSize)
//}
//
//
