package de.htwg_konstanz.in.copy;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Ellen Wieland
 * 
 */
public class SwitchableInputStream extends InputStream {

	private InputStream inputStream;
	private BlockingQueue<InputStream> newInputStreams;

	private int numberOfBytesReceived;
	private int numberOfBytesToRead;

	private boolean isSwitchException = false;

	public SwitchableInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		this.newInputStreams = new LinkedBlockingQueue<InputStream>();
		this.numberOfBytesToRead = 0;
		this.numberOfBytesReceived = 0;
	}

	public void switchInputStream(InputStream inputStream) {
		this.newInputStreams.add(inputStream);
	}

	/**
	 * @param numberOfBytesToRead
	 *            the numberOfBytesToRead to set
	 */
	public void setNumberOfBytesToRead(int numberOfBytesToRead) {
		this.numberOfBytesToRead = numberOfBytesToRead;
	}

	// delegate work to internal input stream

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return inputStream.available();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		inputStream.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void mark(int readlimit) {
		inputStream.mark(readlimit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return inputStream.markSupported();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int data = 0;
		try {
			data = inputStream.read(b, off, len);
		} catch (SocketException e) {
			if (isSwitchException) {
				System.err
						.println("InputstreamException because of switching!!!");
				try {
					inputStream = newInputStreams.take();
					numberOfBytesReceived = 0;
					numberOfBytesToRead = 0;
					isSwitchException = false;
					return this.read(b,off,len);
				} catch (InterruptedException InnerE) {
					// TODO Auto-generated catch block
					System.out.println("InnerException");
					InnerE.printStackTrace();
				}
				return this.read(b,off,len);
				// Do Nothing
			} else {
				e.printStackTrace();
			}
		}
		if (data != -1) {
			numberOfBytesReceived += data;
		} else {
			System.out.println("-------------------1 ");
		}

		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException {
		int data = 0;
		try {
			data = inputStream.read(b);
		} catch (SocketException e) {
			if (isSwitchException) {
				System.err
						.println("InputstreamException because of switching!!!");
				try {
					inputStream = newInputStreams.take();
					numberOfBytesReceived = 0;
					numberOfBytesToRead = 0;
					isSwitchException = false;
					return this.read(b);
				} catch (InterruptedException InnerE) {
					// TODO Auto-generated catch block
					System.out.println("InnerException");
					InnerE.printStackTrace();
				}
				return this.read(b);
				// Do Nothing
			} else {
				e.printStackTrace();
			}
		}

		if (data != -1) {
			numberOfBytesReceived += data;
		} else {
			System.out.println("-------------------1 ");
		}

		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int data = 0;
		try {
			data = inputStream.read();
		} catch (SocketException e) {
			if (isSwitchException) {
				System.err
						.println("InputstreamException because of switching!!!");
				try {
					inputStream = newInputStreams.take();
					numberOfBytesReceived = 0;
					numberOfBytesToRead = 0;
					isSwitchException = false;
					return this.read();
				} catch (InterruptedException InnerE) {
					// TODO Auto-generated catch block
					System.out.println("InnerException");
					InnerE.printStackTrace();
				}
				return this.read();
				// Do Nothing
			} else {
				e.printStackTrace();
			}
		}
		if (data != -1) {
			numberOfBytesReceived += data;
		} else {
			System.out.println("-------------------1 ");
		}

		return data;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		inputStream.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		return inputStream.skip(n);
	}

	public void setSwitchException(boolean isSwitchException) {
		this.isSwitchException = isSwitchException;
	}

	public boolean isSwitchException() {
		return isSwitchException;
	}
}
