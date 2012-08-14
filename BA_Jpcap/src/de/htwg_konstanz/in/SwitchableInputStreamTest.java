package de.htwg_konstanz.in;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SwitchableInputStreamTest {
	
	private SwitchableInputStream switchableInputStream;

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
			switchableInputStream = switchableSocket.getInputStream();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSwitchInputStream() throws IOException {
		// get reference of queue
		BlockingQueue<InputStream> newInputStreams = (BlockingQueue<InputStream>) PrivateAccessor.getPrivateField(switchableInputStream, "newInputStreams");
		// before switch queue has to be empty
		assertTrue(newInputStreams.isEmpty());		
		// switch the underlying input stream
		Socket socket = new Socket("localhost", 8205);
		InputStream newInputStream = socket.getInputStream();		
		switchableInputStream.switchInputStream(newInputStream);
		// after switch queue has to have elements
		assertFalse(newInputStreams.isEmpty());
		System.out.println("END of switch test");
	}
	
	// ?? nur eigene Methoden testen?
	@Test(expected=IOException.class)
	public void testClose() throws IOException {
		try {
			switchableInputStream.close();
		} catch (IOException e) {
			fail("The following exception occured: " + e.getMessage());
		}
		// read on closed socket must throw an IOException
		switchableInputStream.read();
	}	
}
