package com.rj.processing.plasmasoundhd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import com.rj.processing.plasmasound.R;

public class Utils {
	 public static String objectToString(Serializable object) {
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    try {
		        new ObjectOutputStream(out).writeObject(object);
		        byte[] data = out.toByteArray();
		        out.close();

		        out = new ByteArrayOutputStream();
		        Base64OutputStream b64 = new Base64OutputStream(out,Base64.DEFAULT);
		        b64.write(data);
		        b64.close();
		        out.close();

		        return new String(out.toByteArray());
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    return null;
		}

		public static Object stringToObject(String encodedObject) {
		    try {
		        return new ObjectInputStream(new Base64InputStream(new ByteArrayInputStream(encodedObject.getBytes()),Base64.DEFAULT)).readObject();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return null;
		}}
