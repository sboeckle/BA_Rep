package messenger;

import java.io.IOException;
 
public class ServerMain {
	private static final int port=1234;
	private static MySocketServer server;
	
	public static void main(String args[]) {
		try {
			//NWInterface nw = new NWInterface();
			server=new MySocketServer(port);
			server.listen();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
