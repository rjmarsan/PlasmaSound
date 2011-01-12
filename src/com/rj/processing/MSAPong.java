package com.rj.processing;

import com.rj.processing.mt.MTCallback;
import com.rj.processing.mt.MTManager;

import msafluid.MSAFluidSolver2D;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

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






final float FLUID_WIDTH = 80;

float invWidth, invHeight;    // inverse of screen dimensions
float aspectRatio, aspectRatio2;

MSAFluidSolver2D fluidSolver;

PImage imgFluid;

MTManager mtManager;


//boolean drawFluid = true;

public void setup() {
       // use OPENGL rendering for bilinear filtering on texture
    //size(screen.width * 49/50, screen.height * 49/50, OPENGL);
    //hint( ENABLE_OPENGL_4X_SMOOTH );    // Turn on 4X antialiasing
	  hint(DISABLE_DEPTH_TEST);
	  hint(DISABLE_OPENGL_ERROR_REPORT);
    frameRate(30);

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
    
    //BALL
    initPong(); 
    
    debug();
}

public void setupFluid() {
	  fluidSolver.enableRGB(true).setFadeSpeed(0.01f).setDeltaT(0.5f).setVisc(0.0001f).setSolverIterations(2);
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

    addForce(mouseNormX, mouseNormY, mouseVelX, mouseVelY, (long)0);
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
    addForce(x/width, y/height, vx/width, vy/height, (long)random(8));
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
    for(int i=0; i<fluidSolver.getNumCells(); i++) {
        imgFluid.pixels[i] = color(fluidSolver.r[i] * d, fluidSolver.g[i] * d, fluidSolver.b[i] * d);
    }  
    imgFluid.updatePixels();//  fastblur(imgFluid, 2);
    
    image(imgFluid, 0, 0, width, height);

    colorMode(RGB, 255);  

    drawPong();
    //*/
    
    stroke(128);
    this.line(x, y, x2,y2);

}


// add force and dye to fluid, and create particles
public void addForce(float x, float y, float dx, float dy, long sesId) {
//        if(x<0) x = 0; 
//        else if(x>1) x = 1;
//        if(y<0) y = 0; 
//        else if(y>1) y = 1;

        float colorMult = 5;
        colorMult=colorMult*y;
        float velocityMult = 30.0f;

        int index = fluidSolver.getIndexForNormalizedPosition(x, y);

        int drawColor;

        colorMode(HSB, 360, 1, 1);
        float hue = ((x + y) * 180 + frameCount) % 360;
        if (x < 0.5f)
        	hue = 0;
        else
        	hue = 180;
        //float hue = (sesId * 80+random(10)) % 360;
        drawColor = color(hue, 1, 1);
        colorMode(RGB, 1);  

        fluidSolver.rOld[index]  += red(drawColor) * colorMult;
        fluidSolver.gOld[index]  += green(drawColor) * colorMult;
        fluidSolver.bOld[index]  += blue(drawColor) * colorMult;

        fluidSolver.uOld[index] += dx * velocityMult;
        fluidSolver.vOld[index] += dy * velocityMult;
        println("Forces being written: x:"+dx * velocityMult);
        println("Forces being written: y:"+dy * velocityMult);
        

}



class Ball{
  float x=width/2;
  float y=height/2;
  
  float vx=0;
  float vy=0;
  
  
  float yPadding = 20;
  float upperBoundsY = yPadding;
  float lowerBoundsY = height - yPadding;
  
