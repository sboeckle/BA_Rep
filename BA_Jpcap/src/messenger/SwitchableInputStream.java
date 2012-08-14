package messenger;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (data != -1) {
			numberOfBytesReceived += data;
		} else {
			System.out.println("-------------------1 ");
		}		

		if (numberOfBytesReceived == (numberOfBytesToRead + 1)) {
			// test if EOF was caused because other side wants to switch
			// connection

			try {
				inputStream = newInputStreams.take();
				numberOfBytesReceived = 0;
				numberOfBytesToRead = 0;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return this.read(b, off, len);
		} else {
			return data;
		}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (data != -1) {
			numberOfBytesReceived += data;
		} else {
			System.out.println("-------------------1 ");
		}		

		if (numberOfBytesReceived == (numberOfBytesToRead + 1)) {
		
			try {
				inputStream = newInputStreams.take();
				numberOfBytesReceived = 0;
				numberOfBytesToRead = 0;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return this.read(b);
	
		} else {
			return data;
		}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (data != -1) {
			numberOfBytesReceived += data;
		} else {
			System.out.println("-------------------1 ");
		}		

		if (numberOfBytesReceived == (numberOfBytesToRead + 1)) {
			// test if EOF was caused because other side wants to switch
			// connection
			
			try {
				inputStream = newInputStreams.take();
				numberOfBytesReceived = 0;
				numberOfBytesToRead = 0;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return this.read();
			
		} else {
			return data;
		}
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
}
