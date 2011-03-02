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
	
	float defaultval = 0;
	
	public Parameter(String name, boolean global) {
		this(name, global, 0f);
	}
	public Parameter(String name, boolean global, float defaultval) {
		this.name = name;
		this.global = global;
		this.defaultval = defaultval;
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
	
	public void setDefault(float val) {
		this.defaultval = val;
	}
	public void setDefaultNaive(float val) {
		this.defaultval = normalizeValue(val);
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
//		System.out.println("Setting "+this.name+" to:"+value);
	}
	public void pushNormalValue(float value, int num) {
		PdBase.sendFloat(getParamName(num), value);
//		System.out.println("Setting "+this.name+"["+num+"] to:"+value);
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
		if (isGlobal()) pushValue(abnormal);
		else pushValue(abnormal, num);
	}
	
	public void pushDefaultNaive(int num) {
		if (isGlobal()) pushDefault();
		else pushDefault(num);
	}
	public void pushDefault(int num) {
		pushNormalValue(defaultval, num);
	}
	public void pushDefault() {
		pushNormalValue(defaultval);
	}
	
	

}
