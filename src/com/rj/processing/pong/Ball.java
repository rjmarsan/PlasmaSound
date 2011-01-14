package com.rj.processing.pong;

import msafluid.MSAFluidSolver2D;
import processing.core.PApplet;

public class Ball {
	  final static float FLUID_SCALE = 10;

	  final PApplet p;
	  
	  public float x;
	  public float y;
	  
	  final float width;
	  final float height;
	  
	  float vx=0;
	  float vy=0;
	  
	  float maxvel = 0.0f;
	  
	  float yPadding = 20;
	  float upperBoundsY = yPadding;
	  float lowerBoundsY;
	  
	  float scalingFactor = 500;
	  
	  
	  public Ball(PApplet p) {
		  this.p = p;
		  this.x = p.width/2;
		  this.y = p.height/2;
		  this.width = p.width;
		  this.height = p.height;
		  this.lowerBoundsY = height - yPadding;
	  }
	  
	  
	  float noiseScale, noiseVal, fluidvx,fluidvy;
	  int index;
	  public void draw(MSAFluidSolver2D fluidSolver) {
	    p.pushStyle();
	    noiseScale = 0.01f;
	    noiseVal = p.noise(x*noiseScale, y*noiseScale)*255;
	    p.fill(noiseVal,noiseVal,noiseVal,150);
	    p.stroke(0);
	    p.strokeWeight(3);
	    index = fluidSolver.getIndexForNormalizedPosition(x/width,y/height);
	    fluidvy = fluidSolver.v[index]*scalingFactor;
	    fluidvx = fluidSolver.u[index]*scalingFactor;
	    //if (abs(fluidvx + fluidvy) < 3) {
	    //  fluidvscale = 100;  //now it flies a lot better when you push it
	    //}
	    

	    //fluidvscale = 100/abs(fluidvx + fluidvy+0.001); 
	    vy = (fluidvy)/FLUID_SCALE+(FLUID_SCALE-1)*vy/FLUID_SCALE;
	    vx = (fluidvx)/FLUID_SCALE+(FLUID_SCALE-1)*vx/FLUID_SCALE;
	    if (vx > 200) vx = 200;
	    if (vy > 200) vy = 200;
	    p.ellipse(x,y,30,30);
	    p.popStyle();
	    
	    x = x+vx;
	    y = y+vy;
	    checkBounds();
	  }
	  public void checkBounds() {
	    if (x < 0) {
	      x=0;
	      vx = -vx;
	    }
	    else if (x > width) {
	      x=width;
	      vx = -vx;
	    }
	    if (y < upperBoundsY) {
	      y=upperBoundsY;
	      vy = -vy;
	    }
	    else if (y > lowerBoundsY) {
	      y=lowerBoundsY;
	      vy = -vy;
	    }

	  }
	  
	  
	  public void resetBall() {
	    x = width/2;
	    y = height/2;
	    vx = 0;
	    vy = 0;
	    scalingFactor = 500;
	  }

}
