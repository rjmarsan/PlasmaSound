/**
 * @author cramakrishnan
 *
 * Copyright (C) 2003, C. Ramakrishnan / Illposed Software
 * All rights reserved.
 * 
 * See license.txt (or license.rtf) for license information.
 * 
 * 
 * OSCJavaToByteArrayConverter is a helper class that translates
 * from Java types to the format the OSC spec specifies for those
 * types.
 *
 * This implementation is based on Markus Gaelli and
 * Iannis Zannos' OSC implementation in Squeak:
 * http://www.emergent.de/Goodies/
 */

package com.illposed.osc.utility;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Vector;


public class OSCJavaToByteArrayConverter {

	protected ByteArrayOutputStream stream = new ByteArrayOutputStream();
	private byte[] intBytes = new byte[4];

	/*public OSCJavaToByteArrayConverter() {
		super();
	}*/

	/**
	 * Creation date: (2/23/2001 2:43:25 AM)
	 * @param anArray java.lang.Object[]
	 *
	 */
	public void appendNullCharToAlignStream() {
		int mod = stream.size() % 4;
		int pad = 4 - mod;
		for (int i = 0; i < pad; i++)
			stream.write(0);
	}

	/**
	 * Creation date: (2/23/2001 2:21:53 AM)
	 * @return byte[]
	 */
	public byte[] toByteArray() {
		return stream.toByteArray();
	}

	/**
	 * Creation date: (2/23/2001 2:14:23 AM)
	 * @param bytes byte[]
	 */
	public void write(byte[] bytes) {
		writeBigEndToFourByteBoundry(bytes);
	}

	/**
	 * Creation date: (2/23/2001 2:21:04 AM)
	 * @param i int
	 */
	public void write(int i) {
		writeIntegerToByteArray(i);
	}

	/**
	 * Creation date: (2/23/2001 2:03:57 AM)
	 * @param f java.lang.Float
	 */
	public void write(Float f) {
		writeIntegerToByteArray(Float.floatToIntBits(f.floatValue()));
	}

	/**
	 * Creation date: (2/23/2001 2:08:36 AM)
	 * @param i java.lang.Integer
	 */
	public void write(Integer i) {
		writeIntegerToByteArray(i.intValue());
	}

	/**
	 * Creation date: (2/23/2001 1:57:35 AM)
	 * @param str java.lang.String
	 */
	public void write(String str) {
		writeLittleEndToFourByteBoundry(str.getBytes());
	}

	/**
	 * Creation date: (2/23/2001 2:08:36 AM)
	 * @param c char
	 */
	public void write(char c) {
		stream.write(c);
	}

	/**
	 * Creation date: (2/23/2001 2:02:54 AM)
	 * @param anObject java.lang.Object
	 */
	public void write(Object anObject) {
		// Can't do switch on class
		if (null == anObject)
			return;
		if (anObject instanceof Float) {
			write((Float) anObject);
			return;
		}
		if (anObject instanceof String) {
			write((String) anObject);
			return;
		}
		if (anObject instanceof Integer) {
			write((Integer) anObject);
			return;
		}
	}

	/**
	 * Creation date: (2/23/2001 2:43:25 AM)
	 * @param aClass Class
	 */
	public void writeType(Class c) {
		// A big ol' case statement -- what's polymorphism mean, again?
		// I really wish I could extend the base classes!

		// use the appropriate flags to tell SuperCollider what kind of 
		// thing it is looking at

		if (Integer.class.equals(c)) {
			stream.write('i');
			return;
		}
		if (java.math.BigInteger.class.equals(c)) {
			stream.write('h');
			return;
		}
		if (Float.class.equals(c)) {
			stream.write('f');
			return;
		}
		if (Double.class.equals(c)) {
			stream.write('d');
			return;
		}
		if (String.class.equals(c)) {
			stream.write('s');
			return;
		}
		if (Character.class.equals(c)) {
			stream.write('c');
			return;
		}
	}

