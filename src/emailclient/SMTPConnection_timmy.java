package emailclient;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Open an SMTP connection to a mailserver and send one mail.
 *
 */
public class SMTPConnection_timmy {

	/* The socket to the server */
	private Socket connection;

	/* Streams for reading and writing the socket */
	private BufferedReader fromServer;
	private DataOutputStream toServer;

	private static final int SMTP_PORT = 25;
	private static final String CRLF = "\r\n";

	/* Are we connected? Used in close() to determine what to do. */
	private boolean isConnected = false;

	/* Create an SMTPConnection_timmy object. Create the socket and the 
	 associated streams. Initialize SMTP connection. */
	public SMTPConnection_timmy(Envelope envelope) throws IOException {
		connection = new Socket(envelope.DestAddr, SMTP_PORT);
		fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		toServer = new DataOutputStream(connection.getOutputStream());

		/* Fill in */
		/* Read a line from server and check that the reply code is 220.
		 If not, throw an IOException. */
		String line = fromServer.readLine();
		System.out.println("RCVD: " + line);

		if (parseReply(line) != 220) {
			throw new IOException();
		}

		/* SMTP handshake. We need the name of the local machine.
		 Send the appropriate SMTP handshake command. */
		String localhost = "pc177253.cedarville.edu";
		sendCommand("HELO " + localhost, 250);

		isConnected = true;
	}

	/* Send the message. Write the correct SMTP-commands in the
	 correct order. No checking for errors, just throw them to the
	 caller. */
	public void send(Envelope envelope) throws IOException {
		/* Send all the necessary commands to send a message. Call
		 sendCommand() to do the dirty work. Do _not_ catch the
		 exception thrown from sendCommand(). */

		// MAIL
		sendCommand("MAIL FROM:<" + envelope.Sender + ">", 250);

		// RCPT
		sendCommand("RCPT TO:<" + envelope.Recipient + ">", 250);
		
		// CC
		for(int i=0; i<envelope.Cc.length; i++){
			sendCommand("RCPT TO:<" + envelope.Cc[i] + ">", 250);
		}

		// DATA
		sendCommand("DATA", 354);

		// CONTENTS OF MESSAGE
		sendCommand(envelope.Message.toString() + CRLF + ".", 250);
	}

	/* Close the connection. First, terminate on SMTP level, then
	 close the socket. */
	public void close() {
		isConnected = false;
		try {
			sendCommand("QUIT", 221);
			connection.close();
		} catch (IOException e) {
			System.out.println("Unable to close connection: " + e);
			isConnected = true;
		}
	}

	/* Send an SMTP command to the server. Check that the reply code is
	 what is is supposed to be according to RFC 821. */
	private void sendCommand(String command, int rc) throws IOException {
		/* Write command to server and read reply from server. */
		System.out.println("SENT: " + command);
		toServer.writeBytes(command + CRLF);
		
		
		String reply = fromServer.readLine();
		System.out.println("RCVD: " + reply);
		/* Check that the server's reply code is the same as the parameter
		 rc. If not, throw an IOException. */
		if (parseReply(reply) != rc) {
			throw new IOException();
		}
	}

	/* Parse the reply line from the server. Returns the reply code. */
	private int parseReply(String reply) {
		return Integer.valueOf(reply.substring(0, 3));
	}

	/* Destructor. Closes the connection if something bad happens. */
	protected void finalize() throws Throwable {
		if (isConnected) {
			close();
		}
		super.finalize();
	}
}
