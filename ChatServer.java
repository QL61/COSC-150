package chatter;

import java.net.*;
import java.io.*;

public class ChatServer {
	
	ServerSocket sock;
	int publicPort = 51550;
	boolean keepGoing = true;
	
	public static void main(String args[]) {
			
		new ChatServer();
		
	}
	
	public ChatServer() {
		System.out.println("chat server starting");
		try
		{
			sock = new ServerSocket(publicPort); // open socket
			
			// listen for connections
			while (keepGoing) {
				// when client calls, establish output stream to client
				Socket client = sock.accept(); // this blocks until a client calls
				System.out.println("new user has entered chat");
				
				InputStream in = client.getInputStream();
				BufferedReader bin = new BufferedReader(new InputStreamReader(in));
				
				PrintWriter pout = new PrintWriter(client.getOutputStream());
				
				String username = bin.readLine();
				System.out.println("username: " + username);
				
				client.close();
			}
			
			sock.close();
		}
		catch (Exception e) {
			System.err.println("ERROR: exception '" + e + "' caught in ChatServer()");
		}
	}

}
