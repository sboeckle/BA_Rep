package de.htwg_konstanz.in.copy;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

/**
 * @author Steven Böckle
 * 
 */
public class SwitchableSocket extends Socket {

	private Socket socket;

	private SwitchableInputStream switchableInputStream;
	private SwitchableOutputStream switchableOutputStream;
	public String clientName;
	
	private boolean isSwitchException = false;

	public SwitchableSocket(Socket socket) throws IOException {
		this.socket = socket;
		switchableInputStream = new SwitchableInputStream(
				socket.getInputStream());
		switchableOutputStream = new SwitchableOutputStream(
				socket.getOutputStream());
	}
	public SwitchableSocket(Socket socket, String clientName)throws IOException{
		this.socket = socket;
		switchableInputStream = new SwitchableInputStream(
				socket.getInputStream());
		switchableOutputStream = new SwitchableOutputStream(
				socket.getOutputStream());
		this.clientName = clientName;
	}

	public void switchSocket(Socket newSocket){
		
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
			isSwitchException = true;
			switchableInputStream.setSwitchException(isSwitchException);
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
			Socket oldSocket = socket;
			
			socket = newSocket;
			oldSocket.close();
	//		oldSocket.close();
			
	//		oldOutputStream.write(1);
		}catch(IOException e){
			if (isSwitchException){
				System.out.println("Exception because of switching!!!");
				socket = newSocket;
				isSwitchException = false;
				switchableInputStream.setSwitchException(isSwitchException);
				switchableOutputStream.setSwitchException(isSwitchException);
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
	public SwitchableInputStream getInputStream() throws IOException {
		return switchableInputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getOutputStream()
	 */
	public SwitchableOutputStream getOutputStream() throws IOException {
		return switchableOutputStream;
	}

	// override methods by delegating calls to internal socket reference

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#bind(java.net.SocketAddress)
	 */
	@Override
	public void bind(SocketAddress bindpoint) throws IOException {
		socket.bind(bindpoint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#close()
	 */
	@Override
	public synchronized void close() throws IOException {
		socket.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#connect(java.net.SocketAddress, int)
	 */
	@Override
	public void connect(SocketAddress endpoint, int timeout) throws IOException {
		socket.connect(endpoint, timeout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#connect(java.net.SocketAddress)
	 */
	@Override
	public void connect(SocketAddress endpoint) throws IOException {
		socket.connect(endpoint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getChannel()
	 */
	@Override
	public SocketChannel getChannel() {
		return socket.getChannel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getInetAddress()
	 */
	@Override
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getKeepAlive()
	 */
	@Override
	public boolean getKeepAlive() throws SocketException {
		return socket.getKeepAlive();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getLocalAddress()
	 */
	@Override
	public InetAddress getLocalAddress() {
		return socket.getLocalAddress();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getLocalPort()
	 */
	@Override
	public int getLocalPort() {
		return socket.getLocalPort();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getLocalSocketAddress()
	 */
	@Override
	public SocketAddress getLocalSocketAddress() {
		return socket.getLocalSocketAddress();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getOOBInline()
	 */
	@Override
	public boolean getOOBInline() throws SocketException {
		return socket.getOOBInline();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getPort()
	 */
	@Override
	public int getPort() {
		return socket.getPort();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getReceiveBufferSize()
	 */
	@Override
	public synchronized int getReceiveBufferSize() throws SocketException {
		return socket.getReceiveBufferSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getRemoteSocketAddress()
	 */
	@Override
	public SocketAddress getRemoteSocketAddress() {
		return socket.getRemoteSocketAddress();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getReuseAddress()
	 */
	@Override
	public boolean getReuseAddress() throws SocketException {
		return socket.getReuseAddress();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getSendBufferSize()
	 */
	@Override
	public synchronized int getSendBufferSize() throws SocketException {
		return socket.getSendBufferSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getSoLinger()
	 */
	@Override
	public int getSoLinger() throws SocketException {
		return getSoLinger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getSoTimeout()
	 */
	@Override
	public synchronized int getSoTimeout() throws SocketException {
		return socket.getSoTimeout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getTcpNoDelay()
	 */
	@Override
	public boolean getTcpNoDelay() throws SocketException {
		return socket.getTcpNoDelay();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#getTrafficClass()
	 */
	@Override
	public int getTrafficClass() throws SocketException {
		return socket.getTrafficClass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#isBound()
	 */
	@Override
	public boolean isBound() {
		return socket.isBound();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return socket.isClosed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return socket.isConnected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#isInputShutdown()
	 */
	@Override
	public boolean isInputShutdown() {
		return socket.isInputShutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#isOutputShutdown()
	 */
	@Override
	public boolean isOutputShutdown() {
		return socket.isOutputShutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#sendUrgentData(int)
	 */
	@Override
	public void sendUrgentData(int data) throws IOException {
		socket.sendUrgentData(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#setKeepAlive(boolean)
	 */
	@Override
	public void setKeepAlive(boolean on) throws SocketException {
		socket.setKeepAlive(on);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#setOOBInline(boolean)
	 */
	@Override
	public void setOOBInline(boolean on) throws SocketException {
		socket.setOOBInline(on);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#setPerformancePreferences(int, int, int)
	 */
	@Override
	public void setPerformancePreferences(int connectionTime, int latency,
			int bandwidth) {
		socket.setPerformancePreferences(connectionTime, latency, bandwidth);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#setReceiveBufferSize(int)
	 */
	@Override
	public synchronized void setReceiveBufferSize(int size)
			throws SocketException {
		socket.setReceiveBufferSize(size);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#setReuseAddress(boolean)
	 */
	@Override
	public void setReuseAddress(boolean on) throws SocketException {
		socket.setReuseAddress(on);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#setSendBufferSize(int)
	 */
	@Override
	public synchronized void setSendBufferSize(int size) throws SocketException {
		socket.setSendBufferSize(size);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#setSoLinger(boolean, int)
	 */
	@Override
	public void setSoLinger(boolean on, int linger) throws SocketException {
		socket.setSoLinger(on, linger);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#setSoTimeout(int)
	 */
	@Override
	public synchronized void setSoTimeout(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#setTcpNoDelay(boolean)
	 */
	@Override
	public void setTcpNoDelay(boolean on) throws SocketException {
		socket.setTcpNoDelay(on);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#setTrafficClass(int)
	 */
	@Override
	public void setTrafficClass(int tc) throws SocketException {
		socket.setTrafficClass(tc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#shutdownInput()
	 */
	@Override
	public void shutdownInput() throws IOException {
		socket.shutdownInput();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#shutdownOutput()
	 */
	@Override
	public void shutdownOutput() throws IOException {
		socket.shutdownOutput();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.Socket#toString()
	 */
	@Override
	public String toString() {
		return socket.toString();
	}

}
