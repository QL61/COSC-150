package chatter;

import java.net.*;
import java.io.*;

public class ChatterServer
{
	ServerSocket sock;
	boolean keepGoing = true;
	int publicPort = 55305;
	

	public static void main( String[] args ) //throws IOException
	{
		new ChatterServer();
	}

	public ChatterServer()
	{
		System.out.println("chat server starting ...");
		try
		{
			sock = new ServerSocket(publicPort); // open socket
			

			while (keepGoing) // has no way to stop as written
			{
				// listen for connections
				Socket client = sock.accept(); // this blocks until a client calls      
				System.out.println("ChatterServer: accepts client connection ");

				InputStream in = client.getInputStream();
				BufferedReader bin = new BufferedReader( new InputStreamReader(in) );
				
				PrintWriter pwriter = new PrintWriter( client.getOutputStream(), true);
				
				String received, toSend;
				if ((received = bin.readLine()) != null) {
					pwriter.print(received);
					pwriter.flush();
				}
			}
			sock.close(); // is actually never called in this code
		}
		catch( Exception e ) { System.err.println("Error '" + e + "' caught in ChatServer()"); }      
		System.exit(0);
	}
}



//package chatter;
//import java.io.*;
//import java.net.*;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///*
// * The server that can be run both as a console application or a GUI
// */
//public class ChatterServer {
//	
//	// an ArrayList to keep list of Clients
//	private ArrayList<ClientThread> threadList;
//	// the port number to listen for connection
//	private int portNumber = 51500;
//	// the boolean that will be turned off to stop the server
//	private boolean keepGoing = true;
//	
//
//	/*
//	 *  server constructor that receive the port to listen to for connection as parameter
//	 *  in console
//	 */
//	public ChatterServer(int port) {
//		// the port
//		this.portNumber = port;
//		// ArrayList for the Client list
//		threadList = new ArrayList<ClientThread>();
//	}
//	
//	public void start() {
//		keepGoing = true;
//		/* create socket server and wait for connection requests */
//		try 
//		{
//			// the socket used by the server
//			ServerSocket serverSocket = new ServerSocket(portNumber);
//
//			// infinite loop to wait for connections
//			while(keepGoing) 
//			{
//				// format message saying we are waiting
//				System.out.println("Server waiting for Clients on port " + portNumber + ".");
//				
//				Socket socket = serverSocket.accept();  	// accept connection
//				
//				// if I was asked to stop
//				if(!keepGoing)
//					break;
//				
//				ClientThread t = new ClientThread(socket);  // make a thread of it
//				threadList.add(t);							// save it in the ArrayList
//				t.start();
//			}
//			
//			// jump to here after leaving while loop (i.e. after being asked to stop)
//			try {
//				serverSocket.close();
//				for(int i = 0; i < threadList.size(); ++i) {
//					ClientThread tc = threadList.get(i);
//					try {
//					tc.sInput.close();
//					tc.sOutput.close();
//					tc.socket.close();
//					}
//					catch(IOException ioE) {
//						// not much I can do
//					}
//				}
//			}
//			catch(Exception e) {
//				System.err.println("Exception closing the server and clients: " + e);
//			}
//		}
//		catch (IOException e) {
//            String msg = " Exception on new ServerSocket: " + e + "\n";
//			System.err.println(msg);
//		}
//	}		
//	
//	/*
//	 *  to broadcast a message to all Clients
//	 */
//	private synchronized void broadcast(String message) {
//		String msg = message;
//		// we loop in reverse order in case we would have to remove a Client
//		// because it has disconnected
//		for(int i = threadList.size(); --i >= 0;) {
//			ClientThread ct = threadList.get(i);
//			// try to write to the Client if it fails remove it from the list
//			if(!ct.writeMsg(msg)) {
//				threadList.remove(i);
//				System.out.println("Disconnected Client " + ct.username + " removed from list.");
//			}
//		}
//	}
//
////	// for a client who logs off using the LOGOUT message
////	synchronized void remove(int id) {
////		// scan the array list until we found the Id
////		for(int i = 0; i < threadList.size(); ++i) {
////			ClientThread ct = threadList.get(i);
////			// found it
////			if(ct.id == id) {
////				threadList.remove(i);
////				return;
////			}
////		}
////	}
//	
//	
//	/*
//	 *  To run as a console application just open a console window and: 
//	 * > java Server
//	 * > java Server portNumber
//	 * If the port number is not specified 1500 is used
//	 */ 
//	public static void main(String[] args) {
//		
//		int portNumber = 55050;
//		
//		// create a server object and start it
//		ChatterServer server = new ChatterServer(portNumber);
//		server.start();
//	}
//
////	/** One instance of this thread will run for each client */
////	class ClientThread extends Thread {
////		// the socket where to listen/talk
////		Socket socket;
////		BufferedInputStream sInput;
////		PrintWriter sOutput;
////
////		// the Username of the Client
////		String username;
////		// the only type of message a will receive
////		String msg;
////
////		// Constructor
////		
////		ClientThread(Socket socket) {
////
////			this.socket = socket;
////			/* Creating data streams */
////			System.out.println("Thread trying to create Object Input/Output Streams");
////			try
////			{
////				// create output first
////				sOutput = new PrintWriter(socket.getOutputStream());
////				sInput  = new BufferedInputStream(socket.getInputStream());
////				// read the username
////				username = sInput.toString();
////				System.out.println(username + " just connected.");
////			}
////			catch (IOException e) {
////				System.err.println("Exception creating new Input/output Streams: " + e);
////				return;
////			}
////		}
////
////		// what will run forever
////		public void run() {
////			// to loop until LOGOUT
////			boolean keepGoing = true;
////			while(keepGoing) {
////				msg = sInput.toString();
////				if (msg != null) {
////					writeMsg(msg);
////				}
////			}
////			// remove myself from the arrayList containing the list of the
////			// connected Clients
////			remove(3); //TODO
////			close();
////		}
////		
////		// try to close everything
////		private void close() {
////			// try to close the connection
////			try {
////				if(sOutput != null) sOutput.close();
////			}
////			catch(Exception e) {}
////			try {
////				if(sInput != null) sInput.close();
////			}
////			catch(Exception e) {};
////			try {
////				if(socket != null) socket.close();
////			}
////			catch (Exception e) {}
////		}
//
//		/*
//		 * Write a String to the Client output stream
//		 */
//		private boolean writeMsg(String msg) {
//			// if Client is still connected send the message to it
//			if(!socket.isConnected()) {
//				close();
//				return false;
//			}
//			sOutput.print(msg);
//			return true;
//		}
//	}
//}
//
//
//
//
