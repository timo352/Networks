package emailclient;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Open an SMTP connection to a mailserver and send one mail.
 *
 */
public class SMTPConnection {
    /* The socket to the server */

    private Socket connection;

    /* Streams for reading and writing the socket */
    private BufferedReader fromServer;
    private DataOutputStream toServer;
    private String reply;

    private static final int SMTP_PORT = 25;
    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do. */
    private boolean isConnected = false;

    /* Create an SMTPConnection object. Create the socket and the 
     associated streams. Initialize SMTP connection. */
    public SMTPConnection(Envelope envelope) throws IOException {
        connection = new Socket(envelope.DestAddr, SMTP_PORT);
        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new DataOutputStream(connection.getOutputStream());
        Scanner scanner = new Scanner(fromServer);
        int rply = 0;
        String hey = "";
        /* Fill in */
        
        //Read a line from server and check that the reply code is 220.
        try {
          
            reply = scanner.nextLine();
            rply = parseReply(reply);
            System.out.println("rply: " + rply);
            if (rply == 220) {
                System.out.println("Success!");
            }
            else {
              throw new IOException();
            }
            
        } catch (IOException e) {
            System.out.println("Invalid reply code: " + rply + e);
            isConnected = true;
            connection.close();
        }
        // If not, throw an IOException. */

        /* SMTP handshake. We need the name of the local machine.
         Send the appropriate SMTP handshake command. */
        String localhost = InetAddress.getLocalHost().getHostName();
        sendCommand("HELO " + localhost + CRLF, 250);
		
        isConnected = true;
        scanner.close();
    }

    /* Send the message. Write the correct SMTP-commands in the
     correct order. No checking for errors, just throw them to the
     caller. */
    public void send(Envelope envelope) throws IOException {
        /* Fill in */
        sendCommand("MAIL FROM: " + envelope.Sender + CRLF, 250);
        sendCommand("RCPT TO: " + envelope.Recipient + CRLF, 250);
        sendCommand("DATA" + CRLF, 354);
		// TIMMY ADDED THIS LINE
		sendCommand(envelope.Message.toString() + CRLF + "." + CRLF, 250);
        /* Send all the necessary commands to send a message. Call
         sendCommand() to do the dirty work. Do _not_ catch the
         exception thrown from sendCommand(). */
        /* Fill in */
    }

    /* Close the connection. First, terminate on SMTP level, then
     close the socket. */
    public void close() {
        isConnected = false;
        try {
            sendCommand("QUIT" + CRLF, 221);
            connection.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection: " + e);
            isConnected = true;
        }
    }

    /* Send an SMTP command to the server. Check that the reply code is
     what is is supposed to be according to RFC 821. */
    private void sendCommand(String command, int rc) throws IOException {
        /* Fill in */
        String reply = "";
        int rply = 0;
        
        try {
			// TIMMY SWITCHED THIS TO BYTES
            toServer.writeBytes(command);
            reply = fromServer.readLine();
            rply = parseReply(reply);
            System.out.println("Command: " + command + "Reply: " + reply + " rply: " + rply);
            if (rply != rc) {
                throw new IOException();
            }
        }
         catch (IOException e) {
            System.out.println("Wrong reply code, buddy: " + e);
            isConnected = true;
            connection.close();
        }

    }

    /* Parse the reply line from the server. Returns the reply code. */
    private int parseReply(String reply) {
        int num = Integer.parseInt(reply.substring(0, 3));
        return num;

    }

    /* Destructor. Closes the connection if something bad happens. */
    protected void finalize() throws Throwable {
        if (isConnected) {
            close();
        }
        super.finalize();
    }
}