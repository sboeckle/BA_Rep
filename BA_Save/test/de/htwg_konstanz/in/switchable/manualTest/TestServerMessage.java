/**
 * 
 */
package de.htwg_konstanz.in.switchable.manualTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import de.htwg_konstanz.in.switchable.SwitchableSocket;
import de.htwg_konstanz.in.switchable.tests.Constants;

/**
 * @author Steven Böckle
 * 
 */
public class TestServerMessage implements Runnable {

	public SwitchableSocket switchableSocket;
	public ServerSocket serverSocket;
	// only needed if messages should be sent back!
	private OutputStream out;
	private InputStream in;
	private static final int BUFFER_SIZE = 512;

	public TestServerMessage() {
		try {
			serverSocket = new ServerSocket(Constants.SERVER_PORT);
			System.out.println("TCPServer waiting for messages..");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			Socket client = serverSocket.accept();
			switchableSocket = new SwitchableSocket(client);
			in = switchableSocket.getInputStream();
			out = switchableSocket.getOutputStream();			
			byte[] buffer = new byte[BUFFER_SIZE];
			int recvMsgSize;
			while ((recvMsgSize = in.read(buffer)) != -1) {				
				//reads the message from the client and sends it back
				String message = new String(buffer).trim();
				if(message != null && message != "");{
					System.out.println("Server: Message arrived!");
					System.out.println(message.substring(0, recvMsgSize));
					out.write(buffer, 0, recvMsgSize);
					out.flush();
				}
				
			}
			System.out.println("Received EOF -> end server");
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
