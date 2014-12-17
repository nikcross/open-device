package org.onestonesoup.opendevice.display;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//javac -cp jssc.jar LEDBadge.java
//java -cp jssc.jar:./ LEDBadge "TEST 1" 

// Example Class showing how we can set up a Maplin's LED Scrolling Badge in Java
// Using JSSC Serial Port class 
// Johnny Wilson, Brighton July 2014

public class MaplinLEDDisplay {

	static public int style = 0;
	static public int speed = 0;

	private int unsignedint(byte b) {
		return (b < 0 ? (int) b + 256 : (int) b);
	}

	// buildScrollingMessage - build byte array to send to our Maplin's LED
	// Scrolling Badge
	// @message 250 size max string -
	// @style -1 is HOLD, 0 is SCROLL, 1 = STAR DROP, 2 = FLASH
	// @speed 0 is fast , 4 is slow

	private byte[] buildScrollingMessage(String message, int style, int speed)
			throws IOException {

		int msgBlockSize = 0;
		int sum = 0;

		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		byte[] finalHeader = { (byte) 0x02, 0x33, 0x01 };
		byte[] blockHeader = { 0x02, 0x31, 0x06 };

		byte[] bmessage = message.substring(0, Math.min(250, message.length()))
				.getBytes(); // Just the 1st 250 characters will be used
		int messageSize = bmessage.length;
		log("Messaage : " + message);
		log("Message size of " + message.length() + " clipped to "
				+ messageSize);

		ba.write(0x00);
		ba.write(blockHeader);
		ba.write(0x00);
		ba.write((-speed + 0x35)); // speed
		ba.write(0x31); // Memory block
		ba.write((style + 0x42)); // display style
		ba.write(messageSize); // total size of message

		// 1st Block of text
		msgBlockSize = Math.min(messageSize, 60);
		sum = 0;
		for (int i = 0; i < msgBlockSize; i++) {
			ba.write(bmessage[i]);
			int val = unsignedint(bmessage[i]);
			sum = sum + val;
		}
		sum = sum + messageSize - 33 + style - speed; // this is the check sum
														// formula for scrolling
														// messages at speed 5

		// Write out remaining zeros
		for (int i = 0; i < 60 - msgBlockSize; i++) {
			ba.write(0x00);
		}
		// 69th pos
		ba.write(sum); // Save 1st Check Sum

		// 2nd block

		ba.write(blockHeader);
		ba.write(0x40);

		msgBlockSize = Math.max(0, Math.min((messageSize - 60), 64));

		sum = 0;
		for (int i = 0; i < msgBlockSize; i++) {
			ba.write(bmessage[i + 60]);
			int val = unsignedint(bmessage[i + 60]);
			sum = sum + val;
		}
		sum = (sum + 0x77);

		// Write out remaining zeros
		for (int i = 0; i < 64 - msgBlockSize; i++) {
			ba.write(0x00);
		}

		// 138th Position
		ba.write(sum); // Save 2nd Check Sum

		// 3rd Block
		ba.write(blockHeader);
		ba.write(0x80);

		msgBlockSize = Math.max(0, Math.min((messageSize - 60 - 64), 64)); // I'll
																			// leave
																			// you
																			// to
																			// tidy
																			// these
																			// bits
																			// up
																			// -

		sum = 0;
		for (int i = 0; i < msgBlockSize; i++) {
			ba.write(bmessage[i + 60 + 64]);
			int val = unsignedint(bmessage[i + 60 + 64]);
			sum = sum + val;
		}
		sum = sum + 0xb7;

		// Write out remaining zeros
		for (int i = 0; i < 64 - msgBlockSize; i++) {
			ba.write(0x00);
		}

		// 207th Position
		ba.write(sum); // Save 3rd Check Sum

		// final 4th block

		ba.write(blockHeader);
		ba.write(0xC0);
		msgBlockSize = Math.max(0, Math.min((messageSize - 60 - 64 - 64), 62));

		sum = 0;
		for (int i = 0; i < msgBlockSize; i++) {
			ba.write(bmessage[i + 60 + 64 + 64]);
			int val = unsignedint(bmessage[i + 60 + 64 + 64]);
			sum = sum + val;
		}
		sum = sum + 0xf7;

		// Write out remaining zeros.
		// Note although we only allow 250 chars in total [62 chars in final
		// block]
		// - we must write out 64 bytes for final block
		for (int i = 0; i < 64 - msgBlockSize; i++) {
			ba.write(0x00);
		}

		// 276th position
		ba.write(sum);

		// Final few bytes
		ba.write(finalHeader);

		// final size of array should be 280 bytes!
		// log("Size = " + ba.size()) ;

		return ba.toByteArray();
	}

	static public void main(String [] args)
	 {
	  SerialNativeInterface sni = new SerialNativeInterface() ;
	  //log("SCROLLING MAPLIN LED BADGE") ;
	  //log("jssc Version " + sni.getLibraryVersion()) ;
	  String[] portNames = SerialPortList.getPortNames();
	  
	  if (portNames.length == 0) {
	   log("No Serial Ports to print to!") ;
	   return ;
	  }
	  
	  //log("Message " + args[0]) ;
	  
	  for (String name : portNames) {
	   log("Port : " + name) ;
	  }
	  
	    SerialPort serialPort = new SerialPort(portNames[0]);  // FUDGE here - Assume we are using the 1st Serial Port in our list.
	 
	    try {
	             serialPort.openPort();//Open serial port
	             serialPort.setParams(SerialPort.BAUDRATE_38400, 
	                                  SerialPort.DATABITS_8,
	                                  SerialPort.STOPBITS_1,
	                                  SerialPort.PARITY_NONE);

	     
	    LEDBadge badge = new LEDBadge() ;
	    // I have seen the serial writes fail on some occassions -
	    // My guess is that we are missing some kind of flow control
	    // from our set up - to tell the badge - that we are about to send.
	    // The failures are exacerbated when the BADGE is doing some more complicated 
	    // display (style) format - such as the falling 'star drop'.
	    // Please drop me a line if you manage to make your serial sends - more robust.
	    // Thanks - Johnny, johnnyw66 at gmail dot com
	             serialPort.writeBytes(badge.buildScrollingMessage(args[0],LEDBadge.style,LEDBadge.speed));//Write data to port

	              serialPort.closePort();//Close serial port
	         }
	         catch (SerialPortException|IOException ex) {
	             System.out.println(ex);
	         }
	        
	  
	 }
}
