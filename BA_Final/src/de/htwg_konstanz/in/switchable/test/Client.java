/**
 * 
 */
package de.htwg_konstanz.in.switchable.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import de.htwg_konstanz.in.switchable.SwitchableOutputStream;
import de.htwg_konstanz.in.switchable.SwitchableSocket;
import de.htwg_konstanz.in.switchable.realTests.Constants;

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
	byte[] buffer = new byte[Constants.BYTE_ARRAY_SIZE];

	public Client() {
		try {
			switchableSocket = new SwitchableSocket(new Socket(
					Constants.SERVER_ADDRESS, Constants.SERVER_PORT));
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

	public String sendMessage(String message) throws SocketException,
			IOException {
		try {
			System.out.println("sending: " + message);
			out.write(message.getBytes(), 0, message.getBytes().length);
			out.flush();

			System.out.println("Sent: " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Receive the same string back from the server
		byte[] buffer = new byte[message.getBytes().length];

		int bytesRcvd; // Bytes received in last read
		while ((bytesRcvd = in.read(buffer, 0, message.getBytes().length)) != -1) {
			if (bytesRcvd == message.getBytes().length) {
				break;
			}
		} // data array is full

		System.out.println("Received: " + new String(buffer));
		return new String(buffer);
	}

	public boolean parseAndSendFile(String path) throws InterruptedException {
		try {
			if (fileToSend == null) {
				fileToSend = new File(path);
			}
			if (fIn == null) {
				fIn = new FileInputStream(fileToSend);
			}
			// First check the filelength, but only on the first time
			if (fileSize == 0) {
				fileSize = fileToSend.length();
				System.out.println("fileSize: " + fileSize);
			}
			int res;
			// skip the already transferred Bytes
			if (alreadyTransferred != 0) {
				long skipped = fIn.skip(alreadyTransferred);
				if (alreadyTransferred != skipped) {
					System.err
							.println("bytes already transferred != bytes skipped!!!");
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

		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		if (fileSize == alreadyTransferred) {
			System.out.println("All bytes from File has been transferred: "
					+ fileSize);
			return true;
		} else
			return false;

	}

	public SwitchableSocket getSocket() {
		return switchableSocket;
	}

}
