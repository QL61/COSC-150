package chatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
		
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
				pwriter.write(msg);
				System.out.println("from client " + uniqueID + ": " + msg);
			}
		}
		catch (IOException ioe) {
			System.err.println("Caught in ClientThread.run: " + ioe + " from ");
			ioe.printStackTrace();
		}
	}

}
