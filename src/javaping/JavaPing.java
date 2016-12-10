package JavaPing;

/**
 * JavaPing.java
 * Author: Timothy Smith
 * 
 * A program that simulates the ping program standard on most machines.
 * Uses the UDP Echo Protocol (RFC 862) instead of the normal ICMP.
 * 
 * Computer Networks
 * Professor: Dr. Seth Hamman
 * Due Date: October 20, 2016
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class JavaPing {

	static int serverPort = 7;
	static int numPackets = 4;

	public static void main(String[] args) throws IOException {

		// make sure there is just one argument
		if (args.length != 1) {
			System.out.println("Usage: JavaPing <IP address>");
			System.exit(0);
		}

		// declare these outside of the try-catch block so we can get them later
		DatagramSocket sock = null;
		InetAddress serverAddr = null;
		String serverName = args[0];

		try {
			// initialize socket and serverAddr
			sock = new DatagramSocket(7777);
			serverAddr = InetAddress.getByName(serverName);

			// set the ping timeout to be four seconds
			sock.setSoTimeout(4000);

		} catch (UnknownHostException ex) {
			System.out.println("JavaPing request could not find host " + serverName
					+ ". Please check the name and try again.");
			System.exit(0);
		} catch (SocketException ex) {
			System.out.println(ex.getMessage());
			System.out.println("System exiting...");
			System.exit(1);
		}

		System.out.println("\nPinging " + serverName + " with 32 bytes of data:");

		int lost = 0;
		int sent;
		ArrayList<Long> rttTimes = new ArrayList();

		// send numPackets packets
		for (sent = 0; sent < numPackets; sent++) {
			try {
				// send message to serverAddr/serverPort
				byte[] send = "This is the test JavaPing packet".getBytes(); // 32 bytes
				DatagramPacket sendPacket = new DatagramPacket(send, send.length, serverAddr, serverPort);
				long sendTime = System.currentTimeMillis();
				sock.send(sendPacket);

				// get message back from the server
				byte[] rec = new byte[send.length];
				DatagramPacket receivePacket = new DatagramPacket(rec, rec.length);
				sock.receive(receivePacket);
				long recTime = System.currentTimeMillis();

				long tripTime = recTime - sendTime;
				rttTimes.add(tripTime);

				System.out.println("Reply from "
						+ ipToString(receivePacket.getAddress().getAddress())
						+ ": bytes=" + receivePacket.getData().length
						+ " time=" + tripTime + "ms");

				// sleep for a second before repinging (to mimic ping's behavior)
				if (sent < numPackets - 1) {
					Thread.sleep(1000);
				}

			} catch (SocketTimeoutException ex) {
				System.out.println("Request timed out.");
				lost++;
			} catch (InterruptedException ex) {
				System.out.println(ex.getMessage());
				System.out.println("System exiting...");
				System.exit(1);
			}
		}

		// output all the statistics
		System.out.println("\nPing statistics for "
				+ ipToString(serverAddr.getAddress()) + ":");
		System.out.println("\tPackets: Sent=  " + sent
				+ ", Received = " + (sent - lost)
				+ ", Lost = " + lost
				+ " <" + (int)((double)lost/sent)*100 + "% loss>,");

		// calculate the min, max, and average time if we received any
		if (lost != sent) {
			long avgTime = 0;
			long minTime = Long.MAX_VALUE;
			long maxTime = Long.MIN_VALUE;

			for (int i = 0; i < rttTimes.size(); i++) {
				long temp = rttTimes.get(i);
				avgTime += temp;

				if (temp < minTime) {
					minTime = temp;
				}
				if (temp > maxTime) {
					maxTime = temp;
				}
			}
			avgTime /= rttTimes.size();
			// output the round trip time statistics
			System.out.println("Approximate round trip time in milli-seconds:");
			System.out.println("\tMinimum = " + minTime
					+ "ms, Maximum = " + maxTime
					+ "ms, Average = " + avgTime + "ms\n");
		}

		sock.close();
	}

	// function that turns the byte array into the normal 255.255.255.255 format
	private static String ipToString(byte[] b) {
		int a1 = (b[0] < 0) ? b[0] + 256 : b[0];
		int a2 = (b[1] < 0) ? b[1] + 256 : b[1];
		int a3 = (b[2] < 0) ? b[2] + 256 : b[2];
		int a4 = (b[3] < 0) ? b[3] + 256 : b[3];

		return a1 + "." + a2 + "." + a3 + "." + a4;
	}

}
