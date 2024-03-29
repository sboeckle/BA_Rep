package de.htwg_konstanz.in.switchable.withComments;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.management.monitor.Monitor;

/**
 * @author Steven B�ckle
 * 
 */
public class SwitchableInputStream extends InputStream {

	private InputStream inputStream;
	private BlockingQueue<InputStream> newInputStreams;
	public static Object monitor = new Object();
	private boolean isSwitching = false;

	private int numberOfBytesReceived;

	public int getNumberOfBytesReceived() {
		return numberOfBytesReceived;
	}

	public int getNumberOfBytesToRead() {
		return numberOfBytesToRead;
	}

	private int numberOfBytesToRead;

	private boolean isSwitchException = false;
	private boolean isReading = false;

	public boolean isReading() {
		return isReading;
	}

	public SwitchableInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		this.newInputStreams = new LinkedBlockingQueue<InputStream>();
		this.numberOfBytesToRead = 0;
		this.numberOfBytesReceived = 0;
	}

	/**
	 * Puts the new InputStream in the newInputStreams queue
	 * 
	 * @param newSocket
	 * @throws IOException
	 */
	public void putNewInputStream(InputStream inputStream) {
		this.newInputStreams.add(inputStream);
	}

	/**
	 * Sets the numberOfBytesToRead from the existing connection before it can
	 * be closed
	 * 
	 * @param numberOfBytesToRead
	 *            the numberOfBytesToRead to set
	 */
	public void setNumberOfBytesToRead(int numberOfBytesToRead) {
		System.out
				.println("setNumberofBytestoRead to : " + numberOfBytesToRead);
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
			if (numberOfBytesToRead != 0) {
				System.out
						.println("numberOfBytesToRead > numberOfBytesReceived ? :"
								+ numberOfBytesToRead
								+ " / "
								+ numberOfBytesReceived);
				if (numberOfBytesToRead > numberOfBytesReceived) {
					int bytesToReadLeft = numberOfBytesToRead
							- numberOfBytesReceived;
					// are there more bytes left than the array can handle?
					// if so don't switch the stream.
					if (bytesToReadLeft > b.length) {
						data = this.inputStream.read(b, 0, b.length);
						numberOfBytesReceived += data;
						return data;
					} else {
						data = this.inputStream.read(b, 0, bytesToReadLeft);
						// has there been an end of file its because of
						// socketclosing on the other side
						if (data == -1) {
							System.out.println("bin drin");
							internStreamSwitch();
							return this.read(b, off, len);
						}
						// only switch the stream
						// if all the bytes which are left has been read
						if (data == bytesToReadLeft) {
							System.out.println(" last " + bytesToReadLeft
									+ "bytes read over old connection!");
							System.out.println("switching the Stream!!!!");
							// then switch it
							internStreamSwitch();
							return data;
						} else {
							System.out.println("Still Some Bytes Left!");
							numberOfBytesReceived += data;
							System.out.println("new numberOfBytesReceived : "
									+ numberOfBytesReceived);
							return data;
						}
					}
				}

			} else
				this.isReading = true;
			data = inputStream.read(b, off, len);

		} catch (SocketException e) {
			if (isSwitchException) {
					System.out
							.println("InputstreamException because of switching!!!");
					internStreamSwitch();
				return this.read(b, off, len);
				// Do Nothing
			} else {
				e.printStackTrace();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}

		if (data != -1) {
			numberOfBytesReceived += data;
		} else {
			System.out.println("-------------------1 ");
		}
		this.isReading = false;
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) {
		int data = 0;
		try {
			if (numberOfBytesToRead != 0) {
				System.out
						.println("numberOfBytesToRead > numberOfBytesReceived ? :"
								+ numberOfBytesToRead
								+ " / "
								+ numberOfBytesReceived);
				if (numberOfBytesToRead > numberOfBytesReceived) {
					int bytesToReadLeft = numberOfBytesToRead
							- numberOfBytesReceived;
					// are there more bytes left than the array can handle?
					// if so don't switch the stream.
					if (bytesToReadLeft > b.length) {
						data = this.inputStream.read(b, 0, b.length);
						numberOfBytesReceived += data;
						return data;
					} else {
						data = this.inputStream.read(b, 0, bytesToReadLeft);
						// has there been an end of file? if so, its because of
						// socket closing on the other side
						if (data == -1) {
							System.out.println("bin drin");
							internStreamSwitch();
							return this.read(b);
						}
						// only switch the stream
						// if all the bytes which are left has been read
						if (data == bytesToReadLeft) {
							System.out.println(" last " + bytesToReadLeft
									+ "bytes read over old connection!");
							System.out.println("switching the Stream!!!!");
							// then switch it
							internStreamSwitch();
							return data;
						} else {
							System.out.println("Still Some Bytes Left!");
							numberOfBytesReceived += data;
							System.out.println("new numberOfBytesReceived : "
									+ numberOfBytesReceived);
							return data;
						}
					}
				}

			} else
				this.isReading = true;
			data = inputStream.read(b);

		} catch (SocketException e) {
			if (isSwitchException) {
					System.out
							.println("InputstreamException because of switching!!!");
					internStreamSwitch();
				return this.read(b);
				// Do Nothing
			} else {
				e.printStackTrace();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}

		if (data != -1) {
			numberOfBytesReceived += data;
		} else {
			System.out.println("-------------------1 ");
		}
		this.isReading = false;
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() {
		int data = 0;
		try {
			if (numberOfBytesToRead != 0) {
				System.out
						.println("numberOfBytesToRead > numberOfBytesReceived ? :"
								+ numberOfBytesToRead
								+ " / "
								+ numberOfBytesReceived);
				if (numberOfBytesToRead > numberOfBytesReceived) {
					int bytesToReadLeft = numberOfBytesToRead
							- numberOfBytesReceived;
					System.out.println("bytesToReadLeft: " + bytesToReadLeft);
					data = this.inputStream.read();
					if (data == -1) {
						System.out.println("bin drin");
						internStreamSwitch();
						return this.read();
					} else {
						numberOfBytesReceived++;
						return data;
					}
				} else {
					internStreamSwitch();
					return this.read();
				}
			}
			this.isReading = true;
			data = inputStream.read();
			// System.out.println("DATA recieved: " + data);
		} catch (SocketException e) {
			if (isSwitchException) {
				System.err
						.println("InputstreamException because of switching!!!");
					internStreamSwitch();
					setSwitchException(false);
					return this.read();
			} else {
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (data != -1) {
			numberOfBytesReceived += data;
		} else {
			if (isSwitching) {
				internStreamSwitch();
				return this.read();
			} else
				System.out.println("-------------------1 ");
		}
		this.isReading = false;

		return data;

	}

	/**
	 * Actually switches the InputStream to the next one in the queue
	 * newInputStreams
	 * 
	 * @throws InterruptedException
	 */
	public void internStreamSwitch() {
		try {
			System.out.println("trying to actually switch the inputStream...");
			synchronized (monitor) {
				System.out.println("jetzt kommt take!");
				inputStream = newInputStreams.take();
				System.out.println("take hat geklappt!");
				this.numberOfBytesReceived = 0;
				this.numberOfBytesToRead = 0;
				this.isSwitching = false;
				monitor.notify();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public void setSwitchException(boolean isSwitchException) {
		this.isSwitchException = isSwitchException;
	}

	public boolean isSwitchException() {
		return isSwitchException;
	}

	public void setSwitching(boolean b) {
		this.isSwitching = b;
	}
}
