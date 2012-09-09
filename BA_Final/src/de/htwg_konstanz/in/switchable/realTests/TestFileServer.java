/**
 * 
 */
package de.htwg_konstanz.in.switchable.realTests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JOptionPane;

import de.htwg_konstanz.in.switchable.withComments.SwitchableSocket;

/**
 * @author Steven Böckle
 * 
 */
public class TestFileServer implements Runnable {

	static private SwitchableSocket switchableSocket;

	// only needed if messages should be sent back!
	static private OutputStream out;

	static private InputStream in;
	static private FileOutputStream writer;
	static private long totalBytesRecieved;
	private static String pathOriginalFile;
	private static String pathSaveFile;

	private static Date c;

	public TestFileServer(Socket socket) {
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
			while ((recvMsgSize = in.read(buffer)) != -1) {
				writer.write(buffer, 0, recvMsgSize);
				writer.flush();
				totalBytesRecieved += recvMsgSize;
				buffer = new byte[Constants.BYTE_ARRAY_SIZE];
			}
			System.out.println("total Bytes Recieved: " + totalBytesRecieved);
			c = new Date();
			System.out.println(c
					+ ": Received EOF -> compare Files -> end server");

			System.out
					.println("Comparing the converted File with the recieved one, which takes a few seconds. pls wait...");
			int result = testIfFilesAreEqual(pathSaveFile, pathOriginalFile);
			String code = Integer.toString(result);
			System.out
					.println("sending the Result of the comparison between the received "
							+ "and the original File to  the Client: " + code);
			switchableSocket.getOutputStream().write(code.getBytes(), 0, code.getBytes().length);
			switchableSocket.getOutputStream().flush();
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
			TestFileServer server = new TestFileServer(serverSocket.accept());
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
	private int testIfFilesAreEqual(String path1, String path2) {
		//
		try {
			int cBB = FileChecker.CompareFilesbyByte(path1, path2);
			if (cBB == -2) {
				System.err.println("The two files have different lengths!");
				return cBB;
			} else if (cBB == -1) {
				System.err.println("Difference using CompareFilesByBytes!");
				return cBB;
			} else
				System.out
						.println("The Files are the same accordding to CompareFilesByBytes :) ");

			if (FileChecker.MD5HashFile(path1).equals(
					FileChecker.MD5HashFile(path2))) {
				System.out.println("No Differnce using MD5HashFile Method :) ");
				return 0;
			} else {
				System.err.println("Difference using MD5HashFile!");
				return -3;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
			return -5;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -5;
		}

	}

	/**
	 * @return the switchableSocket
	 */
	public SwitchableSocket getSwitchableSocket() {
		return switchableSocket;
	}

}
