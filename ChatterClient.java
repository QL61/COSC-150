package chatter;

import java.net.*;
import java.util.Scanner;
import java.io.*;


public class ChatterClient {

	int publicPort = 55305;
	String ip = "141.161.88.4";
	String username = "ALPHA";
	Socket sock;
	BufferedReader bin;
	BufferedWriter bout;
	Listen listenToServer;
	boolean keepGoing = true;

	public static void main(String[] args) {
		try {
			boolean keepGoing = true;
	
			ChatterClient client = new ChatterClient();
			
			Scanner scan = new Scanner(System.in);
			while(keepGoing) {
				String msg = scan.nextLine();
				if (msg != null) {
					System.out.println("about to write: " + msg);
					client.bout.write(msg + '\n');
					client.bout.flush();
				}
			}
			
			client.sock.close();
			scan.close();
		}
		catch(IOException ioe) {
			System.err.println("Caught in main: " + ioe + " from ");
			ioe.printStackTrace();
		}
	}

	public ChatterClient() {
		System.out.println("chat client starting ...");
		keepGoing = true; 
		
		try
		{
			System.out.println("about to try to call 'localhost' / " + publicPort);

			sock = new Socket("localhost", publicPort);

			InputStream in = sock.getInputStream();
			bin = new BufferedReader( new InputStreamReader(in) );

			OutputStream out = sock.getOutputStream();
			bout = new BufferedWriter( new OutputStreamWriter( out ) );
			bout.write(username + "\n");
			bout.flush();
			
			Thread t = new Thread(new Listen());
			t.start();
		}
		catch ( IOException ioe ) { 
			System.err.println("caught in ChatterClient(): " + ioe + " from ");
			ioe.printStackTrace();
		}
	}
	
	class Listen implements Runnable {
		@Override
		public void run() {
			try {
				while (keepGoing) {
					String msg = bin.readLine();
					if (msg != null) { System.out.println("from server: " + msg); }	
				}
			}
			catch (IOException ioe) {
				System.err.println("Caught in Listen.run(): " + ioe + " from ");
				ioe.printStackTrace();
			}
		}
	}
}