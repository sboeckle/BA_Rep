package de.htwg_konstanz.in.switchable.manualTest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import de.htwg_konstanz.in.switchable.SwitchableSocket;
import de.htwg_konstanz.in.switchable.tests.Constants;

public class TestConnection implements Runnable{
	
	SwitchableSocket ss;
	static OutputStream out;
	
	public TestConnection() throws UnknownHostException, IOException {
		Socket s = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
		this.ss = new SwitchableSocket(s);
		System.out.println("connected to: " + s.getInetAddress());
		out = s.getOutputStream();
	}
	
	public static void main(String[] args) {
		try {
			TestConnection test = new TestConnection();
			Thread thread = new Thread(test);
			thread.start();
//			FileInputStream fIn = new FileInputStream(new File("C:/convertedFile.zip"));
			int[] data = new int[10000000];
			for(int i = 0 ; i < data.length ; i++){
				data[i] = i*2;
			}
			
			System.out.println("writing...");
			int x = 0;
			int numberOfBytesSent = 0;
			while (x <= data.length) {
				out.write(data[x]);
				out.flush();
				numberOfBytesSent++;
				x++;
			}
			
			System.out.println("numberOfBytesSent: "+ numberOfBytesSent);
			
			out.close();
			thread.interrupt();
			System.out.println("finished");
			System.out.println("ENDE");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			// "random" time to wait before switching connection
			int randomTimeInMillis = (int) (Math.random() * 5000)+500;
			System.out.println("SLEEP: "+ randomTimeInMillis + "milliSecs" );
			try {
				Thread.sleep(randomTimeInMillis);
				System.out.println("SWITCH");	
				
				Socket socket = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
				ss.switchSocket(socket);
				//Waiting some seconds to simulate the switching-process time
		//		System.out.println(c+  ": Switch ended: waiting for another 3 secs...");
		//		Thread.sleep(1000);
				System.out.println("done with switching...");
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
