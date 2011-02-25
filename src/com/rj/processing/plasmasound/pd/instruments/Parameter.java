package com.rj.processing.plasmasound.pd.instruments;

import org.puredata.core.PdBase;

public class Parameter {
	String name;
	public final static int CONTINUOUS = 0;  //any value from min to max
	
	boolean enabled = true;
	
	boolean global = false;
	
	//continuous parameters
	float min = 0;
	float max = 1;
	
	
	public Parameter(String name, boolean global) {
		this.name = name;
		this.global = global;
	}
	
	
	/**
	 * this function only applies if you're in CONTINUOUS mode
	 * @param values
	 */
	public void setMinMax(float min, float max) {
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
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public boolean isGlobal() {
		return this.global;
	}
	
	//a number between 0 and 1, will be converted to normal numers
	public float normalizeValue(float value) {
		return this.min + value*this.getRange();
	}
	
	
	public String getParamName() {
		return name;
	}
	public String getParamName(int num) {
		return name+num;
	}
	public void pushNormalValue(float value) {
		PdBase.sendFloat(getParamName(), value);
	}
	public void pushNormalValue(float value, int num) {
		PdBase.sendFloat(getParamName(num), value);
	}
	public void pushValue(float abnormal) {
		float value = normalizeValue(abnormal);
//		setValue(value);
		pushNormalValue(value);
	}
	public void pushValue(float abnormal, int num) {
		float value = normalizeValue(abnormal);
//		setValue(value);
		pushNormalValue(value, num);
	}
	
	/**
	 * This funciton will automatically check if it's global or not and act accordingly
	 * @param abnormal
	 * @param num
	 */
	public void pushValueNaive(float abnormal, int num) {
		if (isGlobal())
			pushValue(abnormal);
		else
			pushValue(abnormal, num);
	}
	
	

}
