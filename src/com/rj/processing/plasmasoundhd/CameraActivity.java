package com.rj.processing.plasmasoundhd;

import java.io.IOException;

import org.json.JSONObject;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.sequencer.CameraPatterns;
import com.rj.processing.plasmasoundhd.sequencer.JSONSequencerPresets;
import com.rj.processing.plasmasoundhd.sequencer.Sequencer;
import com.rj.processing.plasmasoundhd.visuals.AudioStats;

public class CameraActivity extends PlasmaSubFragment implements Camera.PreviewCallback {
	public static String TAG = "Camera";

	public static float SEQUENCER_FADE_SPEED = 0.994f;
	PFont font;
	
	SurfaceView cameraview;
	SurfaceHolder holder;
	RelativeLayout cameraholder;
	static Camera camera;
	int camwidth;
	int camheight;
	PImage gBuffer;
	PImage iBuffer;
	int[] thedata;
	int[] lastframe;
	int[] diff;
	byte[] cambuffer;
	boolean showImage = false;
	float[] sequencerdata = new float[1];
	float[] sequencerdata2 = new float[1];
	

	
	public CameraPatterns sequencer;
	AudioStats stats;

	
	@Override
	int getMenu() { return com.rj.processing.plasmasound.R.menu.motion_menu; }

	
	public CameraActivity() {
		//ewww
	}
	public CameraActivity(PDActivity p) {
		super(p);
	}

	
	@Override
	public void setup() {
		super.setup();
		setupCamera();
		font = p.createFont("americantypewriter.ttf", 28);
	    stats = new AudioStats(p, p); 
		if (sequencer == null) {
			sequencer = new CameraPatterns(p.inst, 16, 10, 120);
			updateSequencer();
			//JSONSequencerPresets.getPresets().loadDefault(p, sequencer);
		}
		p.textFont(font);
		
		p.textMode(PApplet.MODEL);
		sequencer.start();
	}
	

	@Override
	public void destroy() {
		super.destroy();
		destroyCameraUI();
		if (sequencer != null) {
			sequencer.stop();
			sequencer = null;
		}
	}
	
	@Override
	public void background() {
		super.background();
		destroyCameraUI();
		if (sequencer != null) {
			sequencer.stop();
		}
	}
	
	
	@Override
	public void pause() {
		super.onPause();
		destroyCamera();
		if (sequencer != null) sequencer.stop();

	}
	
	@Override
	public void start() {
		super.onStart();
		//setupCamera();
	    if (sequencer != null) sequencer.start();

	}
	
	@Override
	protected void resume() {
		super.onResume();
		setupCamera();
		updateSequencer();
	}
	
	@Override
	public void presetChanged(JSONObject preset) {
	}

	public void clear() {
		sequencer.clear();
	}

	
	
	public void setupCamera() {
		p.runOnUiThread(new Runnable() { public void run() {
			makeUI();
		}});
	}
	public void destroyCamera() {
		p.runOnUiThread(new Runnable() { public void run() {
			destroyCameraUI();
		}});
	}
	public boolean isCameraRunning() {
		return (camera != null); //lazy for now
	}
	
