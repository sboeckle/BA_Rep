/**
 * 
 */
package de.htwg_konstanz.in.switchable.manualTest;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JOptionPane;

import de.htwg_konstanz.in.switchable.tests.Constants;


/**
 * @author Ellen
 *
 */
public class TestFileTransferApplication implements Runnable {
	
	Client sender;
	Date c;
	
	public TestFileTransferApplication(Client sender) {
		this.sender = sender;
	}

	public static void main(String[] args) throws IOException {
		String file = 
			JOptionPane.showInputDialog("Path to File: ");
		Client sender = new Client();
		TestFileTransferApplication testApplication = new TestFileTransferApplication(sender);
		Thread thread = new Thread(testApplication);
		thread.start();
		boolean done = false;
		while (!done) {
			try {
				done = sender.parseAndSendFile(file);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		thread.interrupt();
		
		try {
			sender.getSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("TestApp END");
	}

	
	public void run() {
		//with i is decided on which port a connection request
		//has to be tried. which has to be the same port as the serversocket is listening on.
		//	to test multiply serverswitches
		while (true) {
			// "random" time to wait before switching connection
			int randomTimeInMillis = (int) (Math.random() * 5000)+1500;
			System.out.println("SLEEP: "+ randomTimeInMillis + "milliSecs" );
			try {
				Thread.sleep(randomTimeInMillis);
				c = new Date();
				System.out.println(c+ ": SWITCH");	
				
				Socket socket = new Socket(Constants.SERVER_ADDRESS,Constants.SERVER_PORT);
				sender.isSwitching = true;
				sender.getSocket().switchSocket(socket);
				c = new Date();
				sender.isSwitching = false;
				c = new Date();
				System.out.println(c+  ": done with switching...");
			} catch (UnknownHostException e) {
				e.printStackTrace();			
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			
		}
		
	}

	
}
