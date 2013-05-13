/*
 * Copyright © 2012 Iain Churcher
 *
 * Based on GLtron by Andreas Umbach (www.gltron.org)
 *
 * This file is part of GL TRON.
 *
 * GL TRON is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GL TRON is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GL TRON.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.applh.lightbike.Game;

import android.opengl.GLES11;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import com.applh.lightbike.R;

import com.applh.lightbike.Game.Camera.CamType;
//import com.applh.lightbike.Sound.SoundManager;
import com.applh.lightbike.Video.*;
import com.applh.lightbike.Video.Lighting.LightType;
import com.applh.lightbike.fx.Explosion;
import com.applh.lightbike.fx.SetupGL;
import com.applh.lightbike.fx.TrackRenderer;
import com.applh.lightbike.matrix.Vector3;
import com.applh.lightbike.OpenGLRenderer;


public class LightBikeGame {
	
	private long aTimeNow = 0;
	private long aFrameT0 = 0;
	private long aFrameDT = 0;
	private long aTimeStop = 0;
	
	// Define arena setting 
	private static float aGrideSize0 = 480.0f;
	private static float aSpeed0 = 10.0f;
	private static int aColor0 = 2;

	public static final int MAX_PLAYERS = 24;
	public static final int OWN_PLAYER = 0;
	public static int aNbPlayers0 = 0;
	public static int aCurrentBikes = 0;
		
	private WorldGraphics aWorld = null;
	private int aScreenH = 0;
	private int aScreenW = 0;
	
	private TrackManager aTrackManager;
	private Player aPlayers[] = null;
	private long aScores[] = null;
	
	// Camera data
	private Camera aCam = null;
	private CamType aCamType0 = Camera.CamType.E_CAM_TYPE_FOLLOW_CLOSE;
	
	private TrackRenderer aTrackRenderer = null;
	
	// input processing FIXME make enum type instead of bunch of flags
	boolean aIsProcessInput = false;
	boolean aIsGameReset = false;
	int inputDirection = 0;
	
	private boolean aIsHome = true;
	private boolean aIsReady = false;
//	private boolean aIsPause = false;
	
	// sound index
	public static int CRASH_SOUND = 1;
	public static int ENGINE_SOUND = 2;
	public static int MUSIC_SOUND = 3;
		
	// USER Info Display
	private static HUD HUD = null;
	private static Model BikeModel = null;
		
	
	public Segment aWalls[] = null;
	
	private int aiCount = 1;
	

	// LH HACK TO IMPROVE
	public Video aVisual = null;
	public boolean showInfoFPS = true;
	// Preferences
	private static UserPrefs aPrefs = null;	
	private long curFPS = 0; 
	private long minFPS = 60; 
	private long maxFPS = 0; 
	private long avgFPS = 0; 
	private boolean isPlayActive = false;
	private long curPlayTime = 0;
	
	private OpenGLRenderer aCustomRenderer = null;
	private Context aContext = null;
			
	public LightBikeGame (OpenGLRenderer rgl, Context c)
	{
		aCustomRenderer = rgl;
		aContext = c;

		aTimeNow = 0;
		aFrameT0 = 0;
		aFrameDT = 0;
		aTimeStop = 0;
		
		// Define arena setting 
		aGrideSize0 = 480.0f;
		aSpeed0 = 10.0f;
		aColor0 = 2;

		aNbPlayers0 = 0;
		aCurrentBikes = 0;
		aTrackManager = null;
		
		aWorld = null;
		aScreenH = 0;
		aScreenW = 0;
		
		aPlayers = null;
		aScores = null;
		
		// Camera data
		aCam = null;
		aCamType0 = Camera.CamType.E_CAM_TYPE_FOLLOW_CLOSE;
		
		aTrackRenderer = null;
		
		// input processing FIXME make enum type instead of bunch of flags
		aIsProcessInput = false;
		aIsGameReset = false;
		inputDirection = 0;
		
		aIsHome = true;
		aIsReady = false;
//		aIsPause = false;
					
		// USER Info Display
		HUD = null;
		BikeModel = null;			
		
		aWalls = null;
		
		aiCount = 1;
		
		// LH HACK TO IMPROVE
		aVisual = null;
		showInfoFPS = true;
		// Preferences
		aPrefs = null;	
		curFPS = 0; 
		minFPS = 60; 
		maxFPS = 0; 
		avgFPS = 0; 
		isPlayActive = false;
		curPlayTime = 0;
	}

	public static HUD GetHUD (int i) {
		return HUD;
	}
	
	public static Model GetBike (int i) {
		return BikeModel;
	}
	
	public boolean initGame2 () {
		boolean res = true;

		// Explosion
		Explosion.ReInit0();
		
		// Load HUD
	    HUD = new HUD(this, aContext);
		HUD.displayInstr(true);

		// DEFAULT VALUES
		aNbPlayers0 = 6;
		aGrideSize0 = 480.0f;
		aSpeed0 = 10.0f;
		aColor0 = 2;
		
		// BUILD NEW PLAYGROUND
		prepareNewGame();

		// SET FLAGS TO ACTIVATE GAMEPLAY
		aIsReady = true;

		return res;
	}

	private boolean checkWorldRebuild () {
		boolean res = false;
		if (aWorld == null) res = true;

		// CHECK IF WORLD MUST BE REBUILD
		if (aPrefs != null) {
			float userGS = aPrefs.gridSize();
			if (userGS != aGrideSize0) {
				// set new grid size
				aGrideSize0 = userGS;
				// request world build
				res = true;
			}
		}		
		return res;
	}

	private boolean checkModelRebuild () {
		boolean res = false;
		// TO DO
		// CHECK IF WORLD MUST BE REBUILD
		if (BikeModel == null) res = true;
		if (aTrackRenderer == null) res = true;
		
		return res;
	}

	private void resetScores () {
	    if (aScores == null) {
			aScores = new long[MAX_PLAYERS];
	    }
	    for (int i =0; i < aScores.length; i++) {
	    	aScores[i] = 0;
	    }
	}
	
	private void resetSound () {
		// MUSIC
	    // FIXME
	    SoundManager.CleanUp();
		SoundManager soundFx = SoundManager.Get1(aContext);
		if (soundFx != null) {
			if (aPrefs != null)
				soundFx.initUserSettings(aPrefs);

			// start soundtrack
			soundFx.playMusic(true);
		}
		
	}
	
	private void prepareNewGame () {
	
		// GET SOME FREE MEMORY
		System.gc();

		//Load preferences
	    aPrefs = new UserPrefs(aContext);

	    // reload textures
	    SetupGL.PrepareNewGame2();
	    
		// TO IMPROVE
	    aNbPlayers0 = aPrefs.numberOfPlayers();
	    aCurrentBikes = aNbPlayers0;
	    if (aPlayers == null) {
	    	aPlayers = new Player[MAX_PLAYERS];
	    }
	    resetScores();

        // WORLD
	    if (checkWorldRebuild()) {
	    	// re-init the world
	    	initWalls(aGrideSize0);
	    	// Load Models
	    	aWorld = new WorldGraphics(aContext, aGrideSize0);
	    }
	    
	    resetSound();
	    
	    if (checkModelRebuild()) {
	    	// Load Models
	    	BikeModel = new Model(aContext, R.raw.lightcyclehigh); // SOMEDAY SEVERAL MODELS ;-)
	    	aTrackRenderer = new TrackRenderer();
	    	aTrackManager = new TrackManager(MAX_PLAYERS, aTrackRenderer);
	    }

	    if (aTrackManager != null)
	    	aTrackManager.reset();

	    Explosion.NewGame();
	    
		if (aVisual == null) {
			aVisual = new Video(aScreenW, aScreenH);
		}

		// Setup perspective
		aVisual.doPerspectiveMV(aGrideSize0);
		
		// BUILD PLAYERS
	    BikeTracks.ResetTotal();
	    aColor0 = aPrefs.playerColor();
	    aSpeed0 = aPrefs.speed();
		for (int plyr = 0; plyr < aNbPlayers0; plyr++) {
			aPlayers[plyr] = new Player(aTrackManager, plyr, aGrideSize0, aSpeed0, aColor0);
		}
		
		ComputerAI.initAI2(aWalls, aPlayers, aGrideSize0);

		// HUD RESET TO HOME
		HUD.emptyConsole();
		HUD.displayInstr(true);

		// CAMERA CIRCLING
		aCam = new Camera(aPlayers[OWN_PLAYER], CamType.E_CAM_TYPE_CIRCLING);
		
		// TIMER RESET
		resetTime();

		// CHANGE STATE
		aIsHome = true;
		aIsGameReset = false;

	}

	// hooks for android pausing thread
	public void pauseGame()
	{
		// MUSIC
		SoundManager soundFx = SoundManager.Get1(aContext);
		if (soundFx != null) {
			soundFx.stopMusicBG();
		}
		
//		aIsPause = true;
		
	}
	
	// hooks for android resuming thread
	public void resumeGame()
	{
		/*
		if (aPrefs != null)
		{
			aPrefs.ReloadPrefs();

			// Update options
			if (!aIsHome) {
				aCam.updateType(aPrefs.CameraType());
			}
			else {
				aIsGameReset = true;
			}
									
			resetTime();
		}
		*/
		// LH HACK
		// FIXME: pause is not well managed
		// better restart game
		aIsGameReset = true;

