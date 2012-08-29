package de.htwg_konstanz.in.trimmed;

import static org.junit.Assert.fail;

import java.net.ServerSocket;
import java.net.Socket;

import org.junit.BeforeClass;
import org.junit.Test;

public class SwitchableInputStreamTest {
	static ServerSocket server;
	static Socket oldSocketA;
	static Socket newSocketB;
	static Socket s1;
	static Socket s2;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		server = new ServerSocket(8081);
		s1 = server.accept();
		s2 = server.accept();
		oldSocketA = new Socket("localhost", 8081);
		newSocketB = new Socket("localhost", 8081);
	}

	@Test
	public void testRead() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadByteArrayIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testSwitchInputStream() {
		fail("Not yet implemented");
	}

	@Test
	public void testInternStreamSwitch() {
		
	}

	@Test
	public void testInputStream() {
		fail("Not yet implemented");
	}

}