  float scalingFactor = 500;
  
  
  public void draw(MSAFluidSolver2D fluidSolver) {
    pushStyle();
    fill(150,100,150,150);
    stroke(0);
    strokeWeight(3);
    int index = fluidSolver.getIndexForNormalizedPosition(x/width,y/height);
    float fluidvy = fluidSolver.v[index]*scalingFactor;
    float fluidvx = fluidSolver.u[index]*scalingFactor;
    
    float fluidvscale = 10;
    if (abs(fluidvx + fluidvy) < 3) {
      fluidvscale = 100;  //now it flys a lot better when you push it
    }
    
    //fluidvscale = 100/abs(fluidvx + fluidvy+0.001); 
    vy = (fluidvy)/fluidvscale+(fluidvscale-1)*vy/fluidvscale;
    vx = (fluidvx)/fluidvscale+(fluidvscale-1)*vx/fluidvscale;
      
    ellipse(x,y,30,30);
    popStyle();
    
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
Ball b;
PFont font;

int goalBoarder = 70;
int goalColor = color(150,150,150,150);


int scoreP1 = 0;
int scoreP2 = 0;

int maxScore = 5;
int waitPeriod = 60; //number of frames to keep the status message

int justScored = 0;  //the frame it happened
int lastScored = 0;  //the player who last scored
int gameOver = 0;    //the frame it happened
int initWait = 0;    //the frame it happened

int eventFrame = -1;
String statusMessage;

public void initPong() {
  //textMode(SHAPE);
  textMode(MODEL);
  b = new Ball();
  //load font
  font = loadFont("GillSans-Bold-48.vlw"); 
  //font = loadFont("SansSerif-48.vlw"); 
  textFont(font, 48);
  textAlign(CENTER);
  rectMode(CENTER);
  initGameLogic();
}

public void initGameLogic() {
  scoreP1 = 0;
  scoreP2 = 0;
  
  justScored = 0;
  lastScored = 0;
  gameOver = 0;
  initWait = 0;
 
  
 
 
  
  setStatus("Ready? Go!");
}

public void drawPong() {
  pushStyle();
  colorMode(RGB, 255,255,255,255);
  fill(150,150,150,150);
    
  b.draw(fluidSolver);
  drawStatusMessage();
  drawScore();
  drawGoals();
  updateGameLogic();
  popStyle();
}

public void setStatus(String s) {
  println("New status at frame "+frameCount+": "+s);
  eventFrame = frameCount;
  statusMessage = s;
}

public void updateGameLogic() {
  if (justScored == 0) {
    if (b.x < goalBoarder) {
      justScored = frameCount;
      scoreP2 += 1;
      lastScored = 1;
      if (scoreP2 < maxScore) {
        setStatus("Player "+lastScored+" Scores!");
      }
      else {
        setStatus("Player "+lastScored+" wins!");
        gameOver = frameCount;
      }
    }
    else if (b.x > width-goalBoarder) {
      justScored = frameCount;
      scoreP1 += 1;
      lastScored = 2;
      if (scoreP1 < maxScore) {
        setStatus("Player "+lastScored+" Scores!");
      }
      else {
        setStatus("Player "+lastScored+" wins!");
        gameOver = frameCount;
      }
      
    }
    
  }
  makeGameHarder();
  resetPuck();
  if (gameOver != 0) {
    gameOver();
  }
}

public void makeGameHarder() {
  //b.scalingFactor += 2;
  //fluidSolver.setVisc(fluidSolver.getVisc()/1.003f);
  //println(fluidSolver.getVisc());
}

public void drawStatusMessage() {
  if (eventFrame != -1) {
    if (frameCount - eventFrame < waitPeriod) {
      drawStatusText(statusMessage);
      drawCountdownBar(eventFrame);
    }
    else {
      eventFrame = -1;
    }
  }
}

public void gameOver() {
  if (frameCount - gameOver > waitPeriod) {
    initGameLogic();
  }
}

public void resetPuck() {
  if (justScored != 0 && frameCount - justScored > waitPeriod) {
    b.resetBall();
    justScored = 0;
    resetFluid();
  }
  
}

public void resetFluid() {
  setupFluid();
  fluidSolver.reset();
}

public void drawScore() {
  stroke(0);
  text(scoreP1, 100,100);
  text(scoreP2, width-100,100);
}

public void drawStatusText(String s) {
  text(s,width/2, height/2);
}
public void drawCountdownBar( int frameSince ) {
    rect(width/2,height/2+20,3*(waitPeriod - (frameCount - frameSince)), 10);
}
 
 
public void drawGoals() {
  //for now
  stroke(goalColor);
  strokeWeight(3);
  line(goalBoarder,goalBoarder,goalBoarder,height-goalBoarder);
  line(width-goalBoarder,goalBoarder,width-goalBoarder,height-goalBoarder);
}

  public int sketchWidth() { return this.screenWidth; }
  public int sketchHeight() { return this.screenHeight; }
  public String sketchRenderer() { return this.OPENGL; }
}
