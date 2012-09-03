package de.htwg_konstanz.in.trimmed;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestServer implements Runnable {
	
	static private SwitchableSocket switchableSocket;

	// only needed if messages should be sent back!
	static private OutputStream out;

	static private InputStream in;
	static private FileOutputStream writer;
	static private long totalBytesRecieved;
	private static String pathOriginalFile;
	private static String pathSaveFile;
	
	public TestServer(Socket s) {
		try {
			switchableSocket = new SwitchableSocket(s);
			out = switchableSocket.getOutputStream();
			in = switchableSocket.getInputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			try {
				ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT);
				System.out.println("Server Listening on: " + serverSocket.getLocalPort());
				System.out.println("TCPServer waiting for messages..");

				TestServer server= new TestServer(serverSocket.accept());
				Thread thread = new Thread(server);
				thread.start();

				while (true) {
					// switch socket connection when a new client connects
					Socket newSocket = serverSocket.accept();
					System.out.println("SWITCH");
					server.switchableSocket.switchSocket(newSocket);
					System.out.println("switching complete");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			

			in.close();
			out.close();
			System.out.println("Server END");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		long received = 0;
		int recvMsgSize;
		long totalRecvMsgSize = 0;
		try {
			while ((recvMsgSize = in.read()) != -1) {
				totalRecvMsgSize += recvMsgSize;
				received++;
			}
		System.out.println("received : " + received);
		System.out.println("totalRecvMsgSize : " + totalRecvMsgSize);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
