package com.rj.processing.mt;

import java.util.ArrayList;


import android.view.MotionEvent;



public class MTManager {

	public MTCallback callback;
	
	private class Point {
		public final float x; 
		public final float y; 
		public long time;

		public Point(float x, float y) {
			this.x = x;
			this.y = y;
			time = System.currentTimeMillis();
		}
	}
//	public class Cursor {
//		public final Point firstPoint;
////		public final ///seee where this is going later. when you wake up.
//	}
	public ArrayList<Point> points;
	
	public MTManager(MTCallback callback) {
		this.callback = callback;
		this.points = new ArrayList<Point>(8);
	}

	public void surfaceTouchEvent(MotionEvent me) {
		int numPointers = me.getPointerCount();
		for (int i = 0; i < numPointers; i++) {
			touchEvent(me, i);
		}
	}
	
	public void touchEvent(MotionEvent me, int i) {		
		int pointerId = me.getPointerId(i);
		float x = me.getX(i);
		float y = me.getY(i);
		
		float vx = 0;
		float vy = 0;
		
		int index = me.findPointerIndex(pointerId);
		
		
		if (points.size() < index+1) {
			//points.ensureCapacity(index+4);
			points.add(null);
			points.add(null);
			points.add(null);
			points.add(null);
		}
		Point prevPoint = points.get(index);
		long curt = System.currentTimeMillis();
		if (prevPoint != null && curt-prevPoint.time < 100) {
			long t = curt-prevPoint.time;
			float dt = (float)t/1000f;
//			Log.d("asdf","Old point: "+prevPoint.x+" , "+prevPoint.y+" : "+prevPoint.time);
//			Log.d("asdf","Cur point: "+x+" , "+y+" : "+curt);
			vx = (x-prevPoint.x)*dt;
			vy = (y-prevPoint.y)*dt;
		}
		points.set(index, new Point(x,y));
		
		

		float size = me.getSize(i);

		callback.touchEvent(me, i, x, y, vx, vy, size);
	}

	
	
	
	
	
}
