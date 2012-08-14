package de.htwg_konstanz.in.ssl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author Ellen Wieland
 * 
 */
public class SwitchableSSLSocket extends SSLSocketFactory  {

	private SSLSocket socket;

	private SwitchableSSLInputStream switchableInputStream;
	private SwitchableSSLOutputStream switchableOutputStream;
	
	private boolean isSwitchException = false;

	public SwitchableSSLSocket(SSLSocket socket) throws IOException {
		this.socket = socket;
		switchableInputStream = new SwitchableSSLInputStream(
				socket.getInputStream());
		switchableOutputStream = new SwitchableSSLOutputStream(
				socket.getOutputStream());
	}

	public void switchSocket(SSLSocket newSocket){
		
		OutputStream oldOutputStream = switchableOutputStream.getOutputStream();
		try{
			synchronized (switchableOutputStream) {
				// switch output stream to the new ones from the second socket
				int numberOfBytesSent = switchableOutputStream.switchOutputStream(newSocket.getOutputStream());
				// send how many bytes were sent over old connection to test, if the
				// other side is ready to use new connection
				newSocket.getOutputStream().write(numberOfBytesSent);
		
				// try to receive how many bytes must be read from old connection via
				// new connection
				int numberOfBytesToRead = newSocket.getInputStream().read();
				switchableInputStream.setNumberOfBytesToRead(numberOfBytesToRead);
			}
							
			// signal the input stream to switch when it finished reading all data from the old connection
			// has to be finished before one side calls shutdownOutput()
			switchableInputStream.switchInputStream(newSocket.getInputStream());		
	
			// try to transfer settings from old socket to the new one
			// ignore exceptions to leave the user unaware of internal switching of connection
			try {
				newSocket.setKeepAlive(socket.getKeepAlive());
			} catch (Exception e) {
				// DO NOTHING
			}
			try {
				newSocket.setOOBInline(socket.getOOBInline());
			} catch (Exception e) {
				// DO NOTHING
			}
			try {
				newSocket.setSendBufferSize(socket.getSendBufferSize());
			} catch (Exception e) {
				// DO NOTHING
			}
			try {
				if (socket.getSoLinger() != -1) {
					newSocket.setSoLinger(true, socket.getSoLinger());
				}
			} catch (Exception e) {
				// DO NOTHING
			}
			try {
				newSocket.setSoTimeout(socket.getSoTimeout());
			} catch (Exception e) {
				// DO NOTHING
			}
			try {
				newSocket.setTcpNoDelay(socket.getTcpNoDelay());
			} catch (Exception e) {
				// DO NOTHING
			}
			try {
				newSocket.setTrafficClass(socket.getTrafficClass());
			} catch (Exception e) {
				// DO NOTHING
			}
	
			// switch socket reference
	//		Socket oldSocket = socket;
			isSwitchException = true;
			switchableInputStream.setSwitchException(isSwitchException);
			socket.close();
			socket = newSocket;
	//		oldSocket.close();
			
	//		oldOutputStream.write(1);
		}catch(IOException e){
			if (isSwitchException){
				System.out.println("Exception because of switching!!!");
				socket = newSocket;
				isSwitchException = false;
				switchableInputStream.setSwitchException(isSwitchException);
				//Do Nothing
			}else{
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getInputStream()
	 */
	public SwitchableSSLInputStream getInputStream() throws IOException {
		return switchableInputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getOutputStream()
	 */
	public SwitchableSSLOutputStream getOutputStream() throws IOException {
		return switchableOutputStream;
	}

	@Override
	public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getDefaultCipherSuites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getSupportedCipherSuites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket createSocket(String arg0, int arg1) throws IOException,
			UnknownHostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3)
			throws IOException, UnknownHostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2,
			int arg3) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
