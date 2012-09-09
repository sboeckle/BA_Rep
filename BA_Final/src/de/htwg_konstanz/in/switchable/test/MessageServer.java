/**
 * 
 */
package de.htwg_konstanz.in.switchable.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JOptionPane;

import de.htwg_konstanz.in.switchable.SwitchableSocket;

/**
 * @author Ellen Wieland
 * 
 */
public class MessageServer implements Runnable {

	static private SwitchableSocket switchableSocket;

	// only needed if messages should be sent back!
	static private OutputStream out;

	static private InputStream in;
	static private FileOutputStream writer;
	static private long totalBytesRecieved;
	private static String pathOriginalFile;
	private static String pathSaveFile;

	private static final int SERVER_PORT = 8081;
	private static final int SERVER_PORT2 = 80;
	private static final int BUFFER_SIZE = 512;

	private static Date c;

	public MessageServer(Socket socket) {
		try {
			switchableSocket = new SwitchableSocket(socket);
			out = switchableSocket.getOutputStream();
			in = switchableSocket.getInputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int recvMsgSize;
			while ((recvMsgSize = in.read(buffer)) != -1) {				
				
				String message = new String(buffer).trim();
				if(message != null && message != "");{
					System.out.println("Message arrived!");
					System.out.println(message.substring(0, recvMsgSize));
					out.write(buffer, 0, recvMsgSize);
					out.flush();
				}
				
			}
			System.out.println("Received EOF -> end server");
			// end of test
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param args
	 *            args[0] Path where recieved File should be stored args[1] Path
	 *            to File to which recieved one should be compared
	 * @throws InterruptedException
	 */
	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);			
			System.out.println("TCPServer waiting for messages..");
			
			MessageServer server = new MessageServer(serverSocket.accept());
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

	}

	/**
	 * @return the switchableSocket
	 */
	public SwitchableSocket getSwitchableSocket() {
		return switchableSocket;
	}

}