//		aIsPause = false;
	}
	
	public void updateScreenSize(int width, int height)
	{
		aScreenW = width;
		aScreenH = height;
		
		if (aVisual == null) {
			aVisual = new Video(aScreenW, aScreenH);
		}
		else {
			aVisual.SetWH(aScreenW, aScreenH);
		}
	}
	
	public void sendTouchEvent (float x, float y)
	{
		if (!aIsReady)
			return;
		
		if (aIsHome) {
			setGameStart();
			
			// Change the camera and start movement.
			aCam = new Camera(aPlayers[OWN_PLAYER], aCamType0);
			
			// remove instructions
			HUD.displayInstr(false);
			// set flags to game start
			aIsHome = false;
		}
		else if (isPlayActive) {
			// GAME PLAY
			// TURN LEFT OR RIGHT
			if (x <= (aVisual._iwidth / 2)) {
				inputDirection = aPlayers[OWN_PLAYER].TURN_LEFT;
			}
			else {
				inputDirection = aPlayers[OWN_PLAYER].TURN_RIGHT;
			}
			aIsProcessInput = true;
		}
		else {
			long endAnim = 2000; // 2 seconds
			long deltaEnd = getPlayTime() - aTimeStop;
			// NEW GAME
			if (deltaEnd > endAnim)
				aIsGameReset = true;
		}
	}
	
	public void RunGameFrame () {
		
		// UPDATE TIME
		updateTime0();
		ComputerAI.updateTime(aTimeNow);

		if (aIsGameReset) {
			prepareNewGame();
		}
		else if (isPlayActive) {
			if (aIsProcessInput) {
				Player userP = aPlayers[OWN_PLAYER];
				if (userP != null) 
					userP.doTurn(inputDirection, aTimeNow);
				aIsProcessInput = false;
			}
			// don't activate bots if game not started			
			playAI();
		}
		
		// RENDER FRAME
		RenderGameFrame();
		
	}

	private void playAI () {
		// round robin AI to speed up frame time
		Player bot = aPlayers[aiCount]; 
    	if ((bot != null) && !bot.isCrashed())	{
			ComputerAI.doComputer(aiCount, OWN_PLAYER);
		}
       	aiCount++;
    	
    	if (aiCount > (aNbPlayers0 - 1))
    		aiCount = 1;
		
	}
	
	private void resetTime()
	{
		// LH HACK TO CHANGE
		//aTimeNow = SystemClock.uptimeMillis();
		if (aCustomRenderer != null) {
			aCustomRenderer.resetTime();
			aTimeNow = aCustomRenderer.getPlayTime();
			aFrameT0 = aTimeNow;
			aFrameDT = 0;
		}
		
	}
	
	private void updateTime0()
	{
		// LH HACK TO CHANGE
		if (aCustomRenderer != null){
			// store old value
			aFrameT0 = aTimeNow;
			// store new value
			//aTimeNow = SystemClock.uptimeMillis();
			aTimeNow = aCustomRenderer.getPlayTime();
			// update Delta Time
			aFrameDT = aTimeNow - aFrameT0;
		}		
	}
	
	private void initWalls (float gridSize)
	{
		float raw[][] = {
				{0.0f, 0.0f, 1.0f, 0.0f },
				{ 1.0f, 0.0f, 0.0f, 1.0f },
				{ 1.0f, 1.0f, -1.0f, 0.0f },
				{ 0.0f, 1.0f, 0.0f, -1.0f }
		};
		
		float width = gridSize;
		float height = gridSize;
		
		int j;

		if (aWalls == null) {
	    	aWalls = new Segment[4];
	    	aWalls[0] = new Segment();
	    	aWalls[1] = new Segment();
	    	aWalls[2] = new Segment();
	    	aWalls[3] = new Segment();
	    }
		
		for (j = 0; j < 4; j++) {
			aWalls[j].vStart.v[0] = raw[j][0] * width;
			aWalls[j].vStart.v[1] = raw[j][1] * height;
			aWalls[j].vDirection.v[0] = raw[j][2] * width;
			aWalls[j].vDirection.v[1] = raw[j][3] * height;
		}
	}

	
	private void RenderGameFrame ()
	{		
		if (aIsHome) {			
			// DRAW ALL ELEMENTS
			drawNextFrameHome();
			// DRAW INFO TEXT
			if (HUD != null) {
				// BIG LEAKS
				HUD.drawInfoHome();
			}
		}
		else if (isPlayActive) {
			// game has started
			movePlayers();

			// DRAW ALL ELEMENTS
			drawNextFrame2();
			
			// Check if Game has finished
			checkGameEnd();
			
			// DRAW INFO TEXT
			if (HUD != null)
				HUD.drawInfo();
			
		}
		else {
			// DRAW ALL ELEMENTS
			drawNextFrameFinish();
			// DRAW INFO TEXT
			if (HUD != null)
				HUD.drawInfo();
			
			
		}
		
	}

	private void movePlayers () {
		for (int player = 0; player < aNbPlayers0; player++) {
			// move the bikes
			aPlayers[player].doMovement2(aFrameDT, aTimeNow, aWalls, aPlayers);
		}
	}

	private void checkGameEnd () {
		boolean boOwnPlayerActive = true;
		boolean boOtherPlayersActive = false;
		aCurrentBikes=aNbPlayers0;
		for (int p2 = 0; p2 < aNbPlayers0; p2++)
		{				
			//check win lose should be in game logic not render - FIXME
			if (p2 == OWN_PLAYER) {
				//if (aPlayers[p2].getSpeed() == 0.0f) {
				if (aPlayers[p2].isCrashed()) {
					boOwnPlayerActive = false;						
					aCurrentBikes--;
				}
			}
			else {
//				if (aPlayers[p2].getSpeed() > 0.0f) {						
				if (! aPlayers[p2].isCrashed()) {
					boOtherPlayersActive = true;
				}
				else {
					aCurrentBikes--;
				}
			}				
		}
		// if game has started
		if (!boOwnPlayerActive && boOtherPlayersActive) {
			setGameEnd(false);
		}
		else if (boOwnPlayerActive && !boOtherPlayersActive) {
			setGameEnd(true);
		}
		
		// update score
		aScores[OWN_PLAYER] = aPlayers[OWN_PLAYER].getScore();

	}

	private void drawNextFrameWorld () {

		// PREPARE VIEW
		// Load identity
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glLoadIdentity();
		aVisual.doPerspective(aGrideSize0);
		
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
		GLES11.glLoadIdentity();

		GLES11.glLightfv(GLES11.GL_LIGHT1, GLES11.GL_POSITION, aCam.getCamBuffer());
		
		// START DRAWING NEW FRAME
		// CLEAR IN BLACK
		GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT | GLES11.GL_DEPTH_BUFFER_BIT | GLES11.GL_STENCIL_BUFFER_BIT);

		// IN
		GLES11.glDisable(GLES11.GL_CULL_FACE);							
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		GLES11.glShadeModel(GLES11.GL_FLAT);

		aCam.doLookAt();
		aWorld.drawSkyBox1();
				
		// BIG LEAKS :-(
		aWorld.drawWallsBlend();
		// SOME MEMORY LEAKS
		aWorld.drawFloorTexturedBlend();

		// LH TODO
		// TEXT WILL APPEAR BLACK IF BIKES ARE NOT DRAWN
		//GLES11.glEnable(GLES11.GL_DEPTH_TEST);

		// OUT
		GLES11.glEnable(GLES11.GL_CULL_FACE);							
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		GLES11.glShadeModel(GLES11.GL_SMOOTH);
		
		Lighting.SetupLights(LightType.E_WORLD_LIGHTS);
	}
	
	private void drawNextFrameHome () {

		// camera follow player bike
		aCam.moveCameraHome(aPlayers[OWN_PLAYER], aTimeNow, aFrameDT);

		drawNextFrameWorld();
		
		// LH HACK
		// draw each bike and trail
		for (int player = 0; player < aNbPlayers0; player++) {
			if (player == OWN_PLAYER || aPlayers[player].isVisible(aCam)) {
				// DRAW BIKE
				// SMALL LEAKS
				aPlayers[player].drawCycleFast(aTimeNow, aFrameDT);
			}
		}
	}

		
	private void drawNextFrame2 () {

		// START DRAWING NEW FRAME
		
		// camera follow player bike
		aCam.doCameraMovement(aPlayers[OWN_PLAYER], aTimeNow, aFrameDT);
		
		drawNextFrameWorld();
		Explosion.DrawFrame();

		aTrackManager.drawFrameOldTracks(1.0f);

		// draw each bike and trail
		for (int player = 0; player < aNbPlayers0; player++) {
			Player curP = aPlayers[player];
			//if (player == OWN_PLAYER || aPlayers[player].isVisible(aCam)) {
			if (!curP.isCrashed() && curP.isVisible(aCam)) {
				// DRAW BIKE	
				curP.drawCycleFast(aTimeNow, aFrameDT);
			}
			curP.drawActiveTracks(aTrackRenderer, aCam);
		}

	}

	private void drawNextFrameFinish () {

		// START DRAWING NEW FRAME
		
		// camera follow player bike
		aCam.moveCameraHome(aPlayers[OWN_PLAYER], aTimeNow, aFrameDT);
		
		drawNextFrameWorld();

		// draw each bike and trail
		for (int player = 0; player < aNbPlayers0; player++) {
			Player curP = aPlayers[player];
			if (curP.getTrailHeight() > 0) {
				// DRAW BIKE	
				curP.drawCycleFast(aTimeNow, aFrameDT);
			}
			// DRAW TRAIL
			curP.drawActiveTracks(aTrackRenderer, aCam);
		}

		aTrackManager.drawFrameOldTracks(0);
		Explosion.DrawFrame();
		
	}
	
	public void setGameStart () {
		// flag end of game
		isPlayActive = true;
		resetTime();
	}
	
	public void setGamePause () {
	}
	
	public void setGameEnd (boolean winner) {
		// flag end of game
		isPlayActive = false;
		aTimeStop = getPlayTime();
		// stop travelling
		aCam.setTravelling(false);
		// stop speed
		aPlayers[OWN_PLAYER].setSpeed(0.0f);
		if (winner) {
			HUD.displayWin();
		}
		else {
			HUD.displayLose();
		}
	}

	public long getScore (int player) {
		long res = 0;
		if (aScores != null) {
			if (player < aScores.length)
				res = aScores[player];
		}
		return res;
	}
	
	public long getFPS () {
		return curFPS;
	}
	public long getFPSmin () {
		return minFPS;
	}
	public long getFPSmax () {
		return maxFPS;
	}
	public long getFPSavg () {
		return avgFPS;
	}

	public void setFPS (long fps, long avg) {
		curFPS = fps;
		avgFPS = avg;
		
		// update min max
		if (curFPS > 0) {
			if (curFPS > maxFPS)
				maxFPS = curFPS;
			if (curFPS < minFPS)
				minFPS = curFPS;
		}
	}
	
	public long getPlayTime () {
		
		curPlayTime = aCustomRenderer.getPlayTime();
		//if (isPlayActive) {
			// update active time
			//curPlayTime = aCustomRenderer.getPlayTime();
		//}
		return curPlayTime; 
	}
	
	public long getTotalTrails () {
		long res = 0;
		res = BikeTracks.GetTotal();
		return res;
	}
	
	public long getPower (int player) {
		long res = 0;
		//FIXME
		res = aPlayers[OWN_PLAYER].getPower();
		return res;
	}
	public long getGcMemory () {
		long res = 0;
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) aContext.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		res = mi.availMem / 1024L; // KB
		return res;
	}
	
	public static Vector3 GetBikeBBox (int i) {
		return BikeModel.GetBBoxSize();
	}

	public static float GetBikeBBoxRadius (int i) {
		return BikeModel.GetBBoxRadius();
	}
}
