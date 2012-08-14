package de.htwg_konstanz.in;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SwitchableOutputStreamTest {

	private SwitchableOutputStream switchableOutputStream;
	
	
	@BeforeClass
	public static void setUpBeforeClass() {
		Thread serverThread = new Thread(new StartServerThread());
		serverThread.start();
	}
	
	@Before
	public void setUp() {
		try {
			Socket socket = new Socket("localhost", 8205);
			SwitchableSocket switchableSocket = new SwitchableSocket(socket);
			switchableOutputStream = switchableSocket.getOutputStream();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	@Test
	public void testSwitchOutputStream() {
		OutputStream oldOutputStream = (OutputStream) PrivateAccessor.getPrivateField(switchableOutputStream, "outputStream");
		try {
			Socket socket = new Socket("localhost", 8205);
			OutputStream newOutputStream = socket.getOutputStream();
			switchableOutputStream.switchOutputStream(newOutputStream);
		} catch (IOException e) {
			fail("The following exception occured: " + e.getMessage());
		}
		OutputStream newOutputStream = (OutputStream) PrivateAccessor.getPrivateField(switchableOutputStream, "outputStream");
		// old and new stream are not equal
		assertFalse(oldOutputStream.equals(newOutputStream));
	}
}