	private void destroyCameraInner() {
		Log.d(TAG, "destroyCameraInner called");
		try {
	        camera.setPreviewCallback(null);
			camera.release();
			camera = null;
			showImage = false;
		} catch (NullPointerException e) {
			e.printStackTrace(); 
			//otherwise we really don't care.
		}
		Log.d(TAG, "destoryCamera done");
		
	}
	private void destroyCameraUI() {
		try {
			destroyCameraInner();
			cameraholder.removeAllViews();
			cameraview = null;
		} catch (NullPointerException e) {
			e.printStackTrace(); 
			//otherwise we really don't care.
		}
		
	}

	
	private void makeUI() {
		Log.d(TAG, "makeUI called");
		if (camera != null) return; //no need to redo this.
		Log.d(TAG, "actually making ui");
		if (cameraholder == null) {
			cameraholder = new RelativeLayout(p);
			p.addContentView(cameraholder, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			//cameraholder.setBackgroundColor(Color.MAGENTA);
		}
		cameraview = new SurfaceView(p);
		cameraholder.addView(cameraview, new LayoutParams(320, 240));
		//cameraview.setBackgroundColor(Color.CYAN);
		holder = cameraview.getHolder();
		holder.addCallback(new Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.d(TAG, "surfaceDestroyed called!");
				destroyCameraInner();
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Log.d(TAG, "surfaceCreated called!");
//				destroyCameraInner();
//				setupCameraInner(holder);
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				Log.d(TAG, "surfaceChanged called!");
				destroyCameraInner();
				setupCameraInner(holder);
				
			}
		});
	}
	private void setupCameraInner(SurfaceHolder holder) {
		Log.d(TAG, "camera starting to be setting up");
		if (isCameraRunning()) return; //wow. all done.
		try {
			Log.d(TAG, "making camera");
			camera = getCameraInstance();
			if (camera == null)  {
				Log.d(TAG, "NOOO! camera is dead!");
				return;
			}
			Log.d(TAG, "Passing holder: "+holder);
			Log.d(TAG, "Passing surface: "+holder.getSurface());
			camera.setPreviewDisplay(holder);
			camera.setPreviewCallbackWithBuffer(this);
			Parameters params = camera.getParameters();
			Size previewsize = getSmallestPreviewSize(params);
			camwidth = previewsize.width;
			camheight = previewsize.height;
			params.setPictureSize(camwidth, camheight);
			params.setPreviewSize(camwidth, camheight);
			if (!params.getSupportedPreviewFormats().contains(ImageFormat.YV12)) {
				//handle it here.
				return;
			}
			params.setPreviewFormat(ImageFormat.YV12);
			//params.setAutoExposureLock(true);
			camera.setParameters(params);
			int buffsize = params.getPreviewSize().width * params.getPreviewSize().height
					* ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
			cambuffer = new byte[buffsize];
			int size = camwidth * camheight;
			thedata = new int[size];
			lastframe = new int[size];
			diff = new int[size];
			gBuffer = p.createImage(camwidth, camheight, PApplet.RGB);
			iBuffer = p.createImage(camwidth, camheight, PApplet.RGB);
			camera.addCallbackBuffer(cambuffer);
			camera.startPreview();
			showImage = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "camera starting is done up");
	}

	
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	    	for (int i=0; i<Camera.getNumberOfCameras(); i++) {
	    		CameraInfo info = new CameraInfo();
	    		Camera.getCameraInfo(i, info);
	    		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
	    	        c = Camera.open(i); // attempt to get a Camera instance
	    	        return c;
	    		}
	    	}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return c;
	}
	
	public static CameraInfo getInfoOfFrontFacingCamera() {
    	for (int i=0; i<Camera.getNumberOfCameras(); i++) {
    		CameraInfo info = new CameraInfo();
    		Camera.getCameraInfo(i, info);
    		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
    	        return info;
    		}
    	}
    	return null;
	}
	
	public static Size getSmallestPreviewSize(Parameters params) {
		int camwidth = 10000;
		int camheight = 10000;
		Size returnsize = null;
		for (Size size : params.getSupportedPreviewSizes()) {
			if (size.height * size.width < camwidth * camheight) {
				returnsize = size;
				camwidth = size.width;
				camheight = size.height;
			}
		}
		return returnsize;
	}

	
	
	// Camera.PreviewCallback stuff:
	// ------------------------------------------------------
	public void onPreviewFrame(byte[] data, Camera cam) {
		//Log.d(TAG, "onPreviewFrame called");
		// Decode our camera byte data into RGB data:
		decodeYUV420SP(data, camwidth*camheight, thedata);
		doProcessing();
		
		gBuffer.loadPixels();
		byteArrayToRGB(diff, camwidth*camheight, gBuffer.pixels, 0xaa000000);
		gBuffer.updatePixels();
		iBuffer.loadPixels();
		byteArrayToRGB(lastframe, camwidth*camheight, iBuffer.pixels, 0x55000000);
		iBuffer.updatePixels();

		// Draw to screen:
		camera.addCallbackBuffer(cambuffer);
	}

	// Byte decoder :
	// ---------------------------------------------------------------------
	void decodeYUV420SP(byte[] yuv420sp, int size, int[] thedata) {
		for (int i=size-1; i>=0; i--) {
			int y = (0xff & ((int) yuv420sp[i])) - 16;
			thedata[i] = y;
		}
	}
	
	// ---------------------------------------------------------------------
	void byteArrayToRGB(int[] gray, int size, int[] rgb, int mask) {
		for (int i=size-1; i>=0; i--) {
				int x = gray[i];
				if (x < 0)
					x = 0;
				if (x > 255) 
					x = 255;
				rgb[i] = mask | ((x << 16) & 0xff0000) | ((x << 8) & 0xff00) | ((x >> 0) & 0xff);
		}
	}
	
	/**
	 * 
	 * ------------
	 * |
	 */
	void doProcessing() {
		float lowcutoff = 0.03f;
		if (sequencer == null || sequencer.grid == null || sequencer.grid[0] == null) return;
		int notewidth = sequencer.grid.length;
		int noteheight = sequencer.grid[0].length;
		if (sequencerdata.length != notewidth * noteheight) {
			sequencerdata = new float[notewidth*noteheight];
			sequencerdata2 = new float[notewidth*noteheight];
		}
		for (int i=sequencer.grid.length-1; i>=0; i-=1) 
			for (int j=sequencer.grid[0].length-1; j>=0; j--)  
				sequencerdata[j*notewidth+i] = sequencer.grid[i][j];
		for (int i=sequencerdata.length-1; i>=0; i--) {
			if (sequencerdata[i] == Sequencer.OFF)
				sequencerdata[i] = 0.00001f;
			sequencerdata[i] *= SEQUENCER_FADE_SPEED - Math.random()*0.000001f;
		}
		for (int i=thedata.length-1; i>=0; i--) {
			diff[i] = (Math.abs(thedata[i] - lastframe[i]) > 50) ? 127 : 0;
			int x = i % camwidth;
			int y = i / camwidth;
			int ii = notewidth - 1 -(x * notewidth) / camwidth;
			int jj = noteheight - 1 -((y * noteheight) / camheight);
			sequencerdata[jj*notewidth+ii] += diff[i]*0.0003f;
			sequencerdata[jj*notewidth+ii] = Math.min(1, sequencerdata[jj*notewidth+ii]);
			lastframe[i] = (lastframe[i]+thedata[i])/2;
		}
		//System.arraycopy(thedata, 0, lastframe, 0, camwidth*camheight);
		nonMax(sequencerdata, sequencerdata2, 0.3f, notewidth, noteheight);
		for (int i=sequencerdata2.length-1; i>=0; i--) {
			if (sequencerdata2[i] < lowcutoff)
				sequencerdata2[i] = Sequencer.OFF;
			sequencer.grid[i%notewidth][i/notewidth]=sequencerdata2[i];
		}
	}
	
	public void nonMax(float[] indata, float[] outdata, float supressAmount, int w, int h) {
		for (int i=indata.length-1; i>=0; i--) {
			int x = i % w;
			int y = i / w;
			float current = indata[(y)*w+x];
			float north = 0, south = 0;
			float southsouth = 0, northnorth = 0;
			north = (y > 0) ? indata[(y-1)*w+x] : 0;
			south = (y < h-1) ? indata[(y+1)*w+x] : 0;
			northnorth = (y > 1) ? indata[(y-2)*w+x] : 0;
			southsouth = (y < h-2) ? indata[(y+2)*w+x] : 0;
			
			boolean result = (current > north && current > south && current > northnorth && current > southsouth);
			outdata[(y)*w+x] = result ? current : current * supressAmount;
		}
	}

	
	private void updateSequencer() {
		if (sequencer != null && p.inst != null) sequencer.setFromSettings(p.inst.motion, true);
	}

	
	
	
	
	@Override
	public void draw() {
		//Log.d(TAG, "draw()");

		p.background(0);
		
		if (showImage) {
//		    p.stroke(0,0);
//			float left = 0;
//			float top = 0;
//			float width = p.width / notewidth;
//			float height = p.height / noteheight;
//			for (int x = 0; x < notewidth; x ++) {
//				for (int y = 0; y < noteheight; y ++) {
//					p.fill(notegrid[y*notewidth+x]/30, 0, 0, 100);
//					p.rect(left + x*width, top + y*height, width, height);
//				}
//			}
			p.pushMatrix();
			p.pushStyle();
			 p.scale(-1.0f, 1.0f);
			 //p.tint(255, 255);
			 
			 p.image(gBuffer, -p.width, 0, p.width, p.height);
			 p.tint(255, 50);
			 p.image(iBuffer, -p.width, 0, p.width, p.height);
			p.popStyle();
			p.popMatrix();			
			if (sequencer == null) return;
			CameraPatterns sequencer = this.sequencer;
			if (! p.pdready) return;
			updateSequencer();
			sequencer.instrument = p.inst;
			//p.resetMatrix();
			p.rectMode(PApplet.CORNER);
			p.ellipseMode(PApplet.CORNER);

			float[][] grid = sequencer.grid;
			float barwidth = p.width/grid.length;
			
			/** draw the names of the notes **/
			float barheight = p.height/grid[0].length;
			p.pushStyle();
			p.textAlign(PApplet.CENTER, PApplet.CENTER);
			for (int i=0; i<grid[0].length; i++) {
				p.fill(100);
				p.noStroke();
				p.textSize(barheight/3.5f);
				p.text(Utils.midiNoteToName((int)(sequencer.getNote(i))), p.width-barwidth/2, p.height-(barheight/2 + barheight*i));
			}
			p.popStyle();

			
			for (int i=0; i<grid.length; i++) {
				
				if (sequencer.currentRow == i) {
					p.fill(50);
					p.noStroke();
					p.rect(i*barwidth, 0, barwidth, p.height);
				}
				
				for (int j=0; j<grid[i].length; j++) {
					if (grid[i][j] == Sequencer.OFF) {
						p.fill(100, 30);
						p.stroke(170);
					}  else {
						p.fill(200,60,60,80);
						p.noStroke();
						p.rect(i*barwidth, (grid[i].length - j - 1)*barheight + (barheight-barheight*grid[i][j]), barwidth, barheight*grid[i][j]);
						p.fill(200,30,30, 50);
						p.stroke(170);
					}
					
					p.rect(i*barwidth, (grid[i].length - j - 1)*barheight, barwidth, barheight);

				}

				
			}
			
			
			stats.drawVis();

			
			
			
		} else {
			p.text("Setting up the camera...", 100, 100);
		}
		
		
		
	}
	
	
	
	
	
	@Override
	public void touchAllUp(final Cursor c) {
	}
	@Override
	public void touchDown(final Cursor c) {
	}

	@Override
	public void touchUp(final Cursor c) {
		Point spot = getSpot(c.currentPoint.x, c.currentPoint.y);
		if (spot == null) return;
		int width = sequencer.grid.length;
		int height = sequencer.grid[0].length;
		if (spot.x >= 0 && spot.x < width && spot.y >= 0 && spot.y < height) {
			float value = sequencer.grid[spot.x][spot.y];
			if (value != Sequencer.OFF) {
				value = Sequencer.OFF;
			} else {
				value = 1.f;
			}
			sequencer.setSpot(spot.x, spot.y, value);
		}
	
	}
	
	private boolean outsideRange(Cursor c) {
		if (com.rj.processing.mt.Point.distanceSquared(c.firstPoint, c.currentPoint) > 50) return true;
		return false;
	}
	
