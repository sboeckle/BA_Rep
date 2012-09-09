package de.htwg_konstanz.in.switchable.realTests;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.htwg_konstanz.in.switchable.withComments.SwitchableInputStream;
import de.htwg_konstanz.in.switchable.withComments.SwitchableOutputStream;
import de.htwg_konstanz.in.switchable.withComments.SwitchableSocket;

public class SwitchableSocketTest {
	private static Thread serverThread;
	private static SwitchableSocket switchableSocket;
	private static SwitchableInputStream switchableInputStream;
	private static SwitchableOutputStream switchableOutputStream;
	private static Socket s;

	long chksum;
	long alreadyTransferred = 0;
	long fileSize;
	byte[] buffer = new byte[Constants.BYTE_ARRAY_SIZE];
	File fileToSend;
	FileInputStream fIn;

	// @BeforeClass
	// public static void setUpBeforeClass(){
	// testServerMessage = new TestServerMessage();
	// serverThread = new Thread(testServerMessage);
	// serverThread.start();
	// }

	@AfterClass
	public static void tearDownAfterClass() {
		serverThread.interrupt();
	}

	@Before
	public void setUp() {
		try {
			s = new Socket("localhost", Constants.SERVER_PORT);
			switchableSocket = new SwitchableSocket(s);
			switchableInputStream = switchableSocket.getInputStream();
			switchableOutputStream = switchableSocket.getOutputStream();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSwitchSocket() throws IOException {
		String path = (String) JOptionPane.showInputDialog("Path to File:");
		ControllInstanceMessage control = new ControllInstanceMessage(
				switchableSocket);
		Thread controlThread = new Thread(control);
		controlThread.start();
		boolean done = false;
		while (!done) {
			done = sendFile(path);
		}
		control.finished = true;
		controlThread.interrupt();
		byte[] answer = new byte[64];
		System.out.println("Waiting for Answer...");
		switchableInputStream.read(answer);
		String answerString = new String(answer).trim();
		int answerCode = Integer.parseInt(answerString);
		System.out.println("answerCode: " + answerCode);
		if (answerCode == -2)
			fail("files have different lengths");
		else if (answerCode == -1)
			fail("files are different according to CompareFilesByBytes method");
		else if (answerCode == -3)
			fail("files have different md5 hashs");
		else if (answerCode == -5)
			fail("some other reason");
		assertTrue(answerCode == 0);
	}

	/**
	 * 
	 * Sends a File to the Server to which the SwitchableSocket is connected
	 * 
	 * @param path
	 *            The Path to the File
	 * @return true if all bytes has been transferred
	 * @throws InterruptedException
	 */
	public boolean sendFile(String path) {
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
				switchableOutputStream.write(buffer, 0, res);
				switchableOutputStream.flush();
				alreadyTransferred += res;
			}

			if (fileSize == alreadyTransferred) {
				System.out.println("All bytes from File has been transferred: "
						+ fileSize);
				return true;
			} else
				return false;
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			return false;
		}

		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	// @Test
	// public void testSwitchSocketReference() {
	// fail("Not yet implemented");
	// }

}
