package com.rj.processing.plasmasoundhd.pd.instruments;

import org.puredata.core.PdBase;

public class NonPDParameter extends Parameter {
	
	public NonPDParameter(final String name, final boolean global) {
		super(name, global, 0f);
	}
	
	
	/**
	 * Push a value that's already been put within the range of the output
	 * @param value
	 */
	public void pushNormalValue(final float value) {
		if (DEBUGGG) System.out.println("Setting "+this.name+" to:"+value);
	}
	/**
	 * Push a value thata's already been put within the range of the output, to a specific channel
	 * @param value
	 * @param num
	 */
	public void pushNormalValue(final float value, final int num) {
		if (DEBUGGG) System.out.println("Setting "+this.name+"["+num+"] to:"+value);
	}
	
	
	public float getLastValue() {
		return this.lastVal;
	}
	
	public float getDefaultValue() {
		return this.defaultval;
	}
	
	public float getDefaultValueNaive() {
		return this.defaultvalnaive;
	}

	

}
