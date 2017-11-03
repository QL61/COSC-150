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
}