package de.htwg_konstanz.in.switchable.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

import de.htwg_konstanz.in.switchable.realTests.Constants;


public class TestServer implements Runnable{
	
	private ServerSocket server;
	private Socket client;
	
	public TestServer(){
		try {
			server = new ServerSocket(Constants.SERVER_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		
		while (true){
			try {
				client = server.accept();
				System.out.println("TestServer: client connected to Server");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
