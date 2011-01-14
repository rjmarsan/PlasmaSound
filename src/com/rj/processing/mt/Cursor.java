package com.rj.processing.mt;


public class Cursor {
	public final Point firstPoint;
	public Point currentPoint;
	public final int curId;
	
	public float velX=0;
	public float velY=0;
	
	public static final float VEL_AVG = 3;
	
	public Cursor(Point p, int curId) {
		firstPoint = p;
		currentPoint = p;
		this.curId = curId;
	}
	
	public void updateCursor(Point p) {
		//something about velocity
		
		long t = p.time-currentPoint.time;
		float dt = (float)t/1000f;

		
		float rvx = (p.x - currentPoint.x) * dt;
		float rvy = (p.y - currentPoint.y) * dt;
		
		if (velX*velX+velY*velY > 0.001f) {
			velX = (velX*(VEL_AVG-1) + rvx) / VEL_AVG;
			velY = (velY*(VEL_AVG-1) + rvy) / VEL_AVG;
		}
		else {
			velX = rvx;
			velY = rvy;
		}
		
		currentPoint = p;
	}
}
