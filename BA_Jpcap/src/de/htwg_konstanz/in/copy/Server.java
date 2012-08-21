/**
 * 
 */
package de.htwg_konstanz.in.copy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;


/**
 * @author Ellen Wieland
 *
 */
public class Server implements Runnable {

	static private SwitchableSocket switchableSocket;
	static private OutputStream out;
	static private InputStream in;
	static private FileOutputStream writer;
	static private long totalBytesRecieved;
	
	private static final int SERVER_PORT = 8205;
	private static final int SERVER_PORT2 = 8222;
	private static final int BUFFER_SIZE = 512;
	
	private static Date c;
		
	public Server(Socket socket) {
		try {
			switchableSocket = new SwitchableSocket(socket);
			out = switchableSocket.getOutputStream();
			in = switchableSocket.getInputStream();
			writer = new FileOutputStream("C:/recievedFile.exe");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	/**
	 * @return the switchableSocket
	 */
	public SwitchableSocket getSwitchableSocket() {
		return switchableSocket;
	}



	public void run() {		
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int recvMsgSize;
			while ((recvMsgSize = in.read(buffer)) != -1) {			
//				c = new Date();
//				System.out.println(c + ": recieved smth!");
				writer.write(buffer);
				totalBytesRecieved += recvMsgSize;
//				System.out.println("Number of Bytes recieved: " + recvMsgSize);
			}
			System.out.println("total Bytes Recieved: "+ totalBytesRecieved);
			c = new Date();
			System.out.println(c+ ": Received EOF -> end server");
			System.out.println("Comparing the converted File with the recieved one, which takes a few seconds. pls wait...");
			testIfFilesAreEqual();
			
			// end of test
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * Tests if original File and recieved File are the same
	 */
	private void testIfFilesAreEqual() {
		// 
		try {
			int cBB = FileChecker.CompareFilesbyByte("C:/convertedFile.exe", "C:/recievedFile.exe");
			if(cBB == -2){
				System.err.println("The two files have different lengths!");
			}
			else if(cBB == - 1){
					System.err.println("Difference using CompareFilesByBytes!");
			} else System.out.println("The Files are the same accordding to CompareFilesByBytes :) ");
			
			if(FileChecker.MD5HashFile("C:/convertedFile.exe").equals(FileChecker.MD5HashFile("C:/recievedFile.exe"))){
					System.out.println("No Differnce using MD5HashFile Method :) ");
				}else System.err.println("Difference using MD5HashFile!");
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	public static void main(String[] args) {		
		try {
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);		
			ServerSocket serverSocket2 = new ServerSocket(SERVER_PORT2);		
			System.out.println("TCPServer waiting for messages..");
			Socket newSocket;
			
			Server server = new Server(serverSocket.accept());
			Thread thread = new Thread(server);
			thread.start();
			//with i is decided which serversocket should listen for a connection request
			//has to have the same port as the connection request.
			//	to test multiply serverswitches
			int i = 1;
			while (true) {
				// switch socket connection when a new client connects
				if(i % 2 == 0){
					newSocket = serverSocket.accept();
					c = new Date();
					System.out.println(c + ": client connected to port 8250");	
				}else {
					newSocket = serverSocket2.accept(); 
					c = new Date();
					System.out.println(c + ": client connected to port 8222");				
				}
				c = new Date();
				System.out.println(c+ ": SWITCH");
				server.switchableSocket.switchSocket(newSocket);
				i++;
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