	/**
	 * Creation date: (2/23/2001 2:43:25 AM)
	 * @param anArray java.lang.Object[]
	 */
	public void writeTypesArray(Object[] array) {
		// A big ol' case statement in a for loop -- what's polymorphism mean, again?
		// I really wish I could extend the base classes!

		for (int i = 0; i < array.length; i++) {
			if (null == array[i])
				continue;
			// if the array at i is a type of array write a [
			// This is used for nested arguments
			if (array[i].getClass().isArray()) {
				stream.write('[');
				// fill the [] with the SuperCollider types corresponding to the object
				// (i.e. Object of type String needs -s).
				writeTypesArray((Object[]) array[i]);
				// close the array
				stream.write(']');
				continue;
			}
			// Create a way to deal with Boolean type objects
			if (Boolean.TRUE.equals(array[i])) {
				stream.write('T');
				continue;
			}
			if (Boolean.FALSE.equals(array[i])) {
				stream.write('F');
				continue;
			}
			// go through the array and write the superCollider types as shown in the 
			// above method. the Classes derived here are used as the arg to the above method
			writeType(array[i].getClass());
		}
		// align the stream with padded bytes
		appendNullCharToAlignStream();
	}
	
	/**
	 * Same as writeSuperColliderTypes(Object[]), just that it takes a vector (for jdk1.1
	 * compatibility), rather than an array.
	 * @param vector  the collection I am to write out types for
	 */
	public void writeTypes(Vector vector) {
		// A big ol' case statement in a for loop -- what's polymorphism mean, again?
		// I really wish I could extend the base classes!

		Enumeration enm = vector.elements();
		Object nextObject;
		while (enm.hasMoreElements()) {
			nextObject = enm.nextElement();
			if (null == nextObject)
				continue;
			// if the array at i is a type of array write a [
			// This is used for nested arguments
			if (nextObject.getClass().isArray()) {
				stream.write('[');
				// fill the [] with the SuperCollider types corresponding to the object
				// (e.g., Object of type String needs -s).
				writeTypesArray((Object[]) nextObject);
				// close the array
				stream.write(']');
				continue;
			}
			// Create a way to deal with Boolean type objects
			if (Boolean.TRUE.equals(nextObject)) {
				stream.write('T');
				continue;
			}
			if (Boolean.FALSE.equals(nextObject)) {
				stream.write('F');
				continue;
			}
			// go through the array and write the superCollider types as shown in the 
			// above method. the Classes derived here are used as the arg to the above method
			writeType(nextObject.getClass());
		}
		// align the stream with padded bytes
		appendNullCharToAlignStream();
	}

	/**
	 * convert an integer to byte array
	 *
	 * @param value int
	 */
	private void writeIntegerToByteArray(int value) {
		byte[] intBytes = new byte[4];

		intBytes[3] = (byte)value; value>>>=8;
		intBytes[2] = (byte)value; value>>>=8;
		intBytes[1] = (byte)value; value>>>=8;
		intBytes[0] = (byte)value;

		try {
			stream.write(intBytes);
		} catch (IOException e) {
			throw new RuntimeException("You're screwed: IOException writing to a ByteArrayOutputStream");
		}
	}
	
	/**
	 * Line up the BigEnd of the bytes to a 4 byte boundry
	 *
	 * @param bytes byte[]
	 */
	private void writeBigEndToFourByteBoundry(byte[] bytes) {
		int mod = bytes.length % 4;
		// if the remainder == 0 write the bytes
		if (mod == 0) {
			try { stream.write(bytes); } catch (IOException e)
			{ throw new RuntimeException("You're screwed: IOException writing to a ByteArrayOutputStream"); }
			return;
		}
		// pad the bytes to lineup correctly
		int pad = 4 - mod;
		byte[] newBytes = new byte[pad + bytes.length];
		System.arraycopy(bytes,0,newBytes,pad,bytes.length);

		try { stream.write(newBytes); } catch (IOException e)
		{ throw new RuntimeException("You're screwed: IOException writing to a ByteArrayOutputStream"); }
	}

	/**
	 * Line up the LittleEnd of the bytes to a 4 byte boundry
	 * 
	 * @param bytes byte[]
	 */
	private void writeLittleEndToFourByteBoundry(byte[] bytes) {
		int mod = bytes.length % 4;
		// if the remainder == 0 write the bytes
		if (mod == 4) {
			try { stream.write(bytes); } catch (IOException e)
			{ throw new RuntimeException("You're screwed: IOException writing to a ByteArrayOutputStream"); }
			return;
		}
		// pad the bytes to lineup correctly
		int pad = 4 - mod;
		byte[] newBytes = new byte[pad + bytes.length];
		System.arraycopy(bytes,0,newBytes,0,bytes.length);

		try { stream.write(newBytes); } catch (IOException e)
		{ throw new RuntimeException("You're screwed: IOException writing to a ByteArrayOutputStream"); }
	}

}
