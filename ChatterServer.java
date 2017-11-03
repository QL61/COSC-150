package chatter;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class ChatterServer
{
	ServerSocket sock;
	boolean keepGoing = true;
	int publicPort = 55305;
	ArrayList<ClientThread> clientList = new ArrayList<ClientThread>();
	int idNumber = 0;

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

			while (keepGoing)
			{
				// listen for connections
				Socket client = sock.accept(); // this blocks until a client calls      
				System.out.println("ChatterServer: accepts client connection ");

				ClientThread threadToAdd = new ClientThread(client, idNumber);
				clientList.add(threadToAdd);
				threadToAdd.start();

				idNumber++;
			}

			sock.close();
		}
		catch( Exception e ) { 
			System.err.println("Caught in ChatServer(): " + e +" from ");
			e.printStackTrace();
		}      
		System.exit(0);
	}
	
	
	class ClientThread extends Thread {

		Socket sock;
		int uniqueID;
		PrintWriter pwriter;
		BufferedReader bin;
		boolean keepGoing = true;

		ClientThread(Socket s, int id) {
			sock = s;
			uniqueID = id;
			try {
				InputStream in = sock.getInputStream();
				bin = new BufferedReader( new InputStreamReader(in) );
				pwriter = new PrintWriter( sock.getOutputStream(), true);
			}
			catch(IOException ioe) {
				System.err.println("Caught in ClientThread constructor: " + ioe + " from ");
				ioe.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				while (keepGoing) {
					String msg = new String(bin.readLine());
					System.out.println("from client " + uniqueID + ": " + msg);
					sendOut(msg);
				}
			}
			catch (IOException ioe) {
				System.err.println("Caught in ClientThread.run: " + ioe + " from ");
				ioe.printStackTrace();
			}
		}

	}
	
	public void sendOut(String msg) {
		for (int i = 0; i < clientList.size(); i++) {
			clientList.get(i).pwriter.write(msg + '\n'); // Law of Demeter violation,
			clientList.get(i).pwriter.flush(); // but using very dependable objects
		}
	}
}