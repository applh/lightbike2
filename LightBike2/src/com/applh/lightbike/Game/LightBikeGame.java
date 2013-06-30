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
import android.util.FloatMath;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

import com.applh.lightbike2.R;

import com.applh.lightbike.LightBike;
import com.applh.lightbike.OpenGLRenderer;
import com.applh.lightbike.Game.Camera.CamType;
//import com.applh.lightbike.Sound.SoundManager;
import com.applh.lightbike.Video.*;
import com.applh.lightbike.Video.Lighting.LightType;
import com.applh.lightbike.fx.Explosion;
import com.applh.lightbike.fx.PowerUp;
import com.applh.lightbike.fx.SetupGL;
import com.applh.lightbike.fx.TrackRenderer;
import com.applh.lightbike.matrix.Vector3;


public class LightBikeGame {
	
	
	public ComputerAI aComputerAI=null;
	
	public int aPowerBonus = 100;
	public int aPowerBonus0 = 100;
	
	private long aTimeNow = 0;
	private long aFrameT0 = 0;
	private long aFrameDT = 0;
	private long aTimeStop = 0;
	
	// Define arena setting 
	private static float aGrideSize0 = 480.0f;
	private float aSpeed0 = 10.0f;
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

	private Model aBikeModel0 = null;
	private Model aBikeModel1 = null;
	
	public int aBikeRes0 = R.raw.lightcycle_high;	
	public int aBikeRes1 = R.raw.lightcycle_med;	
	
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

	public boolean aActBoost = false;
	public boolean aActBrake = false;
	public boolean aActSpecial = false;

	public int aPowerUpRatio = 10;
	
	public float aMoveX=-1.0f;
	public float aMoveY=-1.0f;
	public float aMoveDX=0;
	public float aMoveDY=0;
	
	public static final int LOD_DIST[][] = {
		{1000, 1000, 1000 },
		{100,  200,  400},
		{30,   100,  200},
		{10,   30,   150}
	};

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
		
		aComputerAI = null;
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
		aBikeModel0 = null;			
		aBikeModel1 = null;			
		
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
	
	public Model GetBike (int i) {
		Model res = aBikeModel0;
		
		if (i < 1) res = aBikeModel0;
		else res = aBikeModel1;		
		
		// FIXME
		if (avgFPS < 30) {
			res = aBikeModel1;
		}
		else if (curFPS < 40) {
			if (i < 1)
				res = aBikeModel0;
			else
				res = aBikeModel1;				
		}
		else if (curFPS > 50) {
			if (i < (curFPS/10))
				res = aBikeModel0;
			else
				res = aBikeModel1;				
		}
		
		return res;
	}
	
	public boolean initGame2 () {
		boolean res = true;

		// Explosion
		Explosion.ReInit0();
		// PowerUp
		PowerUp.ReInit0();
		
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
		if (aBikeModel0 == null) res = true;
		if (aBikeModel1 == null) res = true;
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
	    	aBikeModel0 = new Model(aContext, aBikeRes0); // SOMEDAY SEVERAL MODELS ;-)
	    	aBikeModel1 = new Model(aContext, aBikeRes1); // SOMEDAY SEVERAL MODELS ;-)
	    	
	    	aTrackRenderer = new TrackRenderer();
	    	aTrackManager = new TrackManager(MAX_PLAYERS, aTrackRenderer);
	    }

	    if (aComputerAI == null)
	    	aComputerAI = new ComputerAI();

	    if (aTrackManager != null)
	    	aTrackManager.reset();

	    Explosion.NewGame();
	    PowerUp.NewGame();
	    
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
			Player curPlayer = new Player(aTrackManager, plyr, aGrideSize0, aSpeed0, aColor0);
			if (plyr == LightBikeGame.OWN_PLAYER) {
				curPlayer.aPower = aPowerBonus + aPowerBonus0;
			}
			else {
				curPlayer.aPower = aPowerBonus + aPowerBonus0;
			}
			aPlayers[plyr] = curPlayer;
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
	
	public void sendMoveEvent (float x, float y)
	{
		if (aMoveX >0) {
			aMoveDX=x-aMoveX;
			aMoveDX=y-aMoveY;
		}
		aMoveX=x;
		aMoveY=y;
	}
	
