/*
 * Author: APPLH.COM
 * Creation: 31/08/12
 * Modif:
 * 
 */


package com.applh.lightbike;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.SystemClock;

import com.applh.lightbike.Game.LightBikeGame;
import com.applh.lightbike.Game.SoundManager;
import com.applh.lightbike.fx.ByteBufferManager;
import com.applh.lightbike.fx.SetupGL;

public class OpenGLRenderer implements GLSurfaceView.Renderer {

	private LightBikeGame aGame = null;
	private Context aContext = null;
	
//	private OpenGLView aView = null;
	
	private int aScreenW = 512;
	private int aScreenH = 512;
	
	private SetupGL aSetupGL = null;
	
	private boolean aIsGLInit = false;
	private boolean aDoneSplash = false;
	private boolean aIsGameInit = false;
	private boolean aIsGamePause = false;
	
	// LH HACK
	private long aNbFrame = 0;
	private long aTimeStart = 0;
	private long aTimeNow = 0;
	
	private long aTime0 = aTimeNow;
	private long aNbFrame0 = aNbFrame;
	private long aDeltaPeriod = 1000;
	private long aCurFPS = 0;
	
	public static long LastCountFB = 0;
	
	public OpenGLRenderer (OpenGLView view, Context context, int screenW, int screenH) {
		// Store theses values for later
		aContext = context;
		aScreenH = screenH;
		aScreenW = screenW;
	}

	public void onTouch (float x, float y) {
		if (aGame != null) 
			aGame.sendTouchEvent(x, y);
	}

	public void onMove (float x, float y) {
		if (aGame != null) 
			aGame.sendMoveEvent(x, y);
	}

	public void onPause () {
		aIsGamePause = true;
		if (aGame != null) 
			aGame.pauseGame();
		
	}
	
	public void onResume () {
		aIsGamePause = false;
		if (aGame != null) 
			aGame.resumeGame();
	    resetTime();
	}

	/*

	public void onStop () {
		// release some resources
	    SoundManager.CleanUp1();

		aIsGameInit = false;
		aDoneSplash = false;
		aIsGLInit = false;
		}
*/
	public void onDestroy () {
		// release some resources
        SoundManager.CleanUp();

        aIsGameInit = false;
		aDoneSplash = false;
		aIsGLInit = false;
	}

	public void onSurfaceCreated (GL10 gl, EGLConfig config) {
		// CAN BE CALLED SEVERAL TIMES ?
		// e.g. OUT/IN AFTER PREFERENCES ?
		// BUILD GL
		aIsGLInit = initGameGL();
	}

	public boolean initGameGL () {
	    aNbFrame = 0;
	    resetTime();
	    	    
	    // CREATE THE SETUP GL
	    aSetupGL = new SetupGL(aContext, aScreenW, aScreenH);
	    long freeM = getFreeMb();
	    aSetupGL.init(freeM);
	    
	    aSetupGL.myInitSurface();
	    
		// CREATE THE GAME 
		createGame();
		
		return true;
	}
	
	@Override
	public void onSurfaceChanged (GL10 gl, int w, int h) {
		aScreenH = h;
		aScreenW = w;
		if (aGame != null)
			aGame.updateScreenSize(aScreenW, aScreenH);
	}
	
	@Override
	public void onDrawFrame (GL10 gl) {
		// CALLED TO DRAW EACH FRAME ...50x per second...

		if (aIsGamePause) {
			aSetupGL.clearScreen();
			return;
		}
		
		// reset FB count
		LastCountFB = ByteBufferManager.ResetFrameFB();
		
		// update time management
		updateFPS();
		
		if (aIsGameInit) {
			// LOOP CALLBACK
			aGame.RunGameFrame();
		}
		else if (aDoneSplash) {
		    // setup the Game
		    aSetupGL.myInitGame();
			aIsGameInit = aGame.initGame();
		}
		else if (aIsGLInit) {			
			// splashscreen
		    aSetupGL.myInitSplash();
	    	aDoneSplash = aSetupGL.drawSplash();
		}
		else {
			// BUILD GL
			aIsGLInit = initGameGL();
		}
	}

	
	public void createGame () {
		// CREATE THE GAME
		if (aGame == null)
			aGame = new LightBikeGame(this, aContext);
		
		aGame.updateScreenSize(aScreenW, aScreenH);
		
	}

	// reset time before play
	public long resetTime () {
		aTimeStart = SystemClock.uptimeMillis();
		aTimeNow = aTimeStart;
		aNbFrame = 0;
		aNbFrame0 = 0;
		
		return aTimeNow;
		
	}
	// return play time in milliseconds
	public long getPlayTime () {
		long res = aTimeNow - aTimeStart;
		return res;
	}
	
	public long getFPS () {
		long deltaFrame = aNbFrame - aNbFrame0;
		long deltaTime = aTimeNow - aTime0;
		if (deltaTime > aDeltaPeriod) {
			aCurFPS = (deltaFrame * 1000 ) / deltaTime;
			// reinit period
			aTime0 = aTimeNow;
			aNbFrame0 = aNbFrame;
		}		
		return aCurFPS;
	}
	
	public void updateFPS () {
		// update time
		aNbFrame++;
		aTimeNow = SystemClock.uptimeMillis();

		/*
		// LH HACK TO SMOOTH GAME PLAY
		long time1 = SystemClock.uptimeMillis();
		long deltaT = time1 - aTimeNow;
		if (deltaT > 100) {
			//aTimeNow += 20; // constant time as 20ms (=> 50fps)
			//aTimeNow += 40; // constant time as 40ms (=> 25fps)			
			//aTimeNow += 50; // constant time as 50ms (=> 20fps)			
			aTimeNow += 100; // constant time as 100ms (=> 10fps)			
		}
		else {
			aTimeNow = time1;						
		}
		*/
		
		long oldFPS = aCurFPS;
		long newFPS = getFPS();
		if (oldFPS != newFPS) {
			long avg = 0;
			float avgF = 0.0f;
			long playT = getPlayTime();
			if (playT > 0) {
				avgF = (1000.0f * aNbFrame) / playT;
				avg = Math.round(avgF);
			}
			aGame.setFPS(newFPS, avg);
		}
	}
	
	public long getFreeMb () {
		long res = 0;
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) aContext.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		res = mi.availMem / 1048576L; // MB
		return res;
	}

}
