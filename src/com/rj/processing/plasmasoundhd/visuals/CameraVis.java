package com.rj.processing.plasmasoundhd.visuals;

import java.io.IOException;

import processing.core.PApplet;
import processing.core.PImage;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.rj.processing.plasmasoundhd.PlasmaActivity;

public class CameraVis extends Visual implements Camera.PreviewCallback {
	public final String TAG = "CameraViz "+this;
	PlasmaActivity pp;
	SurfaceView cameraview;
	SurfaceHolder holder;
	RelativeLayout cameraholder;
	static Camera camera;
	PImage gBuffer;
	int[] thedata;
	int[] lastframe;
	int[] diff;
	byte[] cambuffer;
	boolean showImage = false;
	boolean firstFrame = false;

	
	
	public CameraVis(final PApplet p, PlasmaActivity pp) {
		super(p);
		this.pp = pp;

//		p.runOnUiThread(new Runnable() { public void run() {
//			makeUI();
//		}});
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
		try {
	        camera.setPreviewCallback(null);
			camera.release();
			camera = null;
			showImage = false;
		} catch (NullPointerException e) {
			e.printStackTrace(); 
			//otherwise we really don't care.
		}
		
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

	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(1); // attempt to get a Camera instance
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return c;
	}

	
	private void makeUI() {
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
		if (camera != null) return; //wow. all done.
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
			params.setPictureSize(320, 240);
			params.setPreviewSize(320, 240);
			params.setPreviewFormat(ImageFormat.YV12);
			//params.setAutoExposureLock(true);
			camera.setParameters(params);
			int buffsize = params.getPreviewSize().width * params.getPreviewSize().height
					* ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
			cambuffer = new byte[buffsize];
			thedata = new int[320*240];
			lastframe = new int[320*240];
			diff = new int[320*240];
			gBuffer = p.createImage(320, 240, PApplet.RGB);
			camera.addCallbackBuffer(cambuffer);
			camera.startPreview();
			firstFrame = true;
			showImage = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "camera starting is done up");
	}

	
	
	
	// Camera.PreviewCallback stuff:
	// ------------------------------------------------------
	public void onPreviewFrame(byte[] data, Camera cam) {
		//Log.d(TAG, "onPreviewFrame called");
		// This is called every frame of the preview. Update our global PImage.
		gBuffer.loadPixels();
		// Decode our camera byte data into RGB data:
		decodeYUV420SP(data, 320*240, thedata);
		doProcessing();
		byteArrayToRGB(diff, 320*240, gBuffer.pixels);
		gBuffer.updatePixels();
		// Draw to screen:
		camera.addCallbackBuffer(cambuffer);
	}

	// Byte decoder :
	// ---------------------------------------------------------------------
	void decodeYUV420SP(byte[] yuv420sp, int size, int[] thedata) {
		for (int i=size-1; i>=0; i--) {
			int y = (0xff & ((int) yuv420sp[i])) - 16;
			thedata[i] = (byte) y;
		}
	}
	
	// ---------------------------------------------------------------------
	void byteArrayToRGB(int[] gray, int size, int[] rgb) {
		for (int i=size-1; i>=0; i--) {
				int x = gray[i];
				if (x < 0)
					x = 0;
				if (x > 255) 
					x = 255;
				rgb[i] = 0x99000000 | ((x << 16) & 0xff0000) | ((x << 8) & 0xff00) | ((x >> 0) & 0xff);
		}
	}
	
	
	void doProcessing() {
		int size = 320*240;
		for (int i=size-1; i>=0; i--) {
			diff[i] = (Math.abs(thedata[i] - lastframe[i]) > 30) ? 127 : 0;
		}
		if (firstFrame) {
			System.arraycopy(thedata, 0, lastframe, 0, 320*240);
			firstFrame = false;
		}
	}




	@Override
	public void drawVis() {
		if (showImage) {
		    p.image(gBuffer, p.width - gBuffer.width - 20, p.height - gBuffer.height - 20);
		}
	}

	@Override
	public void touchEvent(MotionEvent me, int i, float x, float y, float vx,
			float vy, float size) {
		firstFrame = true; //hack to zero it out
		
	}

}
