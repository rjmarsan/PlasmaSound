package com.rj.processing.mt;

import android.view.MotionEvent;

public interface MTCallback {

	public void touchEvent(MotionEvent me, int i, float x, float y, float vx, float vy, float size);
	
}
