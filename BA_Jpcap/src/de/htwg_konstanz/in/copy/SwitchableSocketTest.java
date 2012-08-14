package de.htwg_konstanz.in.copy;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SwitchableSocketTest {
	
	private Client client;
	private Thread controlInstanceThread;
	
	private static final String TEST_MESSAGE = "Test Message = Current Time: ";
	private static final int NUMBER_OF_MESSAGES = 50;
	
	
//	@BeforeClass
//	public static void setUpBeforeClass() {
//		Thread serverThread = new Thread(new StartServerThread());
//		serverThread.start();
//	}
	
	@Before
	public void setUp() {
		client = new Client();
		ControlInstance controlInstance = new ControlInstance(client);
		
		controlInstanceThread = new Thread(controlInstance);
		controlInstanceThread.start();
	}
	
	@Test
	public void testCorrectDataExchange() {
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < NUMBER_OF_MESSAGES; i++) {
			try {
				String message = TEST_MESSAGE + System.currentTimeMillis();
				String answer = client.sendMessage(message);
				assertEquals(message, answer);
			} catch (Exception e) {
				fail("The following Exception occured: " + e.getMessage());
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("The test took: " + (endTime - startTime) + " milliseconds.");
	}
	
	
	
	@After
	public void tearDown() {
		controlInstanceThread.interrupt();
		try {
			client.getSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("END");
	}
	

}
