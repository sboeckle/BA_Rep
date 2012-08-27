/**
 * 
 */
package de.htwg_konstanz.in.trimmed;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JOptionPane;


/**
 * @author Ellen
 *
 */
public class TestApplication implements Runnable {
	
	Client sender;
	Date c;
	
	public TestApplication(Client sender) {
		this.sender = sender;
	}

	public static void main(String[] args) throws IOException {
		String file = 
			JOptionPane.showInputDialog("Path to File: ");
//		String p = FileChecker.convertIt("D:/VC_RED.cab", convertedFile);
//		System.out.println("File converted path: " + p);
		Client sender = new Client();
		TestApplication testApplication = new TestApplication(sender);
		Thread thread = new Thread(testApplication);
		thread.start();
		boolean done = false;
		while (!done) {
//			String m = 
//				JOptionPane.showInputDialog("File Located: ");
//			if(m == null || m.equals("") ){
//			}
			try {
//				int res = 
				done = sender.parseAndSendFile(file);
//				switch (res){
//				case 0:
//					System.out.println("Normal Message sent");
//					break;
//				case -1:
//					System.out.println("Message stored because of switching");
//					break;
//				case -2:
//					System.out.println("Not all Stored Message where sent because of switching");
//					break;
//				case 1:
//					System.out.println("All stored Message where sent");
//					break;
//				default:
//					System.out.println("Shouldn't happen!");
//					break;
//				}
			} catch (Exception e) {
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
		System.out.println("TestApp END");
	}

	
	public void run() {
		//with i is decided on which port a connection request
		//has to be tried. which has to be the same port as the serversocket is listening on.
		//	to test multiply serverswitches
		while (true) {
			int time = 6000;
			int port = 8081;
			// "random" time to wait before switching connection
			int randomTimeInMillis = (int) (Math.random() * 5000)+5000;
			System.out.println("SLEEP: "+ randomTimeInMillis + "milliSecs" );
			try {
				Thread.sleep(randomTimeInMillis);
				c = new Date();
				System.out.println(c+ ": SWITCH");	
				
				Socket socket = new Socket("141.37.179.70", port);
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
