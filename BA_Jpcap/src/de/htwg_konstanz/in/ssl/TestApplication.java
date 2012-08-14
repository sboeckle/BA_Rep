/**
 * 
 */
package de.htwg_konstanz.in.ssl;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

/**
 * @author Ellen
 *
 */
public class TestApplication implements Runnable {
	
	Client sender;
	
	public TestApplication(Client sender) {
		this.sender = sender;
	}

	public static void main(String[] args) {
		Client sender = new Client();	
		TestApplication testApplication = new TestApplication(sender);
		Thread thread = new Thread(testApplication);
		thread.start();
		
		while (true) {
			String m = JOptionPane.showInputDialog("Your message: ");
			try {
				sender.sendMessage(m);
			} catch (Exception e) {
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
		
		while (true) {
			// random time to wait before switching connection
			int randomTimeInMillis = (int)(Math.random() * 5000) + 5000;
			System.out.println("SLEEP: " + randomTimeInMillis);
			try {
				Thread.sleep(randomTimeInMillis);
				System.out.println("SWITCH");
				Socket socket = new Socket("localhost", 8205);
				sender.getSocket().switchSocket(socket);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				break;
			}
		}
		
	}

	
}
