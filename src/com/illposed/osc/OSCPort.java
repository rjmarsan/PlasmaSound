/**
 * @author cramakrishnan
 *
 * Copyright (C) 2004, C. Ramakrishnan / Illposed Software
 * All rights reserved.
 * 
 * See license.txt (or license.rtf) for license information.
 * 
 * 
 * OSCPort is an abstract superclass. To send OSC messages, use OSCPortOut. 
 * To listen for OSC messages, use OSCPortIn.
 *
 */

package com.illposed.osc;

import java.net.*;
import java.io.IOException;

public abstract class OSCPort {

	protected DatagramSocket socket;
	protected int port;
	
	/**
	 * The port that the SuperCollider synth engine ususally listens too
	 */
	public static final int defaultSCOSCPort = 57110;
	
	/**
	 * The port that the SuperCollider language engine ususally listens too
	 */
	public static final int defaultSCLangOSCPort = 57120;
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		super.finalize();
		socket.close();
	}
	
	/**
	 * Close the socket and free-up resources. It's recommended that clients call
	 * this when they are done with the port.
	 */
	public void close() {
		socket.close();
	}

}
