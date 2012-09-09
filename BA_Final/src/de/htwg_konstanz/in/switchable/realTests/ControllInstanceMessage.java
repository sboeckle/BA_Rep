package de.htwg_konstanz.in.switchable.realTests;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import de.htwg_konstanz.in.switchable.withComments.SwitchableSocket;
import de.htwg_konstanz.in.switchable.test.TestServerMessage;

/**
 * @author Steven Böckle
 * 
 */
public class ControllInstanceMessage implements Runnable {
	private SwitchableSocket client;
	public boolean finished = false;

	public ControllInstanceMessage(SwitchableSocket client) {
		this.client = client;
	}

	@Override
	public void run() {
		while (true) {
			try {
				int randomTime = (int) (Math.random() * 5000) + 1500;
				System.out.println(randomTime + " milisecs till next switch");
				Thread.sleep(randomTime);
				if (!finished) {
					Socket newClient;
					newClient = new Socket("localhost", Constants.SERVER_PORT);
					client.switchSocket(newClient);
				}

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("going out now...");
				try {
					//Tell the Server, that the file was transferred completely,
					//for that shut down the Output Only, because the 
					//Input is listening for an answer from the server
					client.shutdownOutput();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			}

		}

	}

}
