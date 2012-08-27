/**
 * 
 */
package de.htwg_konstanz.in.trimmed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	SwitchableOutputStream out;
	InputStream in;
	FileInputStream fIn;
	File fileToSend;
	boolean isSwitching = false;
	boolean hasSwitched = false;
	QueueString messageQueue = new QueueString();
	Date c;
	long chksum = 0;
	long alreadyTransferred;
	long fileSize;
	byte[] buffer = new byte[512];

	public Client() {
		try {
			switchableSocket = new SwitchableSocket(new Socket("141.37.179.70",
					8081));
			out = switchableSocket.getOutputStream();
			in = switchableSocket.getInputStream();
			alreadyTransferred = 0;
			fileSize = 0;
			fIn = null;
			fileToSend = null;
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
		// Any messages stored? Send them first
		} else if (!messageQueue.empty()) {
			messageQueue.put(message);
			c = new Date();
			System.out.println(c + ": ------ Anzahl: " + messageQueue.size()
					+ " Stored Messages available------");

			System.out.println(c + ": ______ " + messageQueue + " ______ ");
			for (NodeString t = messageQueue.head; t != null; t = t.next) {
				if (isSwitching) {
					// System.out.println(c+ " returning because of switching");
					return -2;
				}
				String m = messageQueue.get();
				out.write(m.getBytes(), 0, m.getBytes().length);
				out.flush();
				c = new Date();
				System.out.println(c + " Sent Stored Message: " + m);
				sameFromServer(message);
			}
			return 1;
		} else {
			try {
				out.write(message.getBytes(), 0, message.getBytes().length);
				out.flush();
				c = new Date();
				System.out.println(c + ": Sent: " + message);
				sameFromServer(message);
				return 0;
			} catch (IOException e) {
				e.printStackTrace();
				return -9;
			}
			// byte[] buffer = sameFromServer(message);
			// Receive the same string back from the server

		}
	}

	public boolean parseAndSendFile(String path) throws InterruptedException {
		try {
			if(fileToSend == null){
				fileToSend = new File(path);
			}
			if(fIn == null){
				fIn = new FileInputStream(fileToSend);
			}			
			//First check the filelength, but only on the first time
			if(fileSize == 0){
				fileSize = fileToSend.length();
				System.out.println("fileSize: " + fileSize);
			}			
			int res;
			//skip the already transferred Bytes
			if(alreadyTransferred != 0){
				long skipped = fIn.skip(alreadyTransferred);
				if(alreadyTransferred != skipped){
					System.err.println("bytes already transferred != bytes skipped!!!");
				}
			}
				
			while ((res = fIn.read(buffer)) != -1) {
					out.write(buffer, 0, res);
					out.flush();
					alreadyTransferred += res;
			}
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
			return false;
		} 
		
		//only works if the Thread that handles the the Stream is interrupted
		
//		catch (InterruptedIOException iee) {
//			//if (hasSwitched) {
//			iee.printStackTrace();
//				alreadyTransferred = iee.bytesTransferred;
//				System.err.println("alreadyTransferred: " + alreadyTransferred);
//			//}else iee.printStackTrace();
//		} 
		catch (IOException e) {
			// TODO disconnect which wasn't planned
			// if exception is thrown alreadyTransferred has to be reduced accordingly
			// ignore byte[]s that hasn't been sent completely
//			int toMuchSent = alreadyTransferred % 512;
//			if(alreadyTransferred != 0 && (toMuchSent != 0)){
//				alreadyTransferred -= toMuchSent;
//			}
			e.printStackTrace();
			return false;
		}
		if(fileSize == alreadyTransferred){
			System.out.println("All bytes from File has been transferred: " + fileSize);
			return true;
		}else return false;

	}

	private byte[] sameFromServer(String message) throws IOException {
		byte[] buffer = new byte[message.getBytes().length];

		int totalBytesRcvd = 0; // Total bytes received so far
		int bytesRcvd; // Bytes received in last read
		while (totalBytesRcvd < message.getBytes().length) {
			if ((bytesRcvd = fIn.read(buffer, totalBytesRcvd,
					message.getBytes().length - totalBytesRcvd)) == -1) {
				c = new Date();
				throw new SocketException(c + ": Connection closed prematurely");
			}
			totalBytesRcvd += bytesRcvd;
		} // data array is full
		c = new Date();
		System.out.println(c + ": Received: " + new String(buffer));
		return buffer;

	}

	public SwitchableSocket getSocket() {
		return switchableSocket;
	}

}
