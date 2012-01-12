/* $Id: OSCListener.java,v 1.1.1.1 2006/11/13 14:47:21 modin Exp $
 * Created on 28.10.2003
 */
package com.illposed.osc;

import java.util.Date;

/**
 * @author cramakrishnan
 *
 * Copyright (C) 2003, C. Ramakrishnan / Auracle
 * All rights reserved.
 * 
 * See license.txt (or license.rtf) for license information.
 * 
 * Interface for things that listen for incoming OSC Messages
 */
public interface OSCListener {
	
	/**
	 * Accept an incoming OSCMessage
	 * @param time     the time this message is to be executed. null means execute now
	 * @param message  the message
	 */
	public void acceptMessage(Date time, OSCMessage message);

}
