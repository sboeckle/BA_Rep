package de.htwg_konstanz.in.trimmed;

public class ADS {

	public int read(byte[] b, int off, int len) 
	throws IOException {
	 int data = 0;
	 try {
	   if (numberOfBytesToRead != 0) {
	     if (numberOfBytesToRead > numberOfBytesReceived) {
	      int bytesToReadLeft = numberOfBytesToRead
		    - numberOfBytesReceived;
		 // are there more bytes left than the array can handle
		 if (bytesToReadLeft > b.length) {
		 data = this.inputStream.read(b, 0, b.length);
		 numberOfBytesReceived += data;
		 return data;
		} 
		else {
		   data = this.inputStream.read(b, 0, bytesToReadLeft);
		   // only switch the stream if all the bytes which are
		   // left has been read
		   if (data == bytesToReadLeft) {
		    internStreamSwitch();
		    return data;
		   } 
		   else {
		    // Still some bytes left to read
		    numberOfBytesReceived += data;
		    return data;
		 }
		}
	   }
	  }
	  else {
	   this.isReading = true;
	   data = inputStream.read(b, off, len);
	  }
	
}
