/* $Id: OSCByteArrayToJavaConverter.java,v 1.1.1.1 2006/11/13 14:47:22 modin Exp $
 * Created on 28.10.2003
 */
package com.illposed.osc.utility;

import java.math.BigInteger;
import java.util.Date;

import com.illposed.osc.*;

/**
 * @author cramakrishnan
 *
 * Copyright (C) 2003, C. Ramakrishnan / Auracle
 * All rights reserved.
 * 
 * See license.txt (or license.rtf) for license information.
 */
public class OSCByteArrayToJavaConverter {

	byte[] bytes;
	int bytesLength;
	int streamPosition;

	private byte[] intBytes   = new byte[4];
	private byte[] floatBytes = new byte[4];

	private	byte[] secondBytes = new byte[8];
	private	byte[] picosecBytes = new byte[8];

	/**
	 * Helper object for converting from a byte array to Java objects
	 */
	/*public OSCByteArrayToJavaConverter() {
		super();
	}*/

	public OSCPacket convert(byte[] byteArray, int bytesLength) {
		bytes = byteArray;
		this.bytesLength = bytesLength;
		streamPosition = 0;
		if (isBundle())
			return convertBundle();
		else
			return convertMessage();
	}

	private boolean isBundle() {
		// only need the first 7 to check if it is a bundle
		String bytesAsString = new String(bytes, 0, 7);
		return bytesAsString.startsWith("#bundle");
	}                               
                               
	private OSCBundle convertBundle() {
		// skip the "#bundle " stuff
		streamPosition = 8;
		Date timestamp = readTimeTag();
		OSCBundle bundle = new OSCBundle(timestamp);
		OSCByteArrayToJavaConverter myConverter = new OSCByteArrayToJavaConverter();
		while (streamPosition < bytesLength) {
			// recursively read through the stream and convert packets you find
			int packetLength = ((Integer) readInteger()).intValue();
			byte[] packetBytes = new byte[packetLength];
			//streamPosition++;
			System.arraycopy(bytes,streamPosition,packetBytes,0,packetLength);
			streamPosition+=packetLength;
			//for (int i = 0; i < packetLength; i++)
			//	packetBytes[i] = bytes[streamPosition++];
			OSCPacket packet = myConverter.convert(packetBytes, packetLength);
			bundle.addPacket(packet);
		}
		return bundle;
	}

	private OSCMessage convertMessage() {
		OSCMessage message = new OSCMessage();
		message.setAddress(readString());
		char[] types = readTypes();
		if (null == types) {
			// we are done
			return message;
		}
		moveToFourByteBoundry();
		for (int i = 0; i < types.length; i++) {
			if ('[' == types[i]) {
				// we're looking at an array -- read it in
				message.addArgument(readArray(types, i));
				// then increment i to the end of the array
				while (']' != types[i])
					i++;
			} else
				message.addArgument(readArgument(types[i]));
		}
		return message;
	}

	private String readString() {
		int strLen = lengthOfCurrentString();
		char[] stringChars = new char[strLen];
		//System.arraycopy(bytes,streamPosition,stringChars,0,strLen);
		//streamPosition+=strLen;
		for (int i = 0; i < strLen; i++)
			stringChars[i] = (char) bytes[streamPosition++];
		moveToFourByteBoundry();
		return new String(stringChars);
	}

	/**
	 * @return a char array with the types of the arguments
	 */
	private char[] readTypes() {
		// the next byte should be a ","
		if (bytes[streamPosition] != 0x2C)
			return null;
		streamPosition++;
		// find out how long the list of types is
		int typesLen = lengthOfCurrentString();
		if (0 == typesLen) {
			return null;
		}
		// read in the types
		char[] typesChars = new char[typesLen];
		for (int i = 0; i < typesLen; i++) {
			typesChars[i] = (char) bytes[streamPosition++];
		}
		return typesChars;
	}

	/**
	 * @param c type of argument
	 * @return a Java representation of the argument
	 */
	private Object readArgument(char c) {
		switch (c) {
			case 'i' :
				return readInteger();
			case 'h' :
				return readBigInteger();
			case 'f' :
				return readFloat();
			case 'd' :
				return readDouble();
			case 's' :
				return readString();
			case 'c' :
				return readChar();
			case 'T' :
				return Boolean.TRUE;
			case 'F' :
				return Boolean.FALSE;
		}

		return null;
	}

