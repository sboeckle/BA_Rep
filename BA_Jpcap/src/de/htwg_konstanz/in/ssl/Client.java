/**
 * 
 */
package de.htwg_konstanz.in.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;

/**
 * @author Ellen Wieland
 *
 */
public class Client {
	
	SwitchableSSLSocket switchableSocket;
	OutputStream out;
	InputStream in;
	
	public Client() {
		try {
			switchableSocket = new SwitchableSSLSocket(new SSLSocket("localhost", 8205));
			out = switchableSocket.getOutputStream();
			in = switchableSocket.getInputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String sendMessage(String message) throws SocketException, IOException {
		try {
			out.write(message.getBytes(), 0, message.getBytes().length);
			out.flush();
			
			System.out.println("Sent: " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Receive the same string back from the server
		byte[] buffer = new byte[message.getBytes().length];
		
	    int totalBytesRcvd = 0;  // Total bytes received so far
	    int bytesRcvd;           // Bytes received in last read
	    while (totalBytesRcvd < message.getBytes().length) {
	      if ((bytesRcvd = in.read(buffer, totalBytesRcvd, message.getBytes().length - totalBytesRcvd)) == -1)
	        throw new SocketException("Connection closed prematurely");
	      totalBytesRcvd += bytesRcvd;
	    }  // data array is full

	    System.out.println("Received: " + new String(buffer));
	    return new String(buffer);
	}
	
	public SwitchableSSLSocket getSocket() {
		return switchableSocket;
	}

}
