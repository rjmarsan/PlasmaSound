package com.rj.processing.mt;

import java.util.ArrayList;

import android.util.Log;
import android.view.MotionEvent;



public class MTManager {

	public MTCallback callback;
	
	public ArrayList<Point> points;
	public ArrayList<Cursor> cursors;
	
	
	public MTManager(MTCallback callback) {
		this.callback = callback;
		this.points = new ArrayList<Point>(8);
		this.cursors = new ArrayList<Cursor>(8);
	}

	public void surfaceTouchEvent(MotionEvent me) {
		synchronized (cursors) {
			int numPointers = me.getPointerCount();
			if (numPointers == 0) {
				//callback.touchEvent(me, 0, 0,0,0,0,0);
			}
			for (int i = 0; i < numPointers; i++) {
				touchEvent(me, i);
			}
			if (me.getPointerCount() == 1 && me.getAction() == MotionEvent.ACTION_UP) {//if the final finger is lifted...
				cursors.clear();
			}
			if (me.getPointerCount() < cursors.size()) {
				for (int i = 0; i < cursors.size(); i++) {
					int pointerId = me.getPointerId(i);
					int index = me.findPointerIndex(pointerId);
					if (index < 0) {
						cursors.remove(i);
					}
					else if (pointerId != index && i >= 1) {
						cursors.remove(i-1);
					}
				}
			}
		}
	}
	
//	public void touchEvent(MotionEvent me, int i) {		
//		int pointerId = me.getPointerId(i);
//		float x = me.getX(i);
//		float y = me.getY(i);
//		
//		float vx = 0;
//		float vy = 0;
//		
//		int index = me.findPointerIndex(pointerId);
//		
//		
//		if (points.size() < index+1) {
//			//points.ensureCapacity(index+4);
//			points.add(null);
//			points.add(null);
//			points.add(null);
//			points.add(null);
//		}
//		Point prevPoint = points.get(index);
//		long curt = System.currentTimeMillis();
//		if (prevPoint != null && curt-prevPoint.time < 100) {
//			long t = curt-prevPoint.time;
//			float dt = (float)t/1000f;
////			Log.d("asdf","Old point: "+prevPoint.x+" , "+prevPoint.y+" : "+prevPoint.time);
////			Log.d("asdf","Cur point: "+x+" , "+y+" : "+curt);
//			vx = (x-prevPoint.x)*dt;
//			vy = (y-prevPoint.y)*dt;
//		}
//		points.set(index, new Point(x,y));
//		
//		
//
//		float size = me.getSize(i);
//
//		callback.touchEvent(me, i, x, y, vx, vy, size);
//	}
	
	public void touchEvent(MotionEvent me, int i) {		
		int pointerId = me.getPointerId(i);
		float x = me.getX(i);
		float y = me.getY(i);
		
		float vx = 0;
		float vy = 0;
		
		int index = me.findPointerIndex(pointerId);
		
		maybeAddCapacity(cursors,index);
		
		Cursor c = cursors.get(index);
		long ctime = System.currentTimeMillis();
		if (c != null && c.curId == pointerId && ctime - c.currentPoint.time < 100 ) {
			c.updateCursor(new Point(x,y));

		}
		else {
			c = new Cursor(new Point(x,y), pointerId);
		}
		cursors.set(index, c);
		if (me.getAction() == MotionEvent.ACTION_UP) {
			if (me.getPointerId(me.getActionIndex()) == pointerId)
				cursors.remove(index);
		}
		
		vx = c.velX;
		vy = c.velY;
		

		float size = me.getSize(i);

		callback.touchEvent(me, i, x, y, vx, vy, size);
	}

	public void maybeAddCapacity(ArrayList<?> something, int index) {
		if (something.size() < index+1) {
			//points.ensureCapacity(index+4);
			something.add(null);
			something.add(null);
			something.add(null);
			something.add(null);
		}
	}

	
	
	
	
	
}
