package go;


import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.Color;
import java.io.*;

/**
 * GoServer class
 * 
 * 
 * The server is responsible for establishing Socket connections with each client,
 * receiving and parsing sent to it by clients, and processing strings, including by
 * sending strings to their appropriate recipients
 * 
 * @author Christian Collier (chc46), Qingyue Li (ql61), Mark Ozdemir (mo732)
 */
public class GoServer
{

	private static final String WELCOME_MESSAGE = "Welcome to Go. Messages from other users will appear below. "
			+ "Select a username from the drop-down menu on the top right to begin a private chat with another user.\n\n";
	private static final int DEFAULT_PORT = 55305;
	private ServerSocket sock;
	private boolean keepGoing = true;
	private int publicPort;
	private ArrayList<GoClientThread> clientList = new ArrayList<GoClientThread>();
	private ArrayList<String> clientNameList = new ArrayList<String>();
	// Thread unique ID for easy disconnect & nickname updates
	private static int threadIdcounter = 1;

	private boolean blackTaken = false;
	private boolean whiteTaken = false;
	private SparseMatrix occupied = new SparseMatrix();
	private static int BLACK = 1;
	private static int WHITE = 0;
	private int whoseTurn = BLACK;

	
	// run to start server and make it ready to accept clients
	public static void main( String[] args ) //throws IOException
	{
		if (args.length >= 1) {
			int portArg = Integer.parseInt(args[0]);
			new GoServer(portArg);			
		}
		else new GoServer(DEFAULT_PORT);
	}

	// GoServer constructor
	public GoServer(int port)
	{
		System.out.println("chat server started...");
		try
		{
			publicPort = port; //set port to passed port number
			sock = new ServerSocket(publicPort); // open socket

			while (keepGoing)
			{	
				// listen for connections
				Socket client = sock.accept(); // this blocks until a client calls      
				System.out.println("GoServer >> accepts client connection ");

				GoClientThread threadToAdd = new GoClientThread(client);
				//System.out.println("adding GoClientThread to ArrayList");
				clientList.add(threadToAdd);
				//System.out.println("starting GoClientThread");
				threadToAdd.start();

			}

			sock.close();
		}
		catch( Exception e ) { 
			System.err.println("Error: Caught in GoServer(): " + e +" from ");
			e.printStackTrace();
		}      
		System.exit(0);
	}

	// thread that is created once for each new client
	class GoClientThread extends Thread {

		private Socket sock;
		private String username = "";

		protected BufferedReader br;
		protected PrintWriter pw;
		protected GomokuProtocol protocol;
		
		protected boolean keepGoing = true;
		int threadID;		// Thread unique ID for easy disconnect & nickname updates

		GoClientThread(Socket s) {
			System.out.println("ClientThread constructor executing");
			sock = s;
			try {
				// set up output stream and assign color to player
				pw = new PrintWriter(sock.getOutputStream());
				if ((blackTaken == false) && whiteTaken == false) {
					if (assignColor() == Color.BLACK) {
						pw.write(protocol.generateSetBlackColorMessage());
						pw.flush();
						blackTaken = true;
					}
					else {
						pw.write(protocol.generateSetWhiteColorMessage());
						pw.flush();
						whiteTaken = true;
					}
				}
				else if ((blackTaken == true) && (whiteTaken == false)) {
					pw.write(protocol.generateSetWhiteColorMessage());
					pw.flush();
					whiteTaken = true;	
				}
				else if ((blackTaken == false) && (whiteTaken == true)) {
					pw.write(protocol.generateSetBlackColorMessage());
					pw.flush();
					whiteTaken = true;	
				}
				
				br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				
				//assign a unique thread ID
				threadID = ++threadIdcounter; 

			}
			catch(IOException ioe) {
				System.err.println("Caught in ClientThread constructor: " + ioe + " from ");
				ioe.printStackTrace();
			} 
		}

