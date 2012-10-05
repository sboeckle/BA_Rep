package de.htwg_konstanz.in.switchable.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.htwg_konstanz.in.switchable.SwitchableInputStream;
import de.htwg_konstanz.in.switchable.SwitchableOutputStream;
import de.htwg_konstanz.in.switchable.SwitchableSocket;

/**
 * Does the same as superclass {@link de.htwg_konstanz.in.switchable.tests.SwitchableSocketTest}
 * but with the difference that the server which is used is successive listening on two ServerSockets which different Ports
 * so a different ControllInstance {@link de.htwg_konstanz.in.switchable.tests.ControlInstanceServerSwitching} is needed
 *
 *
 * @author Steven Böckle
 *
 */
public class SwitchableSocketTestWithServerSwitch extends SwitchableSocketTest{
	
	public static ControlInstanceServerSwitching control;
	@Override
	public void startControllInstance(){
		control = new ControlInstanceServerSwitching(
				switchableSocket);
		controlThread = new Thread(control);
		controlThread.start();
	}
	@Override
	public void shutDownControllInstance() {
		control.finished = true;
		controlThread.interrupt();
	}
	
}
