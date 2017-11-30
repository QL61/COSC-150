package go;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

/**
 * ChatterClient class
 * 
 * Is used by ChatterClientGUI to process user input; communicates with server through Socket
 * connection to send and receive ChatterMessages and SArrays
 * 
 * @author Christian Collier (chc46), Qingyue Li (ql61), Mark Ozdemir (mo732)
 */
public class GoClient {
	
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 55305;
	
	private String username = "player"; //default username
	private ArrayList<String> nameList = new ArrayList<String>();
	private int port;
	private String ip;
	private Socket sock;
	private PrintWriter pw;
	private BufferedReader br;
	private GoClientGUI goGUI;	
	private boolean keepGoing = true;
	private GomokuProtocol protocol;

	// run once for each user who wants to use the chat
	public static void main(String[] args) {
		if ( args.length >= 2 ){
			String serverAddress = args[0];
			int portNumber = Integer.parseInt(args[1]);
			new GoClientGUI(serverAddress, portNumber);
		}
		else new GoClientGUI(DEFAULT_HOST, DEFAULT_PORT);
	}

	// ChatterClient constructor
	public GoClient(String server, int portNumber, String clientName, GoClientGUI gg) {
		this.ip = server;
		this.port = portNumber;
		this.username = clientName;
		this.goGUI = gg;
	}
	
	// connects socket and creates input and output streams
	public boolean start() {
		System.out.println("go client started...");
		boolean startValue = true;
		keepGoing = true; 
		
		try
		{
			//System.out.println("about to try to call 'localhost' / " + port);
			sock = new Socket(ip, port);
			pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));

			//System.out.println("setting username: " + cGUI.getUsername());
			//TODO setUsername(GoClientGUI.getUsername());
			pw.write(protocol.generateChangeNameMessage(username, username));
			pw.flush();
			
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			//System.out.println("creating listen thread");
			Thread listenThread = new Thread(new Listen());
			listenThread.start();
		}
		catch ( IOException ioe ) { System.err.println("Client connection was closed."); }
		
		return startValue;
	}

	// called when user attempts to change username
	protected void setUsername(String newName) {		
		//Use the new protocol for the game nickname changes
		String msg = GomokuProtocol.generateChangeNameMessage(username, newName);
		pw.write(msg);
		pw.flush();
	}

	// function to send message through output stream
	public void sendMessage(String msg) {
		pw.write(msg);
		pw.flush();
	}
	
	//get the list of names
	protected ArrayList<String> getNameList() {
		return nameList;
	}
	
	//get the current client username
	protected String getClientUsername(){
		return this.username;
	}

	// thread that runs to listen for new messages and add them to chat history list if
	// they are public messages or private messages meant for this client
	class Listen implements Runnable {
		@Override
		public void run() {
			//System.out.println("Listen running...");

			try {
				while (keepGoing) {
					String msgReceived = br.readLine();
					System.out.println("Received: " + msgReceived);
					
			        if (GomokuProtocol.isPlayMessage(msgReceived)) {
			            int[] detail = GomokuProtocol.getPlayDetail(msgReceived);
			            // black is 1 and white is 0
			            System.out.println("color is " + detail[0]);
			            System.out.println("row is " + detail[1]);
			            System.out.println("col is " + detail[2]);
			        }
			        
			        if (GomokuProtocol.isChatMessage(msgReceived)) {
			            String[] detail = GomokuProtocol.getChatDetail(msgReceived);
			            // black is 1 and white is 0
			            System.out.println("sender is " + detail[0]);
			            System.out.println("chat message is " + detail[1]);
			        }
			        
			        if (GomokuProtocol.isChangeNameMessage(msgReceived)) {
			            String[] detail = GomokuProtocol.getChangeNameDetail(msgReceived);
			            System.out.println("old name is " + detail[0]);
			            System.out.println("new name is " + detail[1]);
			            // TODO changeName on GUI;
			        }
			        
			        if (GomokuProtocol.isResetMessage(msgReceived)) {
			            // black is 1 and white is 0
			            System.out.println("resetting game");
			            //TODO reset game on GUI
			        }
				} // END while
			} // END try 
			catch(SocketException se) {
				System.err.println("caught: " + se + " from ");
				se.printStackTrace();
			} // END catch 
			catch (IOException ioe) {
				System.err.println("\n\n\nUser connection was closed");
			} // END catch
		} // END public void run()
	} // END class Listen
	
	
	////////////////////////////////////////////////////////////		new methods recently added		////////////////////////////////////////
	
	public void resetGame(String msgToServer) {	}
	public void giveUpGame(String msgtoServer) { }
	public void getColor(String msgFromServer) { }
	public void isWin(String msgFromServer) { }
	public void isLost(String msgFromServer) { }
	public void setMyMove(String msgToServer) {	}
	public void getOpponent(String msg) { }
	public void useProtocol(String msg) { }
	public void selectMode(String msg) { }
	
	
} // END public class ChatterClient