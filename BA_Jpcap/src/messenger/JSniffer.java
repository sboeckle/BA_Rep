package messenger;

/* Program: Console TCP/IP packet sniffer, using jpcap
 * Author: Plaguez
 * Date: 01.01.2008
 */

import java.net.*;
import java.io.*;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.packet.*;

class JSniffer {

	/* variables */
	JpcapCaptor captor;
	NetworkInterface[] devices;
	String str, info;
	int i, choice;

	public static void main(String args[]) {
		new JSniffer();
	}

	public JSniffer() {
	    	     
		     /* first fetch available interfaces to listen on */
		devices = JpcapCaptor.getDeviceList();
			System.out.println("Available interfaces: ");
			
			for(i=0; i<devices.length; i++) {
				//print out its name and description
				  System.out.println(i+": "+devices[i].name + "(" + devices[i].description+")");

				  //print out its datalink name and description
				  System.out.println(" datalink: "+devices[i].datalink_name + "(" + devices[i].datalink_description+")");

				  //print out its MAC address
				  System.out.print(" MAC address:");
				  for (byte b : devices[i].mac_address)
				    System.out.print(Integer.toHexString(b&0xff) + ":");
				  System.out.println();

				  //print out its IP address, subnet mask and broadcast address
				  for (NetworkInterfaceAddress a : devices[i].addresses)
				    System.out.println(" address:"+a.address + " " + a.subnet + " "+ a.broadcast);
			}
		            System.out.println("-------------------------\n");
		            choice = Integer.parseInt(getInput("Choose interface (0,1..): "));
			    System.out.println("Listening on interface -> "+devices[choice].name);
			    System.out.println("-------------------------\n");
		  
		  
		  
		  /*Setup device listener */
		  try {
		         captor=JpcapCaptor.openDevice(devices[choice], 65535, false, 20);
			 /* listen for TCP/IP only */
			 captor.setFilter("ip and tcp", true);
		      }
		         catch(IOException ioe) { ioe.printStackTrace(); }
		  
		  
		  /* start listening for packets */
		//  while (true) {
		    for (int i = 0; i < 40;) {
					
				
			  TCPPacket info = (TCPPacket)captor.getPacket();
		      if(info != null){
		    	  System.out.println("--------------------TCP/IP Packet recived--------------------");
		    	  System.out.println("From: "+ info.src_ip);
		    	  System.out.println("To: "+ info.dst_port);
		    	  System.out.println("is acknowledged: " +info.ack);
		    	  System.out.println("AcknowledgeNumber: " +info.ack_num);
		          System.out.print(getPacketText(info));
		          i++;
	             }
			}
	    //	}
	    }

	/* get user input */
	public static String getInput(String q) {
		String input = "";
		System.out.print(q);
		BufferedReader bufferedreader = new BufferedReader(
				new InputStreamReader(System.in));
		try {
			input = bufferedreader.readLine();
		} catch (IOException ioexception) {
		}
		return input;
	}

	/* return packet data in true text */
	String getPacketText(Packet pack) {
		int i = 0, j = 0;
		byte[] bytes = new byte[pack.header.length + pack.data.length];

		System.arraycopy(pack.header, 0, bytes, 0, pack.header.length);
		System.arraycopy(pack.data, 0, bytes, pack.header.length,
				pack.data.length);
		StringBuffer buffer = new StringBuffer();

		for (i = 0; i < bytes.length;) {
			for (j = 0; j < 8 && i < bytes.length; j++, i++) {
				String d = Integer.toHexString((int) (bytes[i] & 0xff));
				buffer.append((d.length() == 1 ? "0" + d : d) + " ");

				if (bytes[i] < 32 || bytes[i] > 126)
					bytes[i] = 46;
			}
		}
		return new String(bytes, i - j, j);
	}
} /* end class */
