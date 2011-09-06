package com.rj.processing.plasmasoundhd.waveform;

public class Waveform {
	public float[] points; //all from -1 to 1 plz
	
	public Waveform(float[] points) {
		this.points = points;
	}
	public Waveform(int size) {
		this.points = new float[size];
	}
	
}
