package com.rj.processing.pong;

import processing.core.PFont;

import com.rj.processing.MSAPong;

import processing.core.PFont;

public class Game {
	final MSAPong p;
	final float width;
	final float height;

	Ball b;
	PFont font;

	int goalBoarder = 70;
	int goalColor;

	int scoreP1 = 0;
	int scoreP2 = 0;

	int maxScore = 5;
	int waitPeriod = 60; // number of frames to keep the status message

	int justScored = 0; // the frame it happened
	int lastScored = 0; // the player who last scored
	int gameOver = 0; // the frame it happened
	int initWait = 0; // the frame it happened

	int eventFrame = -1;
	String statusMessage;

	public Game(MSAPong p) {
		this.p = p;
		this.width = p.width;
		this.height = p.height;

		goalColor = p.color(150, 150, 150, 150);

	}

	public void initPong() {
		// textMode(SHAPE);
		p.textMode(p.MODEL);
		b = new Ball(p);
		// load font
		font = p.loadFont("GillSans-Bold-48.vlw");
		// font = loadFont("SansSerif-48.vlw");
		p.textFont(font, 48);
		p.textAlign(p.CENTER);
		p.rectMode(p.CENTER);
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
		p.pushStyle();
		p.colorMode(p.RGB, 255, 255, 255, 255);
		p.fill(150, 150, 150, 150);

		b.draw(p.fluidSolver);
		drawStatusMessage();
		drawScore();
		drawGoals();
		updateGameLogic();
		p.popStyle();
	}

	public void setStatus(String s) {
		p.println("New status at frame " + p.frameCount + ": " + s);
		eventFrame = p.frameCount;
		statusMessage = s;
	}

	public void updateGameLogic() {
		if (justScored == 0) {
			if (b.x < goalBoarder) {
				justScored = p.frameCount;
				scoreP2 += 1;
				lastScored = 1;
				if (scoreP2 < maxScore) {
					setStatus("Player " + lastScored + " Scores!");
				} else {
					setStatus("Player " + lastScored + " wins!");
					gameOver = p.frameCount;
				}
			} else if (b.x > width - goalBoarder) {
				justScored = p.frameCount;
				scoreP1 += 1;
				lastScored = 2;
				if (scoreP1 < maxScore) {
					setStatus("Player " + lastScored + " Scores!");
				} else {
					setStatus("Player " + lastScored + " wins!");
					gameOver = p.frameCount;
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
		// b.scalingFactor += 2;
		// fluidSolver.setVisc(fluidSolver.getVisc()/1.003f);
		// println(fluidSolver.getVisc());
	}

	public void drawStatusMessage() {
		if (eventFrame != -1) {
			if (p.frameCount - eventFrame < waitPeriod) {
				drawStatusText(statusMessage);
				drawCountdownBar(eventFrame);
			} else {
				eventFrame = -1;
			}
		}
	}

	public void gameOver() {
		if (p.frameCount - gameOver > waitPeriod) {
			initGameLogic();
		}
	}

	public void resetPuck() {
		if (justScored != 0 && p.frameCount - justScored > waitPeriod) {
			b.resetBall();
			justScored = 0;
			resetFluid();
		}

	}

	public void resetFluid() {
		p.setupFluid();
		p.fluidSolver.reset();
	}

	public void drawScore() {
		p.stroke(0);
		p.text(scoreP1, 100, 100);
		p.text(scoreP2, width - 100, 100);
	}

	public void drawStatusText(String s) {
		p.text(s, width / 2, height / 2);
	}

	public void drawCountdownBar(int frameSince) {
		p.rect(width / 2, height / 2 + 20,
				3 * (waitPeriod - (p.frameCount - frameSince)), 10);
	}

	public void drawGoals() {
		// for now
		p.stroke(goalColor);
		p.strokeWeight(3);
		p.line(goalBoarder, goalBoarder, goalBoarder, height - goalBoarder);
		p.line(width - goalBoarder, goalBoarder, width - goalBoarder, height
				- goalBoarder);
	}

}
