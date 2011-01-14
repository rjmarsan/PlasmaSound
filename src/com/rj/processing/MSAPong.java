package com.rj.processing;

import msafluid.MSAFluidSolver2D;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.rj.processing.mt.MTCallback;
import com.rj.processing.mt.MTManager;
import com.rj.processing.pong.Ball;
import com.rj.processing.pong.Game;

public class MSAPong extends PApplet implements MTCallback {

/***********************************************************************
 * 
 * Demo of the MSAFluid library (www.memo.tv/msafluid_for_processing) controlled by TUIO
 * Move mouse to add dye and forces to the fluid.
 * Alternatively use a TUIO tracker/server to control remotely (www.tuio.org)
 * 
 * Click mouse to turn off fluid rendering seeing only particles and their paths.
 * Demonstrates feeding input into the fluid and reading data back (to update the particles).
 * Also demonstrates using Vertex Arrays for particle rendering.
 * 
/***********************************************************************
 
 Copyright (c) 2008, 2009, Memo Akten, www.memo.tv
 *** The Mega Super Awesome Visuals Company ***
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of MSA Visuals nor the names of its contributors 
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS 
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 *
 * ***********************************************************************/ 






final float FLUID_WIDTH = 60;

float invWidth, invHeight;    // inverse of screen dimensions
float aspectRatio, aspectRatio2;

public MSAFluidSolver2D fluidSolver;

PImage imgFluid;

MTManager mtManager;

boolean evenframe=true;
//boolean drawFluid = true;

Game g;

public void setup() {
       // use OPENGL rendering for bilinear filtering on texture
    //size(screen.width * 49/50, screen.height * 49/50, OPENGL);
    //hint( ENABLE_OPENGL_4X_SMOOTH );    // Turn on 4X antialiasing
	  hint(DISABLE_DEPTH_TEST);
	  hint(DISABLE_OPENGL_ERROR_REPORT);
    frameRate(60);

    invWidth = 1.0f/width;
    invHeight = 1.0f/height;
    aspectRatio = width * invHeight;
    aspectRatio2 = aspectRatio * aspectRatio;

    // create fluid and set options
    fluidSolver = new MSAFluidSolver2D((int)(FLUID_WIDTH), (int)(FLUID_WIDTH * height/width));
    setupFluid();

    // create image to hold fluid picture
    imgFluid = createImage(fluidSolver.getWidth(), fluidSolver.getHeight(), RGB);
    
    
    mtManager = new MTManager(this);
    
    //GAME CODE
    g = new Game(this);
    initPong(); 
    
    debug();
}

public void setupFluid() {
	  fluidSolver.enableRGB(true).setFadeSpeed(0.01f).setDeltaT(0.5f).setVisc(0.0001f).setSolverIterations(3);
	  //fluidSolver.enableRGB(true).setFadeSpeed(0.01f).setDeltaT(1).setVisc(1).setSolverIterations(5);
}

public void debug() {
	  // Place this inside your setup() method
	  DisplayMetrics dm = new DisplayMetrics();
	  getWindowManager().getDefaultDisplay().getMetrics(dm);
	  float density = dm.density; 
	  int densityDpi = dm.densityDpi;
	  println("density is " + density); 
	  println("densityDpi is " + densityDpi);
	  
	  println("HEY! the screen size is "+width+"x"+height);
}


public void mouseMoved() {
    float mouseNormX = mouseX * invWidth;
    float mouseNormY = mouseY * invHeight;
    float mouseVelX = (mouseX - pmouseX) * invWidth;
    float mouseVelY = (mouseY - pmouseY) * invHeight;

    addForce(mouseNormX, mouseNormY, mouseVelX, mouseVelY);
}
//mt version
public boolean surfaceTouchEvent(MotionEvent me) {
	if (mtManager != null) mtManager.surfaceTouchEvent(me);
	
	return super.surfaceTouchEvent(me);

}

public void touchEvent(MotionEvent me, int i, float x, float y, float vx,
		float vy, float size) {
	//if (!ready) return;
	
	float velocityScale = 30f;
	float maxVel = 0.2f;
//	float velocityScale = 30f;
//	float maxVel = 0.2f;
	
	println(""+vx+","+vy);

//	if (Math.abs(vx) > maxVel) {
//		float diff = maxVel / Math.abs(vx);
//		vx = vx * diff;
//		vy = vy * diff;
//	}
//	if (Math.abs(vy) > maxVel) {
//		float diff = maxVel / Math.abs(vy);
//		vx = vx * diff;
//		vy = vy * diff;
//	}
//	
	vx = vx * velocityScale;
	vy = vy * velocityScale;
	
	float siz = me.getSize(i);
    addForce(x/width, y/height, vx/width, vy/height);
	this.x=x;
	this.y=y;
	this.x2=x+vx*100;
	this.y2=y+vy*100;
}


float x,y,x2,y2;

public void draw() {
    background(0);

    colorMode(RGB, 1);  

    fluidSolver.update();

    imgFluid.loadPixels();
    int d = 2;
    for(int i=0; i<fluidSolver.getNumCells(); i++) { //optimize here.
        imgFluid.pixels[i] = color(fluidSolver.r[i] * d, fluidSolver.g[i] * d, fluidSolver.b[i] * d);
    }  
    imgFluid.updatePixels();//  fastblur(imgFluid, 2);
    
    image(imgFluid, 0, 0, width, height);

    colorMode(RGB, 255);  

    drawPong();
    //*/
    
    //stroke(128);
    //this.line(x, y, x2,y2);
    
    if (this.frameCount % 10 == 0) println(this.frameRate+"");

}


// add force and dye to fluid, and create particles
public void addForce(float x, float y, float dx, float dy) {
        float colorMult = 5;
        colorMult=colorMult*y;
        float velocityMult = 30.0f;

        if (dx > 1) dx = 1;
        if (dy > 5) dy = 1;

        int drawColor;

        colorMode(HSB, 360, 1, 1);
        float hue = ((x + y) * 180 + frameCount) % 360;
        if (x < 0.5f)
        	hue = 0;
        else
        	hue = 180;
        drawColor = color(hue, 1, 1);
        colorMode(RGB, 1);  
        for (int i=0; i<3; i++) {
        	for (int j=0; j<1; j++) {
	        	int index = fluidSolver.getIndexForNormalizedPosition(x+.01f*i, y+.01f*j);
		        fluidSolver.rOld[index]  += red(drawColor) * colorMult;
		        fluidSolver.gOld[index]  += green(drawColor) * colorMult;
		        fluidSolver.bOld[index]  += blue(drawColor) * colorMult;
		
		        fluidSolver.uOld[index] += dx * velocityMult;
		        fluidSolver.vOld[index] += dy * velocityMult;
        	}
        }
        println("Forces being written: x:"+dx * velocityMult);
        println("Forces being written: y:"+dy * velocityMult);
        

}



	public void initPong() {
		g.initPong();
	}

	public void drawPong() {
		g.drawPong();
	}

  public int sketchWidth() { return this.screenWidth; }
  public int sketchHeight() { return this.screenHeight; }
  public String sketchRenderer() { return this.OPENGL; }
}