//	private void addToSpot(Point p, float valueDiff) {
//		int x = p.x;
//		int y = p.y;
//		int width = sequencer.grid.length;
//		int height = sequencer.grid[0].length;
//		if (x >= 0 && x < width && y >= 0 && y < height) {
//			float val = sequencer.grid[x][y];
//			val += valueDiff;
//			val = Math.min(1,Math.max(0.001f,val));
//			sequencer.grid[x][y] = val;
//		}
//	}
	
	
	public Point getSpot(float x, float y) {
		int gridx = (int)(x / p.width * sequencer.grid.length);
		if (gridx < sequencer.grid.length && gridx >= 0) {
			int gridy = (int)( (p.height-y) / p.height * sequencer.grid[gridx].length);
			if (gridy < sequencer.grid[gridx].length && gridy >= 0) {
				return new Point(gridx,gridy);
			}
		}
		return null;
	}
	
	
	
	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//	    switch (item.getItemId()) {
//	    case com.rj.processing.plasmasound.R.id.load_sequence_settings:
//	        loadSequenceSettings();
//	        return true;
//	    case com.rj.processing.plasmasound.R.id.save_sequence_settings:
//	        saveSequenceSettings();
//	        return true;
//	    case com.rj.processing.plasmasound.R.id.clear_sequence_settings:
//	        clearSequenceSettings();
//	        return true;
//	    default:
//	        return super.onOptionsItemSelected(item);
//	    }
//	}
//
//	
//	
//	
//	public void saveSequenceSettings() {
//		JSONSequencerPresets.getPresets().showSaveMenu(this.getActivity(), this.sequencer);
//	}
//	public void loadSequenceSettings() {
//		JSONSequencerPresets.getPresets().showLoadMenu(this.getActivity(), this.sequencer);
//	}
//	public void clearSequenceSettings() {
//		clear();
//	}
	
	

}
