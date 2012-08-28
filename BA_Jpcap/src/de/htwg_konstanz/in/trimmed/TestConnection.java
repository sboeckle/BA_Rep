package de.htwg_konstanz.in.trimmed;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestConnection {
	
	public static void main(String[] args) {
		try {
			Socket s = new Socket("141.37.179.70", 8081);
			System.out.println("connected to: " + s.getInetAddress());
			OutputStream out = s.getOutputStream();
//			FileInputStream fIn = new FileInputStream(new File("C:/convertedFile.zip"));
			byte[] buffer = new byte[32];
//			System.out.println("writing...");
//			int read;
//			while ( (read = fIn.read(buffer)) != -1) {
//				out.write(buffer, 0, read);
//				buffer = new byte[512];
//			}
//			fIn.close();
			String message= "26345";
			out.write(message.getBytes(), 0, message.getBytes().length);
			out.flush();
			out.close();
			System.out.println("finished");
			System.out.println("ENDE");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