	/**
	 * @return a Character
	 */
	private Object readChar() {
		return new Character((char) bytes[streamPosition++]);
	}

	/**
	 * @return a Double
	 */
	private Object readDouble() {
		return readFloat();
	}

	/**
	 * @return a Float
	 */
	private Object readFloat() {
		//byte[] floatBytes = new byte[4];
		floatBytes[0] = bytes[streamPosition++];
		floatBytes[1] = bytes[streamPosition++];
		floatBytes[2] = bytes[streamPosition++];
		floatBytes[3] = bytes[streamPosition++];

		int floatBits = 
			((floatBytes[3] & 0xFF) ) +
			((floatBytes[2] & 0xFF) << 8) +
			((floatBytes[1] & 0xFF) << 16) +
			((floatBytes[0] & 0xFF) << 24);
		
		return new Float(Float.intBitsToFloat(floatBits));
	}

	/**
	 * @return a BigInteger
	 */
	private Object readBigInteger() {
		//byte[] intBytes = new byte[4];
		intBytes[0] = bytes[streamPosition++];
		intBytes[1] = bytes[streamPosition++];
		intBytes[2] = bytes[streamPosition++];
		intBytes[3] = bytes[streamPosition++];

		int intBits = 
			((intBytes[3] & 0xFF) ) +
			((intBytes[2] & 0xFF) << 8) +
			((intBytes[1] & 0xFF) << 16) +
			((intBytes[0] & 0xFF) << 24);
		
		return new Integer(intBits);
	}

	/**
	 * @return an Integer
	 */
	private Object readInteger() {
		//byte[] intBytes = new byte[4];
		intBytes[0] = bytes[streamPosition++];
		intBytes[1] = bytes[streamPosition++];
		intBytes[2] = bytes[streamPosition++];
		intBytes[3] = bytes[streamPosition++];

		int intBits = 
			((intBytes[3] & 0xFF) ) +
			((intBytes[2] & 0xFF) << 8) +
			((intBytes[1] & 0xFF) << 16) +
			((intBytes[0] & 0xFF) << 24);
		
		return new Integer(intBits);
	}
	
	/**
	 * @return a Date
	 */
	private Date readTimeTag() {
		//byte[] secondBytes = new byte[8];
		//byte[] picosecBytes = new byte[8];
		/*for (int i = 4; i < 8; i++)
			secondBytes[i] = bytes[streamPosition++];
		for (int i = 4; i < 8; i++)
			picosecBytes[i] = bytes[streamPosition++];*/
		System.arraycopy(bytes,streamPosition,secondBytes,4,4);
		streamPosition+=4;
		System.arraycopy(bytes,streamPosition,picosecBytes,4,4);
		streamPosition+=4;
		
		BigInteger secsSince1900 = new BigInteger(secondBytes);
		long secsSince1970 =  secsSince1900.longValue() - OSCBundle.SECONDS_FROM_1900_to_1970.longValue();
		if (secsSince1970 < 0) secsSince1970 = 0; // no point maintaining times in the distant past
		BigInteger picosecs = new BigInteger(picosecBytes);
		long millisecs = (secsSince1970 * 1000) + (picosecs.longValue() / 1000);
		return new Date(millisecs);
	}

	/**
	 * @param types
	 * @param i
	 * @return an Array
	 */
	private Object[] readArray(char[] types, int i) {
		int arrayLen = 0;
		while (types[i + arrayLen] != ']')
			arrayLen++;
		Object[] array = new Object[arrayLen];
		for (int j = 0; i < arrayLen; j++) {
			array[j] = readArgument(types[i + j]);
		}
		return array;
	}

	private int lengthOfCurrentString() {
		int i = 0;
		while (bytes[streamPosition + i] != 0)
			i++;
		return i;
	}

	private void moveToFourByteBoundry() {
		// If i'm already at a 4 byte boundry, I need to move to the next one
		int mod = streamPosition % 4;
		streamPosition += (4 - mod);
	}

}

