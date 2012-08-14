package de.htwg_konstanz.in.copy;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ControlInstance implements Runnable {
	
	private Client client;
	private static final int[] SLEEP_TIMES_IN_MILLIS = { 20,   5, 157, 312,  91, 203, 289, 330,  71, 169, 
														994, 889, 760, 179, 621, 219, 682, 374, 656,   3, 
														492, 331, 411, 289, 781, 473,  10,  30, 942, 853  };
	
	
	public ControlInstance(Client client) {
		this.client = client;
	}


	public void run() {
		int count = 0;
		while (count == 0) {
			if (count == (SLEEP_TIMES_IN_MILLIS.length - 1)) {
				count = 0;
			}
			int sleepTimeInMillis = SLEEP_TIMES_IN_MILLIS[count++];
			System.out.println("SLEEP: " + sleepTimeInMillis);
			try {
				Thread.sleep(sleepTimeInMillis);
				System.out.println("SWITCH");
				Socket socket = new Socket("localhost", 8205);
				client.getSocket().switchSocket(socket);
			} catch (UnknownHostException e) {
				e.printStackTrace();			
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// end of test
				break;
			}
		}
		
	}
}
