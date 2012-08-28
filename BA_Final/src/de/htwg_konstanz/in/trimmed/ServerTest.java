package de.htwg_konstanz.in.trimmed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(8081);
			System.out.println("Server listening on Port: "
					+ server.getLocalPort());
			Socket s = server.accept();
			System.out.println("Client connected \n local Port:"
					+ s.getLocalPort() + "remotePort : " + s.getPort());
			InputStream in = s.getInputStream();
			// FileOutputStream out = new FileOutputStream(new
			// File("/home/steven/files/testRecievedFile.zip"));
			String read;
			// while((read = in.read(buffer)) != -1){
			// out.write(buffer, 0, read);
			// buffer = new byte[512];
			// }
			byte[] buffer = new byte[32];
			int recvMsgSize;
			String message = null;
			while ((recvMsgSize = in.read(buffer)) != -1) {

				message = new String(buffer).trim();
			}
			long bytes = Long.parseLong(message);

			System.out.println("bytes: " + bytes);
			in.close();
			// out.close();
			s.close();
			System.out.println("Server END");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
