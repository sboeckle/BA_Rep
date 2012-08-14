/**
 * 
 */
package de.htwg_konstanz.in;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author Ellen Wieland
 * 
 */
public class Client {

	SwitchableSocket switchableSocket;
	OutputStream out;
	InputStream in;
	boolean isSwitching = false;
	QueueString messageQueue = new QueueString();
	Date c;

	public Client() {
		try {
			switchableSocket = new SwitchableSocket(new Socket("localhost",
					8205));
			out = switchableSocket.getOutputStream();
			in = switchableSocket.getInputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int sendMessage(String message) throws SocketException, IOException {
		if (isSwitching) {
			messageQueue.put(message);
			return -1;
		} else if (!messageQueue.empty()) {
			messageQueue.put(message);
			c = new Date();
			System.out.println( c+ ": ------ Anzahl: " + messageQueue.size()
					+ " Stored Messages available------");
			
				System.out.println( c+ ": ______ " + messageQueue + " ______ ");
			for(NodeString t = messageQueue.head; t != null; t = t.next) {
				if (isSwitching) {
				//	System.out.println(c+ " returning because of switching"); 
					return -2;
				}
				String m = messageQueue.get();
				out.write(m.getBytes(), 0, m.getBytes().length);
				out.flush();
				c = new Date();
				System.out.println(c+ " Sent Stored Message: " + m);
				sameFromServer(message);
			}
			return 1;
		} else {
			try {
				out.write(message.getBytes(), 0, message.getBytes().length);
				out.flush();
				c = new Date();
				System.out.println(c+ ": Sent: " + message);
				sameFromServer(message);
				return 0;
			} catch (IOException e) {
				e.printStackTrace();
				return -9;
			}
			//byte[] buffer = sameFromServer(message);
			// Receive the same string back from the server

		}
	}

	private byte[] sameFromServer(String message) throws IOException {
		byte[] buffer = new byte[message.getBytes().length];

		int totalBytesRcvd = 0; // Total bytes received so far
		int bytesRcvd; // Bytes received in last read
		while (totalBytesRcvd < message.getBytes().length) {
			if ((bytesRcvd = in.read(buffer, totalBytesRcvd,
					message.getBytes().length - totalBytesRcvd)) == -1){
				c = new Date();
				throw new SocketException(c+ ": Connection closed prematurely");
			}
			totalBytesRcvd += bytesRcvd;
		} // data array is full
		c = new Date();
		System.out.println(c+ ": Received: " + new String(buffer));
		return buffer;

	}

	public SwitchableSocket getSocket() {
		return switchableSocket;
	}

}