	public void sendTouchEvent (float x, float y)
	{
		if (!aIsReady)
			return;
		
		if (aIsHome) {
			
			boolean actStartGame=false;
			
			if ((x > (aVisual._iwidth * 0.25f)) && (x < (aVisual._iwidth * 0.75f))) {
				if ((y > aVisual._iheight *0.25f) && (y < aVisual._iheight *0.75f)) {
						actStartGame=true;
				}
				else if ((y > aVisual._iheight *0.75f) && (y < aVisual._iheight *0.9f)) {
		        	// OPEN THE SETTINGS MENU
					LightBike lba = (LightBike) aContext;
		        	lba.startPreferences();
				}
			}
			
			if (actStartGame) {
				setGameStart();
				
				// Change the camera and start movement.
				aCam = new Camera(aPlayers[OWN_PLAYER], aCamType0);
				
				// remove instructions
				HUD.displayInstr(false);
				// set flags to game start
				aIsHome = false;
			}
		}
		else if (isPlayActive) {
			// GAME PLAY
			
			// bottom 2/3 screen
			// avoid android back button
			if (y > aVisual._iheight *0.3f) {
				// TURN LEFT OR RIGHT
				if (x < (aVisual._iwidth * 1/5)) {
					inputDirection = aPlayers[OWN_PLAYER].TURN_LEFT;
					aIsProcessInput = true;
				}
				else if (x > (aVisual._iwidth * 4/5)) {
					inputDirection = aPlayers[OWN_PLAYER].TURN_RIGHT;
					aIsProcessInput = true;
				}
				else if (x < (aVisual._iwidth * 2/5)) {
					aActBrake = true;
				}
				else if (x > (aVisual._iwidth * 3/5)) {
					aActBoost = true;
				}
				else {
					// middle button
					aActSpecial = true;
				}
			}
			else {
				aActSpecial = true;
			}
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
		aComputerAI.updateTime(aTimeNow);

		if (aIsGameReset) {
			prepareNewGame();
		}
		else if (isPlayActive) {
			Player userP = aPlayers[OWN_PLAYER];
			if (aIsProcessInput) {
				if (userP != null) 
					userP.doTurn(inputDirection, aTimeNow);
				aIsProcessInput = false;
			}
			
			if (aActBoost) {
				if ((userP != null) && (userP.aPower >1)) {
					userP.aPower-=1;
					userP.aSpeed*=1.2;
				}
				aActBoost=false;
			}
			else if (aActBrake) {
				if ((userP != null) && (userP.aPower >1)) {
					if (userP.aSpeed > aSpeed0) {
						userP.aPower-=1;
						userP.aSpeed*=.9;
					}
				}
				aActBrake=false;
			}
			else if (aActSpecial) {
				if ((userP != null) && (userP.aPower >0)) {
					// MAX Power Bonus
					userP.doSpecial(aPowerBonus0, aTimeNow);
				}
				aActSpecial=false;
			}
			
			if (userP != null) {
				if (userP.aPower < 0) {
					userP.aPower=0;
					userP.aIsCrash=true;
				}
			}
			
			// don't activate bots if game not started			
			playAI();
			playPowerUp();
		}
		
		// RENDER FRAME
		RenderGameFrame();
		
	}

	private void playPowerUp () {
		Player userP = aPlayers[OWN_PLAYER];
		// MANAGE POWERUP
		PowerUp.ManageRules(aGrideSize0, userP, aPowerUpRatio);

		int check = PowerUp.checkPlayer(userP);
		if (check >0) {
			Player curP = null;
			for (int player = 0; player < aNbPlayers0; player++) {
				curP=aPlayers[player];
				if (player != OWN_PLAYER) {
					curP.doDamage(aTimeNow, 0);
				}
				else {
					curP.aPower+=10;
				}
			}			
		}
	}
	
	private void playAI () {
				
		// round robin AI to speed up frame time
		Player bot = aPlayers[aiCount]; 
    	if ((bot != null) && !bot.isCrashed())	{
			aComputerAI.doComputer(aiCount, OWN_PLAYER);
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
			drawNextFrame();
			
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
				if (! aPlayers[p2].isCrashed()) {
					boOtherPlayersActive = true;
				}
				else {
					aCurrentBikes--;
				}
			}				
		}
		
		aPlayers[OWN_PLAYER].updatePowerMax();
		
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
			if (player == OWN_PLAYER || isVisible(aPlayers[player], aCam)) {
				// DRAW BIKE
				// SMALL LEAKS
				drawCycleFast(aPlayers[player], aTimeNow, aFrameDT);
			}
		}
	}

	
	public boolean isVisible (Player player, Camera cam)
	{
		if (curFPS > 60) return true;		
		
		Vector3 v1;
		Vector3 v2;
		Vector3 tmp = new Vector3(player.getXpos(), player.getYpos(), 0.0f);
		int lod_level = 2;

		if (curFPS > 40) lod_level=1;

		float d,s;
		int i;
		int LC_LOD = 3;
		float fov = 120;
		
		boolean retValue;
		
		v1 = cam._target.sub(cam.aCam);
		v1.Normalise();
		
		v2 = cam.aCam.sub(tmp);
		
		d = v2.Length();
		
		for (i=0; (i<LC_LOD) && (d >= LOD_DIST[lod_level][i]); i++);
		
		if (i >= LC_LOD) {
			retValue = false;
		}
		else {
			v2 = tmp.sub(cam.aCam);
			v2.Normalise();
			
			s = v1.Dot(v2);
			d = FloatMath.cos((float) (fov * Math.PI / 360.0f));
			
			if (s < d - (GetBikeBBoxRadius(player.aPlayerID) * 2.0f)) {
				retValue = false;
			}
			else {
				retValue = true;
			}
			
		}
		
		return retValue;
	}
		
	private void drawNextFrame () {

		// START DRAWING NEW FRAME
		if (curFPS > 25) 
			aTrackRenderer.aAlphaMin=0.0f;
		else
			aTrackRenderer.aAlphaMin=1.0f;
			
		// camera follow player bike
		aCam.doCameraMovement(aPlayers[OWN_PLAYER], aTimeNow, aFrameDT);
		
		drawNextFrameWorld();
		Explosion.DrawFrame();
		PowerUp.DrawFrame();

		aTrackManager.drawFrameOldTracks(1.0f);

		// draw each bike and trail
		for (int player = 0; player < aNbPlayers0; player++) {
			Player curP = aPlayers[player];
			//if (player == OWN_PLAYER || aPlayers[player].isVisible(aCam)) {
			if (!curP.isCrashed() && isVisible(curP, aCam)) {
				// DRAW BIKE	
				drawCycleFast(curP, aTimeNow, aFrameDT);
			}
			drawActiveTracks(curP, aTrackRenderer, aCam);
		}

	}

	public void drawActiveTracks (Player player, TrackRenderer render, Camera cam)
	{
		if (player.aTrailHeight > 0.0f) {
			if (render != null) {
				render.drawTracks(player.tabTracks, player.aTrailOffset, player.aTrailHeight, player.mPlayerColourDiffuse);
			}
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
				drawCycleFast(curP, aTimeNow, aFrameDT);
			}
			// DRAW TRAIL
			aTrackRenderer.aAlphaMin=.8f;
			drawActiveTracks(curP, aTrackRenderer, aCam);
		}

		aTrackManager.drawFrameOldTracks(0);
		PowerUp.DrawFrame();
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
			// increase power from game to game
			aPowerBonus = 100 + aPlayers[OWN_PLAYER].aPower;

			HUD.displayWin();
			
			// save the max score
			if (aPrefs != null) 
				aPrefs.saveMaxPower(aPlayers[OWN_PLAYER].aPowerMax);
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
	
	public long getMaxPower (int player) {
		long res=200;
		
		if (aPrefs != null)
			res=aPrefs.aPowerMax;
		else if (isPlayActive) 
			res=aPlayers[OWN_PLAYER].aPowerMax;

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
		res = aPlayers[OWN_PLAYER].aPower;
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
	
	public Vector3 GetBikeBBox (int i) {
		return GetBike(i).GetBBoxSize();			
	}

	public float GetBikeBBoxRadius (int i) {
		return GetBike(i).GetBBoxRadius();
	}
	
	public void drawCycleFast (Player player, long curr_time, long time_dt)
	{
		GLES11.glPushMatrix();
		GLES11.glTranslatef(player.getXpos(), player.getYpos(), 0.0f);

		player.doBikeRotation(curr_time);
		//Lights.setupLights(gl, LightType.E_CYCLE_LIGHTS);

		// IN
		//GLES11.glFrontFace(GLES11.GL_CCW);
		//GLES11.glShadeModel(GLES11.GL_SMOOTH);
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		GLES11.glEnable(GLES11.GL_LIGHTING);
		GLES11.glEnable(GLES11.GL_DEPTH_TEST);
		//GLES11.glDepthMask(true);

		GLES11.glEnable(GLES11.GL_NORMALIZE);
		GLES11.glTranslatef(0.0f, 0.0f, GetBikeBBox(player.aPlayerID).v[2] / 2.0f);
		//GLES11.glEnable(GLES11.GL_CULL_FACE);
		//gl.glTranslatef((GridSize/2.0f), (GridSize/2.0f), 0.0f);
		//gl.glTranslatef(_Player._PlayerXpos, _Player._PlayerYpos, 0.0f);
		// LH HACK
		GetBike(player.aPlayerID).Draw(player.mPlayerColourSpecular, player.mPlayerColourDiffuse);

		// OUT
		//GLES11.glDisable(GLES11.GL_CULL_FACE);
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		GLES11.glDisable(GLES11.GL_LIGHTING);
		//GLES11.glShadeModel(GLES11.GL_FLAT);

		GLES11.glPopMatrix();		
	}

}
