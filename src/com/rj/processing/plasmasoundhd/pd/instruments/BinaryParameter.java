package com.rj.processing.plasmasoundhd.pd.instruments;

import org.puredata.core.PdBase;

public class BinaryParameter extends Parameter {
	public BinaryParameter(String name, boolean global) {
		super(name, global);
	}
	public void pushNormalValue(final float value) {
		PdBase.sendFloat(getParamName(), value == 0.0f? 0.0f : 1.0f);
	}
	public void pushNormalValue(final float value, final int num) {
		PdBase.sendFloat(getParamName(num), value == 0.0f? 0.0f : 1.0f);
	}
}
