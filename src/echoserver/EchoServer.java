package echoserver;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class EchoServer {

	public static void main(String[] args) {
		int PORT = 33333;
		ServerSocket welcomeSocket;
		try {
			welcomeSocket = new ServerSocket(PORT);
			System.out.println("LISTENING ON PORT: " + PORT);

			// blocks at next line until a connection arrives
			Socket connection = welcomeSocket.accept();
			// receive message from client
			Scanner in = new Scanner(connection.getInputStream());			
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			
			String message = in.nextLine();
			while (!message.equals("QUIT")) {
				System.out.println("RCVD: " + message);
				message = in.nextLine();
				out.writeBytes("HELLO\n");
				System.out.println("SEND: " + message);
			}
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
