/**
 * @author cramakrishnan
 *
 * Copyright (C) 2004, C. Ramakrishnan / Illposed Software
 * All rights reserved.
 * 
 * See license.txt (or license.rtf) for license information.
 * 
 * 
 * OSCPortOut is the class that sends OSC messages.
 *
 * To send OSC, in your code you should instantiate and hold onto an OSCPort
 * pointing at the address and port number for the receiver.
 *
 * When you want to send an OSC message, call send().
 *
 * An example based on com.illposed.osc.test.OSCPortTest::testMessageWithArgs() :
		OSCPort sender = new OSCPort();
		Object args[] = new Object[2];
		args[0] = new Integer(3);
		args[1] = "hello";
		OSCMessage msg = new OSCMessage("/sayhello", args);
		 try {
			sender.send(msg);
		 } catch (Exception e) {
			 showError("Couldn't send");
		 }
 */

package com.illposed.osc;

import java.net.*;
import java.io.IOException;
import com.illposed.osc.utility.OSCByteArrayToJavaConverter;

public class OSCPortOut extends OSCPort {

	protected InetAddress address;

	/**
	 * Create an OSCPort that sends to newAddress, newPort
	 * @param newAddress InetAddress
	 * @param newPort int
	 */
	public OSCPortOut(InetAddress newAddress, int newPort) throws SocketException {
		socket = new DatagramSocket();
		address = newAddress;
		port = newPort;
	}

	/**
	 * Create an OSCPort that sends to newAddress, on the standard SuperCollider port
	 * @param newAddress InetAddress
	 *
	 * Default the port to the standard one for SuperCollider
	 */
	public OSCPortOut(InetAddress newAddress) throws SocketException {
		this(newAddress, defaultSCOSCPort);
	}

	/**
	 * Create an OSCPort that sends to localhost, on the standard SuperCollider port
	 * Default the address to localhost
	 * Default the port to the standard one for SuperCollider
	 */
	public OSCPortOut() throws UnknownHostException, SocketException {
		this(InetAddress.getLocalHost(), defaultSCOSCPort);
	}
	
	/**
	 * @param aPacket OSCPacket
	 */
	public void send(OSCPacket aPacket) throws IOException {
		byte[] byteArray = aPacket.getByteArray();
		DatagramPacket packet = 
			new DatagramPacket(byteArray, byteArray.length,address,port);
		socket.send(packet);
	}
}
