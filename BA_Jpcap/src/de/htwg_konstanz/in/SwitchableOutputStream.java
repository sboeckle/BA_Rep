package de.htwg_konstanz.in;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Ellen Wieland
 * 
 */
public class SwitchableOutputStream extends OutputStream {

	private OutputStream outputStream;
	private volatile int numberOfBytesSent;
	
	private boolean isSwitchException = false;

	public SwitchableOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
		this.numberOfBytesSent = 0;
	}

	public synchronized int switchOutputStream(OutputStream outputStream) throws IOException {
		this.outputStream.flush();
		this.outputStream = outputStream;
		int number = numberOfBytesSent;
		numberOfBytesSent = 0;
		return number;
	}

	public synchronized OutputStream getOutputStream() {
		return outputStream;
	}

	// delegate work to internal output stream

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public synchronized void close() throws IOException {
		outputStream.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public synchronized void flush() throws IOException {
		outputStream.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException {
		numberOfBytesSent = numberOfBytesSent + len;
		outputStream.write(b, off, len);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public synchronized void write(byte[] b) throws IOException {
		numberOfBytesSent = numberOfBytesSent + b.length;
		outputStream.write(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public synchronized void write(int b) throws IOException {
		numberOfBytesSent++;
		outputStream.write(b);
	}

	public void setSwitchException(boolean isSwitchException) {
		this.isSwitchException = isSwitchException;
	}

	public boolean isSwitchException() {
		return isSwitchException;
	}

}
