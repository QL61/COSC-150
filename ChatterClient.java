package chatter;

import java.net.*;
import java.io.*;


public class ChatterClient {

	int publicPort = 55305;
	String ip = "141.161.88.4";
	String username = "ALPHA";
	boolean keepGoing = true;

	public static void main(String[] args) {
		new ChatterClient();
	}

	public ChatterClient() {
		System.out.println("chat client starting ...");

		try
		{
			System.out.println("about to try to call 'localhost' / " + publicPort);

			Socket sock = new Socket("localhost", publicPort);

			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

			InputStream in = sock.getInputStream();
			BufferedReader bin = new BufferedReader( new InputStreamReader(in) );

			OutputStream out = sock.getOutputStream();
			BufferedWriter bout = new BufferedWriter( new OutputStreamWriter( out ) );
			bout.write(username + "\n");
			bout.flush();

			String toSend, received;
			while (keepGoing) {
				toSend = userInput.readLine();
				bout.write(toSend);
				bout.flush();
				if ( (received = bin.readLine()) != null) {
					System.out.print(received);
				}
			}
			// close the socket connection
			sock.close();
		}
		catch ( IOException ioe ) { 
			System.err.println("from ChatterClient(): " + ioe); 
		}
	}
}





//package chatter;
//import java.net.*;
//import java.io.*;
//import java.util.*;
//
///*
// * The Client that can be run both as a console or a GUI
// */
//public class ChatterClient  {
//
//	boolean keepGoing = true;
//
//	// for I/O
//	private BufferedInputStream sInput;		// to read from the socket
//	private PrintWriter sOutput;	// to write on the socket
//	private Socket socket;
//
//	// the server, the port and the username
//	String serverAddress = "localhost", username = "ALPHA";
//	int portNumber = 51500;
//
//	/*
//	 *  Constructor called by console mode
//	 *  server: the server address
//	 *  port: the port number
//	 *  username: the username
//	 */
//
//	ChatterClient(String serverAddress, int portNumber, String username) {
//		this.serverAddress = serverAddress;
//		this.portNumber = portNumber;
//		this.username = username;
//	}
//
//	/*
//	 * To start the dialog
//	 */
//	public boolean start() {
//		// try to connect to the server
//		try {
//			socket = new Socket(serverAddress, portNumber);
//		} 
//		// if it failed not much I can so
//		catch(Exception ec) {
//			System.err.println("Error connectiong to server:" + ec);
//			return false;
//		}
//
//		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
//		System.err.println(msg);
//
//		/* Creating both Data Stream */
//		try
//		{
//			sInput  = new BufferedInputStream(socket.getInputStream());
//			sOutput = new PrintWriter(socket.getOutputStream());
//		}
//		catch (IOException eIO) {
//			System.err.println("Exception creating new Input/output Streams: " + eIO);
//			return false;
//		}
//
//		// creates the Thread to listen from the server 
//		new ListenToServer().start();
//		sOutput.print(username);
//		// success we inform the caller that it worked
//		return true;
//	}
//
//	/*
//	 * To send a message to the server
//	 */
//	void sendMessage(String msg) {
//		sOutput.write(msg);
//	}
//
//	/*
//	 * When something goes wrong
//	 * Close the Input/Output streams and disconnect not much to do in the catch clause
//	 */
//	private void disconnect() {
//		try { 
//			if(sInput != null) sInput.close();
//		}
//		catch(Exception e) {} // not much else I can do
//		try {
//			if(sOutput != null) sOutput.close();
//		}
//		catch(Exception e) {} // not much else I can do
//		try{
//			if(socket != null) socket.close();
//		}
//		catch(Exception e) {} // not much else I can do			
//	}
//	/*
//	 * To start the Client in console mode use one of the following command
//	 * > java Client
//	 * > java Client username
//	 * > java Client username portNumber
//	 * > java Client username portNumber serverAddress
//	 * at the console prompt
//	 * If the portNumber is not specified 1500 is used
//	 * If the serverAddress is not specified "localHost" is used
//	 * If the username is not specified "Anonymous" is used
//	 * > java Client 
//	 * is equivalent to
//	 * > java Client Anonymous 1500 localhost 
//	 * are equivalent
//	 * 
//	 * In console mode, if an error occurs the program simply stops
//	 * when a GUI id used, the GUI is informed of the disconnection
//	 */
//	public static void main(String[] args) {
////		String serverAddress = args[2]; TODO: turn into command line 
////		int portNumber = args[3];
////		String username = username;
//		
//		String serverAddress = "localhost";
//		int portNumber = 55050;
//		String username = "ALPHA";
//		boolean keepGoing = true;
//
//		// create the Client object
//		ChatterClient client = new ChatterClient(serverAddress, portNumber, username);
//		// test if we can start the connection to the Server
//		// if it failed nothing we can do
//		if(!client.start())
//			return;
//
//		// wait for messages from user
//		Scanner scan = new Scanner(System.in);
//		// loop forever for message from the user
//		while(keepGoing) {
//			// read message from user
//			String msg = scan.nextLine();
//			// logout if message is LOGOUT
//			if(msg.equalsIgnoreCase("LOGOUT")) {
//				keepGoing = false;
//			}
//			else {				// default to ordinary message
//				client.sendMessage(msg);
//			}
//		}
//		// done, disconnect
//		scan.close();
//		client.disconnect();	
//	}
//
//	/*
//	 * a class that waits for the message from the server and append them to the JTextArea
//	 * if we have a GUI or simply System.out.println() it in console mode
//	 */
//	class ListenToServer extends Thread {
//
//		public void run() {
//			while(keepGoing) {
//				String msg = sInput.toString();
//				if (msg != null) {
//					System.out.println(msg);
//					System.out.print("> ");
//				}
//			}
//		}
//	}
//}




	