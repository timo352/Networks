package javaping;

import java.net.*;
import java.io.*;

public class udpsocket {

    public static void main(String[] args) throws Exception {

        int PORT = 7;

        if (args.length != 2) {

            //System.out.println("Error server and port required");
        }

        //InetAddress target = InetAddress.getByName(args[0]);
        InetAddress target = InetAddress.getByName("163.11.238.205");

        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Working from: " + ip);

        DatagramSocket socket = new DatagramSocket(7777);

        String message = "PINGER FFACE JGERKHGKESGHKSERJG";
        byte[] buf = message.getBytes();
        System.out.println("Pinging " + target + " with " + buf.length + " bytes of data.");
        long startTime = 0;
	long endTime = 0;
        int sent = 0;
        int failed = 0;
        int received = 0;
        double lost = 0;
        int lostPercent = 0;
        
        
        //socket.connect(new InetSocketAddress(target, PORT));
        
        for (int i = 0; i < 4; i++) {

            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), target, 7);
            socket.send(packet);
            
            startTime = System.currentTimeMillis();
            sent++;
            DatagramPacket response = new DatagramPacket(buf, buf.length);

            socket.setSoTimeout(4000);
            try {
                
                socket.receive(response);
                endTime = System.currentTimeMillis();
               // time /= Math.pow(10, 6);
            } catch (IOException E) {
                
            }
           // if (response.getAddress() != null) {
                System.out.println("Reply from " + response.getAddress() + ": bytes="
                        + (response.getLength()) + " time=" + (endTime-startTime) + "ms");
                received++;
		Thread.sleep(1000);
           /* } else {
               System.out.println("Request timed out.");
               failed++;
          } */
        }
        lost = (failed/sent);
        lostPercent = (int) lost * 100;
        System.out.println("Ping statistics for " + target + ":");
        System.out.println("    Packets: Sent = " + sent + ", Received = " + received +
                ", Lost = " + failed + "(" + lostPercent + "%)");
        if (failed == 0){
            //System
        }

        //System.out.println(response.getData());
        //printData(response);
        System.out.println("done");

    }

}
