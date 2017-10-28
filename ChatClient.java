package chatter;

import java.net.*;
import java.io.*;

public class ChatClient {
	Socket sock;
	int publicPort = 51550;
	String username = "ALPHA";
	
	public static void main(String args[]) {

		new ChatClient();
		
	}
	
	public ChatClient() {
		System.out.println("chat client starting");
		
		try {
			sock = new Socket("localhost", publicPort);
			
			InputStream in = sock.getInputStream();
			BufferedReader bin = new BufferedReader (new InputStreamReader(in));
			
			OutputStream out = sock.getOutputStream();
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(out));
			
			bout.write(username);
			bout.flush();
		}
		catch (IOException ioe) {
			System.err.println(ioe);
		}
		catch (Exception e) {
			System.err.println("ERROR: exception '" + e + "' caught in ChatClient()");
		}
	}
}
