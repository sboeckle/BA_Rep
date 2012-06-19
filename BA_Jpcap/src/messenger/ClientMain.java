package messenger;

import java.io.IOException;

 
public class ClientMain {
	private static final int port=1234;
	private static final String hostname="localhost";
	private static MySocketClient client;

	public static void main(String args[]) {
		try {
			while(true){
				createClient();			
				String clientName=System.console().readLine();
				System.out.println(client.sendAndReceive(clientName));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void createClient() throws IOException {
		if(client == null){
			client=new MySocketClient(hostname,port);
			System.out.print("Client: Enter name> ");
		}
		
	}
}
