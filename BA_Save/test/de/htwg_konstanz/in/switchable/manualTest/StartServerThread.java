package de.htwg_konstanz.in.switchable.manualTest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class StartServerThread implements Runnable {
	
	private static final int SERVER_PORT = 8205;

	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);			
			System.out.println("TCPServer waiting for messages..");
			
			TestServer123 server = new TestServer123(serverSocket.accept());
			Thread thread = new Thread(server);
			thread.start();
			
			while (true) {
				// switch socket connection when a new client connects
				Socket newSocket = serverSocket.accept();
//				System.out.println("SWITCH");
				server.getSwitchableSocket().switchSocket(newSocket);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