		// accepts and delivers messages from associated client
		@Override
		public void run() {
			System.out.println("ClientThread running...");
			try {
				while (keepGoing) {
										
					//remove GoMessage for the game
					//GoMessage cm = (GoMessage) oIn.readObject();
					String msgReceived = br.readLine();
					System.out.println(msgReceived);
					
			        if (GomokuProtocol.isPlayMessage(msgReceived)) {
			            int[] detail = GomokuProtocol.getPlayDetail(msgReceived);
			            // black is 1 and white is 0
			            System.out.println("color is " + detail[0]);
			            System.out.println("row is " + detail[1]);
			            System.out.println("col is " + detail[2]);
			            if ((detail[0] == whoseTurn) && occupied.isFree(detail[1], detail[2])) {
			            	sendOut(msgReceived);
			            }
			            // TODO if space is already occupied or if playing out of turn, let client know
			        }
			        
			        if (GomokuProtocol.isChatMessage(msgReceived)) {
			            String[] detail = GomokuProtocol.getChatDetail(msgReceived);
			            // black is 1 and white is 0
			            System.out.println("sender is " + detail[0]);
			            System.out.println("chat message is " + detail[1]);
			            // TODO: output on GUI
			        }
			        
			        if (GomokuProtocol.isChangeNameMessage(msgReceived)) {
			            String[] detail = GomokuProtocol.getChangeNameDetail(msgReceived);
			            // black is 1 and white is 0
			            System.out.println("old name is " + detail[0]);
			            System.out.println("new name is " + detail[1]);
			            changeName(detail[1]);
			        }
			        
			        if (GomokuProtocol.isGiveupMessage(msgReceived)) {
			            String[] detail = GomokuProtocol.getChangeNameDetail(msgReceived);
			            // black is 1 and white is 0
			            System.out.println("old name is " + detail[0]);
			            System.out.println("new name is " + detail[1]);
			            //TODO
			        }
			        
			        if (GomokuProtocol.isResetMessage(msgReceived)) {
			            String[] detail = GomokuProtocol.getChangeNameDetail(msgReceived);
			            // black is 1 and white is 0
			            System.out.println("old name is " + detail[0]);
			            System.out.println("new name is " + detail[1]);
			            //TODO
			        }
			        
			        


					// exit thread
					// If client exits, terminate the thread
			        /*
					else if (cm.getMessageType().equalsIgnoreCase(GoMessage.EXIT)) {
						//Create a notification message for the log off event
						GoMessage cmExit = new GoMessage("public", "SERVER >>", "", username + " left the chat\n\n");
						sendOut(cmExit);
						keepGoing = false;
						break;
					}
					*/
					//else { sendOut(cm); }
				}

				//exit thread
				disconnectClient(threadID);
				disconnectThread();		
			}

			catch (IOException ioe) {
				System.err.println("Error: Caught in ClientThread.run: " + ioe + " from ");
				ioe.printStackTrace();
			}
		}

		// creates message indicating that user wants to change name
		private void changeName(String newName) {
			//System.out.println("\tchanging username");

			boolean nameFound = false;
			if (!clientNameList.isEmpty()) {

				for (int i = 0; (i < clientNameList.size()) && (!nameFound); i++) {
					String nameInList = clientNameList.get(i);
					nameInList.trim();
					//System.out.println(nameInList  + " is the name in list currently"); 

					if (nameInList.equals(username.trim())) {
						clientNameList.set(i, newName);	
						nameFound = true;
						//System.out.println(clientNameList.get(i)  + " is the new name that has been set"); 
					}
				}
				if (!nameFound) {
					clientNameList.add(newName);					
				}
			}
			else if (clientNameList.isEmpty()) {
				//System.out.println("client list is empty in server");
				clientNameList.add(newName);
			}
			this.username = newName;
			updateUserNameList();
		}

