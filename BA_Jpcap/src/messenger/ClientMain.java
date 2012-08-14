package messenger;

import java.io.IOException;

import javax.swing.JOptionPane;

 
public class ClientMain {
	private static final int port=1234;
	private static final String hostname="localhost";
	private static MySocketClient client;

	public static void main(String args[]) {
		try {
			while(true){
				createClient();			
				String clientName=JOptionPane.showInputDialog("Your message: ");
				JOptionPane.showInputDialog("--InfoMessage-- Just click OK! "+ "\n" + client.sendAndReceive(clientName));
				//System.out.println(client.sendAndReceive(clientName));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void createClient() throws IOException {
		if(client == null){
			client=new MySocketClient(hostname,port);
		}
		
	}
}
