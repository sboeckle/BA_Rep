package messenger;

import java.io.*;
import java.net.*;
 
public class MySocketClient {
	private Socket socket;
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	private QueueString que = new QueueString();
	public boolean switchInitialized = false;
	
	MySocketClient(String hostname, int port) 
					throws IOException {
		socket=new Socket();
		System.out.print("[Client]: connecting '"+hostname+
			"' on "+port+" ... ");
		socket.connect(new InetSocketAddress(hostname,port));
		System.out.println("done.");
		objectInputStream=
			new ObjectInputStream(socket.getInputStream());
		objectOutputStream=
			new ObjectOutputStream(socket.getOutputStream());
	}

	public String sendAndReceive(String message) 
					throws Exception {
		if(message.startsWith("switch")){
			switchInitialized = true;
		}
		if(switchInitialized){
			String tmp = message;
			que.put(tmp);
			return null;
		}
		//}
		else if (!que.empty()){
			String storedMsg = "";
			for(NodeString t = que.head; t!= null; t = t.next){
				String tmp = que.get();
				storedMsg = storedMsg + " // " + tmp;
			}
				storedMsg = storedMsg + "\n" + " new Message: " + message;
				objectOutputStream.writeObject(storedMsg);
				System.out.println("[Client] send STOREDMSG : "+storedMsg + "\n" +
						"New Message: " + message);
				return "Client: received STOREDMSG '"
				+(String)objectInputStream.readObject()+"'";
		}
			
		else {
			objectOutputStream.writeObject(message);
			System.out.println("[Client]: send "+message);
			return "Client: received '"
				+(String)objectInputStream.readObject()+"'";
		}
			
			
	}
	
	public void disconnect() 
					throws IOException {
		try {
			socket.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
