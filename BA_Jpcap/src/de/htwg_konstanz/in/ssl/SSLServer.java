/**
 * 
 */
package de.htwg_konstanz.in.ssl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 * @author Ellen Wieland
 *
 */
public class SSLServer implements Runnable {

	private SwitchableSSLSocket switchableSocket;
	private OutputStream out;
	private InputStream in;
	
	private static final int SERVER_PORT = 8205;
	private static final int BUFFER_SIZE = 32;
		
	public SSLServer() {
		try {
			switchableSocket = new SwitchableSSLSocket();
			out = switchableSocket.getOutputStream();
			in = switchableSocket.getInputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	/**
	 * @return the switchableSocket
	 */
	public SwitchableSSLSocket getSwitchableSSLSocket() {
		return switchableSocket;
	}



	public void run() {		
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int recvMsgSize;
			while ((recvMsgSize = in.read(buffer)) != -1) {				
				
				String message = new String(buffer).trim();
				System.out.println(message.substring(0, recvMsgSize));
				out.write(buffer, 0, recvMsgSize);
				out.flush();
			}
			System.out.println("Received EOF -> end server");
			// end of test
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {		
		try {
			 SSLServerSocketFactory sslserversocketfactory =
                 (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
         SSLServerSocket sslserversocket =
                 (SSLServerSocket) sslserversocketfactory.createServerSocket(9999);

			Thread thread = new Thread(server);
			thread.start();
			
			while (true) {
				// switch socket connection when a new client connects
				SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();
				System.out.println("SWITCH");
				server.switchableSSLSocket.switchSocket(newSocket);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
