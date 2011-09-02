package com.rj.processing.plasmasoundhd.pd.instruments;

import org.puredata.core.PdBase;

public class Parameter {
	String name;
	public final static int CONTINUOUS = 0;  //any value from min to max
	
	boolean enabled = true;
	
	boolean global = false;
	
	//continuous parameters
	float min = 0;
	float max = 1;
	float lastVal;
	
	float defaultval = 0;
	float defaultvalnaive = 0;
	
	public Parameter(final String name, final boolean global) {
		this(name, global, 0f);
	}
	public Parameter(final String name, final boolean global, final float defaultval) {
		this.name = name;
		this.global = global;
		this.defaultval = defaultval;
	}
	
	
	/**
	 * this function only applies if you're in CONTINUOUS mode
	 * @param values
	 */
	public void setMinMax(final float min, final float max) {
		this.min = min;
		this.max = max;
	}
		

	

	
	public String getName() {
		return name;
	}
	
	public float getRange() {
		return this.max - this.min;
	}
	
	public float getMin() {
		return this.min;
	}
	
	public float getMax() {
		return this.max;
	}
	
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public boolean isGlobal() {
		return this.global;
	}
	
	public void setDefault(final float val) {
		this.defaultval = val;
		this.defaultvalnaive = denormalizeValue(val);
	}
	public void setDefaultNaive(final float val) {
		this.defaultval = normalizeValue(val);
		this.defaultvalnaive = val;
	}
	
	//a number between 0 and 1, will be converted to normal numers
	public float normalizeValue(final float value) {
		return this.min + value*this.getRange();
	}
	public float denormalizeValue(final float value) {
		return (value - this.min)/this.getRange();
	}
	
	
	public String getParamName() {
		return name;
	}
	public String getParamName(final int num) {
		return name+num;
	}
	/**
	 * Push a value that's already been put within the range of the output
	 * @param value
	 */
	public void pushNormalValue(final float value) {
		PdBase.sendFloat(getParamName(), value);
		System.out.println("Setting "+this.name+" to:"+value);
	}
	/**
	 * Push a value thata's already been put within the range of the output, to a specific channel
	 * @param value
	 * @param num
	 */
	public void pushNormalValue(final float value, final int num) {
		PdBase.sendFloat(getParamName(num), value);
		System.out.println("Setting "+this.name+"["+num+"] to:"+value);
	}
	
	/**
	 * Push a single value, to be normalized to the range of the output
	 * @param abnormal
	 */
	public void pushValue(final float abnormal) {
		lastVal = abnormal;
		final float value = normalizeValue(abnormal);
//		setValue(value);
		pushNormalValue(value);
	}
	
	/**
	 * Push a single value to a channel, to be normalized to the range of the output
	 * @param abnormal
	 * @param num
	 */
	public void pushValue(final float abnormal, final int num) {
		lastVal = abnormal;
		final float value = normalizeValue(abnormal);
//		setValue(value);
		pushNormalValue(value, num);
	}
	
	/**
	 * This funciton will automatically check if it's global or not and act accordingly
	 * @param abnormal
	 * @param num
	 */
	public void pushValueNaive(final float abnormal, final int num) {
		if (isGlobal()) pushValue(abnormal);
		else pushValue(abnormal, num);
	}
	
	public void pushDefaultNaive(final int num) {
		if (isGlobal()) pushDefault();
		else pushDefault(num);
	}
	public void pushDefault(final int num) {
		pushNormalValue(defaultval, num);
	}
	public void pushDefault() {
		pushNormalValue(defaultval);
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
