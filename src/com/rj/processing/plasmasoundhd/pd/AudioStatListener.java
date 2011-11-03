package com.rj.processing.plasmasoundhd.pd;

import org.puredata.core.PdReceiver;

import android.util.Log;

public class AudioStatListener implements PdReceiver{
	public float audiolevel = 0f;

	@Override
	public void print(final String s) {	
		//Log.d("PDManager", "recieved print! "+s);

	}

	@Override
	public void receiveBang(final String source) {
		//Log.d("PDManager", "recieved bang! "+source);
	}

	@Override
	public void receiveFloat(final String source, final float x) {
		//Log.d("PDManager", "recieved float! "+source+" : "+x);
		if (source.equalsIgnoreCase("mainlevel")) {
			audiolevel = x;
		}
	}
	@Override
	public void receiveList(final String source, final Object... args) {
		//Log.d("PDManager", "recieved list! "+source);
	}
	@Override
	public void receiveMessage(final String source, final String symbol,
			final Object... args) {	
		//Log.d("PDManager", "recieved message! "+source);
	}
	@Override
	public void receiveSymbol(final String source, final String symbol) {		
		//Log.d("PDManager", "recieved symbol! "+source);
	}
};

