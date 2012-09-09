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
import de.htwg_konstanz.in.switchable.realTests.Constants;
import de.htwg_konstanz.in.switchable.realTests.FileChecker;

/**
 * @author Ellen Wieland
 * 
 */
public class FileServer implements Runnable {

	static private SwitchableSocket switchableSocket;

	// only needed if messages should be sent back!
	static private OutputStream out;

	static private InputStream in;
	static private FileOutputStream writer;
	static private long totalBytesRecieved;
	private static String pathOriginalFile;
	private static String pathSaveFile;

	private static Date c;

	public FileServer(Socket socket) {
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
			byte[] buffer = new byte[Constants.BYTE_ARRAY_SIZE];
			int recvMsgSize;
			synchronized (in) {

				while ((recvMsgSize = in.read(buffer)) != -1) {
					// c = new Date();
					// System.out.println(c + ": recieved smth!");
					writer.write(buffer,0,recvMsgSize);
					writer.flush();
					totalBytesRecieved += recvMsgSize;
					buffer = new byte[Constants.BYTE_ARRAY_SIZE];
					// System.out.println("Number of Bytes recieved: " +
					// recvMsgSize);
				}
			}
			writer.flush();
			System.out.println("total Bytes Recieved: " + totalBytesRecieved);
			c = new Date();
			System.out.println(c
					+ ": Received EOF -> compare Files -> end server");

			System.out
					.println("Comparing the converted File with the recieved one, which takes a few seconds. pls wait...");
			testIfFilesAreEqual(pathSaveFile, pathOriginalFile);

			System.out.println("Server END");

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
			if (args.length < 2) {
				System.err
						.println("need 2 Arguments: first -> Path where recieved File should be stored \n "
								+ "second ->Path to File to which recieved one should be compared");
				System.exit(-1);
			}
			ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT);
			// ServerSocket serverSocket2 = new ServerSocket(10199);
			System.out.println("TCPServer waiting for messages..");
			Socket newSocket;
			System.out.println(args[0]);
			pathSaveFile = args[0];
			pathOriginalFile = args[1];
			writer = new FileOutputStream(pathSaveFile);
			FileServer server = new FileServer(serverSocket.accept());
			Thread thread = new Thread(server);
			thread.start();
			while (true) {
				newSocket = serverSocket.accept();
				c = new Date();
				System.out.println(c + ": SWITCH");
				// Server is switching now his Socket with the one from the New
				// Connection
				// thread.sleep(1000);
				switchableSocket.switchSocket(newSocket);
				System.out.println("trying to receive from new connection...");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Tests if original File and recieved File are the same
	 */
	private void testIfFilesAreEqual(String path1, String path2) {
		//
		try {
			int cBB = FileChecker.CompareFilesbyByte(path1, path2);
			if (cBB == -2) {
				System.err.println("The two files have different lengths!");
			} else if (cBB == -1) {
				System.err.println("Difference using CompareFilesByBytes!");
			} else
				System.out
						.println("The Files are the same accordding to CompareFilesByBytes :) ");

			if (FileChecker.MD5HashFile(path1).equals(
					FileChecker.MD5HashFile(path2))) {
				System.out.println("No Differnce using MD5HashFile Method :) ");
			} else
				System.err.println("Difference using MD5HashFile!");

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