		// if a client changes its name, this is called to update the list of client names
		private void updateUserNameList() {
			try {
				for (int i = 0; i < clientList.size(); i++) {
					//System.out.println("\tsending to " + clientList.get(i).username);
					SArray toSend = new SArray(clientNameList);
					GoClientThread gcThread = clientList.get(i);
					gcThread.pw.write(toSend); 	// Demeter violation, but using	
					gcThread.pw.flush(); 					// fairly dependable objects
				}
			}
			catch(IOException ioe) {
				System.err.println("Error: Caught: " + ioe + " from ");
				ioe.printStackTrace();
			}
		}

		// exit
		// remove chatter client thread upon user exits
		private synchronized void disconnectClient(int threadID) {
			// update 
			for(int i = 0; i < clientList.size(); ++i) {
				GoClientThread ct = clientList.get(i);
				// check to see if client thread matches the passed thread id
				if(ct.threadID == threadID) {
					clientList.remove(i);	
					//remove the name from the client name list
					String nameToRemove = this.username;  			
					int indexToRemove = clientNameList.indexOf(nameToRemove);
					if ( indexToRemove != -1){
						clientNameList.remove(indexToRemove);
						//System.out.println("removed: " + nameToRemove);
					}		
					//Update the Name List in all active threads
					updateUserNameList(); 						
					return;
				}
			}
		}

		// try to disconnect the thread
		private void disconnectThread() {
			// try to close the connection
			try {
				if(pw != null) pw.close();
			}
			catch(Exception e) {}
			try {
				if(br != null) br.close();
			}
			catch(Exception e) {};
			try {
				if(sock != null) sock.close();
			}
			catch (Exception e) {}
		}

		//sends out message to the clients
	/*	
		private synchronized void sendOut(GoMessage cm) {
			try {
				//System.out.println("sending out a message: '" + cm.getMessage() + "'");
				if (cm.getMessageType().equalsIgnoreCase("public")) {
					//System.out.println("\tdetermined that message is public");
					for (int i = 0; i < clientList.size(); i++) {
						//System.out.println("\tsending to " + clientList.get(i).username);
						GoClientThread ccThread = clientList.get(i);
						ccThread.oOut.writeObject(cm); // Demeter violation, but using
						ccThread.oOut.flush(); 		// fairly dependable objects
					}
				}
				else {
					//System.out.println("\tdetermined that message is private");
					for (int i = 0; i < clientList.size(); i++) {
						if ((clientList.get(i).username.equals(cm.getRecipient()))
								|| (clientList.get(i).username.equals(cm.getSender()))) {
							//System.out.println("\tsending to " + clientList.get(i).username);
							GoClientThread ccThread = clientList.get(i);
							ccThread.oOut.writeObject(cm); // Demeter violation, but using
							ccThread.oOut.flush(); 		// fairly dependable objects
						}
					}
				}
			}
			catch(IOException ioe) {
				System.err.println("Error: Caught in ClientThread.run(): " + ioe + " from ");
				ioe.printStackTrace();
			}
		}
	*/
	
		private synchronized void sendOut(String msg) {
			//System.out.println("sending out a message: '" + cm.getMessage() + "'");
			System.out.println("sendOut" + msg);
			for (int i = 0; i < clientList.size(); i++) {
				//System.out.println("\tsending to " + clientList.get(i).username);
				GoClientThread gcThread = clientList.get(i);
				gcThread.pw.write(msg); 
				gcThread.pw.flush(); 		
			}
	
		}
	
	
	////////////////////////////////////////////////////////////		new methods recently added		////////////////////////////////////////
	
	public void isWin(boolean b, String msg) {	}
	public void isLost(boolean b, String msg) {	}
	public void useProtocol(String msg) { }
	public void pairPlayers(boolean b, String msg) {}
	public Color assignColor() {
		Color selectColor;
		int randNum = ThreadLocalRandom.current().nextInt(0, 2);	//get random integer between 0 and 1 (inclusive)
		if (randNum == 0) selectColor = Color.WHITE;
		else selectColor = Color.BLACK;
		return selectColor;
	}
	}
	
}  //END GoServer class