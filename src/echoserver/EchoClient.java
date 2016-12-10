package echoserver;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/* $Id: MailClient.java,v 1.7 1999/07/22 12:07:30 kangasha Exp $ */
/**
 * A simple mail client with a GUI for sending mail.
 *
 * @author Jussi Kangasharju
 */
public class EchoClient extends Frame {

	/* The stuff for the GUI. */
	private Button btSend = new Button("Send");
	private Button btClear = new Button("Clear");
	private Button btQuit = new Button("Quit");
	private TextArea messageText = new TextArea(10, 40);
	private TextField sendingText = new TextField();
	Socket connection;
	BufferedReader fromServer;
	DataOutputStream toServer;

	/**
	 * Create a new MailClient window with fields for entering all the relevant
	 * information (From, To, Subject, and message).
	 */
	public EchoClient() {
		super("Timmy Talker");

		/* Create panels for holding the fields. To make it look nice,
		 create an extra panel for holding all the child panels. */
		Panel messagePanel = new Panel(new BorderLayout());
		messagePanel.add(messageText, BorderLayout.CENTER);
		Panel sendingPanel = new Panel(new BorderLayout());
		sendingPanel.add(sendingText, BorderLayout.CENTER);

		/* Create a panel for the buttons and add listeners to the
		 buttons. */
		Panel buttonPanel = new Panel(new GridLayout(1, 0));
		btSend.addActionListener(new SendListener());
		btClear.addActionListener(new ClearListener());
		btQuit.addActionListener(new QuitListener());
		buttonPanel.add(btSend);
		buttonPanel.add(btClear);
		buttonPanel.add(btQuit);

		messageText.setEditable(false);

		/* Add, pack, and show. */
		add(sendingPanel, BorderLayout.NORTH);
		add(messagePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		pack();
		setVisible(true);

		try {
			connection = new Socket("127.0.0.1", 33333);
			fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			toServer = new DataOutputStream(connection.getOutputStream());

			toServer.writeBytes("HELLO\n");

			if (!"HELLO".equals(fromServer.readLine())) {
				System.exit(1);
			}
		} catch (IOException ex) {

		}
	}

	static public void main(String argv[]) {
		new EchoClient();
	}

	/* Handler for the Send-button. */
	class SendListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			try {
				toServer.writeBytes(sendingText.getText() + "\n");
				String message = fromServer.readLine();
				System.out.println("FROM SERVER: " + message);
				messageText.setText(messageText.getText() + sendingText.getText() + "\n" + message + "\n");
				sendingText.setText("");
			} catch (IOException ex) {
			}
		}
	}

	/* Clear the fields on the GUI. */
	class ClearListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			System.out.println("Clearing fields");
			messageText.setText("");
		}
	}

	/* Quit. */
	class QuitListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
}
