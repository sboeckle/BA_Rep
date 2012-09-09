/**
 * 
 */
package de.htwg_konstanz.in.switchable.test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JOptionPane;

import de.htwg_konstanz.in.switchable.realTests.Constants;


/**
 * @author Ellen
 *
 */
public class TestMessageApplication implements Runnable {
	
	Client sender;
	Date c;
	
	public TestMessageApplication(Client sender) {
		this.sender = sender;
	}

	public static void main(String[] args) {
		Client sender = new Client();	
		TestMessageApplication testApplication = new TestMessageApplication(sender);
		Thread thread = new Thread(testApplication);
		thread.start();
		
		while (true) {
			String m = JOptionPane.showInputDialog("Your message: ");
			try {
				sender.sendMessage(m);
			} catch (Exception e) {
				System.err.println("Exception because of the JOptionPane!");
				e.printStackTrace();
				break;
			}
		}
		thread.interrupt();
		try {
			sender.getSocket().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("END");
	}

	
	public void run() {
		//with i is decided on which port a connection request
		//has to be tried. which has to be the same port as the serversocket is listening on.
		//	to test multiply serverswitches
		while (true) {
			int port = 8081;
			// "random" time to wait before switching connection
			int randomTimeInMillis = (int) (Math.random() * 5000)+5000;
			System.out.println("SLEEP: "+ randomTimeInMillis + "milliSecs" );
			try {
				Thread.sleep(randomTimeInMillis);
				c = new Date();
				System.out.println(c+ ": SWITCH");	
				
				Socket socket = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
				sender.isSwitching = true;
				sender.getSocket().switchSocket(socket);
				c = new Date();
				//Waiting some seconds to simulate the switching-process time
		//		System.out.println(c+  ": Switch ended: waiting for another 3 secs...");
		//		Thread.sleep(1000);
				sender.isSwitching = false;
				c = new Date();
				System.out.println(c+  ": done with switching...");
				//Thread.sleep(6000);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		
	}

	
}
