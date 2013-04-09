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

import java.util.Random;

//import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES11;
import android.util.FloatMath;

//import com.applh.lightbike.Sound.SoundManager;

import com.applh.lightbike.Video.*;
import com.applh.lightbike.fx.Explosion;
import com.applh.lightbike.fx.TrackRenderer;
import com.applh.lightbike.matrix.Vector3;

public class Player {

	private int aPlayerID;
	private int Direction;
	private int LastDirection;
	
	private int Score;
	
	private TrackManager aTrackManager;
	private final int MaxTracks = 100;
	private BikeTracks[] tabTracks = new BikeTracks[MaxTracks] ;
	
	private int aPower;
	private boolean aIsCrash;
	private int aDamageMin = 10;
	private int aDamageRange = 40;
	
	private float aCrashRotationZ = 0;
	private int aCrashRotationTTL = 0;
	
	public int aTrailOffset;
	private float aTrailHeight;
	
	private float aSpeed;
	private float aSpeedRef;
	private int aSpeedKmh;	
	private float aBoost;
	private int aBoostFactor;
	
	public long TurnTime;
	public final float DIRS_X[] = {0.0f, -1.0f, 0.0f, 1.0f};
	public final float DIRS_Y[] = {-1.0f, 0.0f, 1.0f, 0.0f};

	private final float dirangles[] = { 0.0f, -90.0f, -180.0f, 90.0f, 180.0f, -270.0f };
	public final int TURN_LEFT = 3;
	public final int TURN_RIGHT = 1;
	public final int TURN_LENGTH = 200;
	public final float TRAIL_HEIGHT = 3.5f;
		
	private Random aRand;
	
	private static final float ColourDiffuse[][] = {
			{ 0.0f, 0.1f, 0.900f, 1.000f},      // Blue
			{ 1.00f, 0.550f, 0.140f, 1.000f},   // Yellow
			{ 0.750f, 0.020f, 0.020f, 1.000f},  // Red
			{ 0.800f, 0.800f, 0.800f, 1.000f},  // Grey
			{ 0.120f, 0.750f, 0.0f, 1.000f},    // Green
			{ 0.750f, 0.0f, 0.35f, 1.000f},      // Purple
	};

	private static final float ColourSpecular[][] = {
			{ 0.0f, 0.1f, 0.900f, 1.000f},    // Blue
			{0.500f, 0.500f, 0.000f, 1.000f}, // Yellow
			{0.750f, 0.020f, 0.020f, 1.000f}, // Red
			{1.00f, 1.00f, 1.00f, 1.000f},    // Grey
			{0.050f, 0.500f, 0.00f, 1.00f},   // Green
			{0.500f, 0.000f, 0.500f, 1.00f},  // Purple
	};
	
	private static final float ColourAlpha[][] = {
     		{0.0f, 0.1f, 0.900f, 0.600f},      // Blue
			{1.000f, 0.850f, 0.140f, 0.600f}, // Yellow
			{0.750f, 0.020f, 0.020f, 0.600f}, // Red
			{0.700f, 0.700f, 0.700f, 0.600f}, // Grey
			{0.120f, 0.700f, 0.000f, 0.600f}, // Green
			{0.720f, 0.000f, 0.300f, 0.600f},  // Purple
	};

	private int mPlayerColourIndex = 0;

	// USED BY MESH DRAWING
	// FASTER WITH PUBLIC ACCESS
	public float mPlayerColourDiffuse[];
	public float mPlayerColourSpecular[];
	public float mPlayerColourAlpha[];

	public byte mColorDiffuse255[];
	public byte mColorSpecular255[];
	public byte mColorAlpha255[];

	private float aBBminX;
	private float aBBminY;
	private float aBBmaxX;
	private float aBBmaxY;
	
	//private float mPlayerColourAlpha0;
	static public boolean isAlphaTrail = false;

	
//	private final int MAX_LOD_LEVEL = 3;
	private static final int LOD_DIST[][] = {
			{ 1000, 1000, 1000 },
			{100, 200, 400},
			{30,100,200},
			{10,30,150}
	};
	
	public Player (TrackManager manager, int player_number, float gridSize, float speed, int colorPref)
	{
		// when power = 0, Bike is crashed 
		aPower = 100;
		aIsCrash = false;
		
		aTrackManager = manager;
		
		// init random colors
		aRand = new Random();

		mPlayerColourDiffuse = new float[4];
    	mPlayerColourDiffuse[0]= 0.4f + 0.6f * aRand.nextFloat();
    	mPlayerColourDiffuse[1]= 0.4f + 0.6f * aRand.nextFloat();
    	mPlayerColourDiffuse[2]= 0.4f + 0.6f * aRand.nextFloat();
    	mPlayerColourDiffuse[3]= 1.0f;

    	mPlayerColourSpecular = new float[4];
    	mPlayerColourSpecular[0]= mPlayerColourDiffuse[0] + (1.0f - mPlayerColourDiffuse[0]) * 0.2f * aRand.nextFloat();
    	mPlayerColourSpecular[1]= mPlayerColourDiffuse[1] + (1.0f - mPlayerColourDiffuse[0]) * 0.2f * aRand.nextFloat();
    	mPlayerColourSpecular[2]= mPlayerColourDiffuse[2] + (1.0f - mPlayerColourDiffuse[0]) * 0.2f * aRand.nextFloat();
    	mPlayerColourSpecular[3]= 1.0f ;

    	mPlayerColourAlpha = new float[4];
    	mPlayerColourAlpha[0]= mPlayerColourDiffuse[0];
    	mPlayerColourAlpha[1]= mPlayerColourDiffuse[1];
    	mPlayerColourAlpha[2]= mPlayerColourDiffuse[2];
    	mPlayerColourAlpha[3]= 1.0f;
    	//mPlayerColourAlpha[3]= 0.7f + 0.002f * rand.nextInt(100);
		// store the initial alpha
		//mPlayerColourAlpha0 = mPlayerColourAlpha[3];

		// INITIAL POSITION
		Direction = aRand.nextInt(3); // accepts values 0..3;
		LastDirection = Direction;
		
		tabTracks[0] = new BikeTracks(this);
		aTrailOffset = 0;
//		Trails[trailOffset].vStart.v[0] = START_POS[player_number][0] * gridSize;
//		Trails[trailOffset].vStart.v[1] = START_POS[player_number][1] * gridSize;
		if (player_number == 0) {
			tabTracks[aTrailOffset].vStart.v[0] = 0.5f * gridSize;
			tabTracks[aTrailOffset].vStart.v[1] = 0.5f * gridSize;
		}
		else {
			// pair so cant crash with player at start
			tabTracks[aTrailOffset].vStart.v[0] = 2 * (1 + aRand.nextInt(99))* 0.005f * gridSize;
			tabTracks[aTrailOffset].vStart.v[1] = 2 * (1 + aRand.nextInt(99))* 0.005f * gridSize;
		
			// give some disadvantage to bots 
			aDamageRange = 80;
		}
		
/*
		float distance = 0.5f * player_number/LightBikeGame.mCurrentBikes;
		double angle = rand.nextDouble()* 2.0 * Math.PI;
		Trails[trailOffset].vStart.v[0] = 0.5f + distance* (float) Math.cos(angle) * gridSize;
		Trails[trailOffset].vStart.v[1] = 0.5f + distance* (float) Math.sin(angle) * gridSize;
*/
		tabTracks[aTrailOffset].vDirection.v[0] = 0.0f;
		tabTracks[aTrailOffset].vDirection.v[1] = 0.0f;
		
		aTrailHeight = TRAIL_HEIGHT;

		// RESET BOUNDING BOX
		aBBminX = tabTracks[aTrailOffset].vStart.v[0];
		aBBmaxX = aBBminX;
		aBBminY = tabTracks[aTrailOffset].vStart.v[1];
		aBBmaxY = aBBminY;
		
		aSpeed = speed;
		aSpeedRef = aSpeed;
		
		aBoost = 1.001f; // 0.1% boost
		//Speed0 = Speed;
		aSpeedKmh = (int) (15 * aSpeed);
		//exp_radius = 0.0f;
		
		aPlayerID = player_number;
		Score = 0;
		
		// Select Colour
		int nbcolors = ColourDiffuse.length;
		if(player_number == LightBikeGame.OWN_PLAYER)
		{
			int pcolor = colorPref;
			if ((pcolor >= 0) && (pcolor < nbcolors)) {
				mPlayerColourIndex = pcolor;
			}
			else {
				mPlayerColourIndex = 0;
			}
			mPlayerColourDiffuse = ColourDiffuse[mPlayerColourIndex];
			mPlayerColourSpecular = ColourSpecular[mPlayerColourIndex];
			mPlayerColourAlpha = ColourAlpha[mPlayerColourIndex];
			// store the initial alpha
			//mPlayerColourAlpha0 = mPlayerColourAlpha[3];
		}

		// compute the colors in 255
		mColorDiffuse255 = new byte[4];
		mColorSpecular255 = new byte[4];
		mColorAlpha255 = new byte[4];
		for (int c=0; c<4; c++) {
			mColorDiffuse255[c] = (byte)(mPlayerColourDiffuse[c] * 255.0f);
			mColorSpecular255[c] = (byte)(mPlayerColourSpecular[c] * 255.0f);
			mColorAlpha255[c] = (byte)(mPlayerColourAlpha[c] * 255.0f);
		}
	}
	
	private void updateBBox (float curX, float curY) {
		// UPDATE BOUNDING BOX		
		//float curX = tabTracks[aTrailOffset].vStart.v[0];
		//float curY = tabTracks[aTrailOffset].vStart.v[1];
		//float curX = getXpos();
		//float curY = getYpos();
		if (curX <aBBminX) {
			aBBminX = curX;
		}
		else if (curX > aBBmaxX) {
			aBBmaxX = curX;
		}
		if (curY <aBBminY) {
			aBBminY = curY;
		}
		else if (curY > aBBmaxY) {
			aBBmaxY = curY;
		}
		
	}
	public void doTurn (int direction, long current_time)
	{
		float x = getXpos();
		float y = getYpos();
		
		// FIXME
		if (aTrailOffset >= tabTracks.length) {
			restartTrack(current_time);
		}
		aTrailOffset++;
		tabTracks[aTrailOffset] = new BikeTracks(this);
		tabTracks[aTrailOffset].vStart.v[0] = x;
		tabTracks[aTrailOffset].vStart.v[1] = y;
		tabTracks[aTrailOffset].vDirection.v[0] = 0.0f;
		tabTracks[aTrailOffset].vDirection.v[1] = 0.0f;
		
		LastDirection = Direction;
		Direction = (Direction + direction) % 4;
		TurnTime = current_time;

		updateBBox(x, y);
		
		doRuleTurn(10);
	}

	public void restartTrack (long curTime)
	{
		float x = getXpos();
		float y = getYpos();

		aTrackManager.addPastTracks(tabTracks, aTrailOffset, aTrailHeight, mPlayerColourDiffuse);
		
		// reset track
		aTrailOffset = 0;
		tabTracks[aTrailOffset] = new BikeTracks(this);
		tabTracks[aTrailOffset].vStart.v[0] = x;
		tabTracks[aTrailOffset].vStart.v[1] = y;
		tabTracks[aTrailOffset].vDirection.v[0] = 0.0f;
		tabTracks[aTrailOffset].vDirection.v[1] = 0.0f;
		
		LastDirection = Direction;
		//Direction = (Direction + direction) % 4;
		TurnTime = curTime;

		// RESET BOUNDING BOX
		aBBminX = x;
		aBBmaxX = aBBminX;
		aBBminY = y;
		aBBmaxY = aBBminY;

		// FIXME
		doRuleTurn(100);
	}

	public void doRuleTurn (int bonus) {
		// turn is good :)
		Score += bonus;
		// ref speed
		int speedRef1 = Math.round(15 * aSpeedRef) + 50;
		int speedRef0 = Math.round(15 * aSpeedRef) - 50;

		float fBrake = 0.08f * aRand.nextFloat();
		if (aSpeedKmh > speedRef1)
			fBrake = 0.16f * aRand.nextFloat();
		else if (aSpeedKmh < speedRef0)
			fBrake = 0;
		
		aSpeed = (1 - fBrake) * aSpeed;
		// random boost 0.1%-0.5% or more if slow
		aSpeedKmh = (int) (15 * aSpeed);
		// brake factor
//		float fBrake = 0.01f * aRand.nextInt(12);
//		float fBrake = 0.01f * aRand.nextInt(10);
		aBoostFactor = 3;
		//int SpeedRef = 200;
		int minBoost = 0;
		if (aSpeedKmh < speedRef0)
			minBoost = 2;
		else if (aSpeedKmh < speedRef1)
			minBoost = 1;
		
		if (aSpeedKmh > 0) {
			//myBoostFactor = rand.nextInt(5*SpeedRef/mSpeedKmh);
			//aBoostFactor = aRand.nextInt(3*SpeedRef/aSpeedKmh);
			aBoostFactor = Math.round(3 * aRand.nextFloat());
			if (aBoostFactor < minBoost)
				aBoostFactor = minBoost;
			
			aBoost = 1.0f + 0.001f * aBoostFactor;
			// LH TO IMPROVE
			if (aPlayerID == LightBikeGame.OWN_PLAYER) {
				String booster = " ";
				for (int b=0; b<aBoostFactor; b++) {
					booster += "*";
				}
				LightBikeGame.GetHUD(aPlayerID).addLineToConsole("SPEED " + aSpeedKmh + booster);			
			}
		}
	}
	
	public void doMovement2 (long dt, long curTime, Segment walls[], Player players[])
	{		
		if (aSpeed > 0.0f) {
			updateSpeed();
			float scale = 0.01f;
			float dist;
			dist = aSpeed * dt * scale;
						
			updateTrackPosition(dist);
			
			// check collision
			checkCrashBike(players, curTime);
			doCrashTestWalls(walls);			
		}
		else if (aIsCrash) {
			// trail melting down
			if (aTrailHeight > 0.0f) {
				aTrailHeight -= (dt * TRAIL_HEIGHT) / 1000.0f;
			}
		}
	}

	private float updateSpeed () {
		// Player is still alive
		// Booster :)
		aSpeed *= aBoost;
		
		// LH debug
		int speedKmh2 = (int) (15 * aSpeed);
		if (Math.abs(speedKmh2-aSpeedKmh) > 5) {
			// LH TO IMPROVE
			if (aPlayerID == LightBikeGame.OWN_PLAYER) {
				String booster = " ";
				for (int b=0; b<aBoostFactor; b++) {
					booster += "*";
				}
				LightBikeGame.GetHUD(aPlayerID).addLineToConsole("SPEED " + aSpeedKmh + booster);			
			}

			aSpeedKmh = speedKmh2;
		}
		return aSpeed;
	}
	
	private  void updateTrackPosition (float dist) {
		// update the last trail
		tabTracks[aTrailOffset].vDirection.v[0] += dist * DIRS_X[Direction];
		tabTracks[aTrailOffset].vDirection.v[1] += dist * DIRS_Y[Direction];		

		updateBBox(getXpos(), getYpos());
	}
	
	public void drawCycleFast (long curr_time, long time_dt)
	{
		GLES11.glPushMatrix();
		GLES11.glTranslatef(getXpos(), getYpos(), 0.0f);

		doBikeRotation(curr_time);
		//Lights.setupLights(gl, LightType.E_CYCLE_LIGHTS);

		// IN
		//GLES11.glFrontFace(GLES11.GL_CCW);
		//GLES11.glShadeModel(GLES11.GL_SMOOTH);
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		GLES11.glEnable(GLES11.GL_LIGHTING);
		GLES11.glEnable(GLES11.GL_DEPTH_TEST);
		GLES11.glDepthMask(true);

		GLES11.glEnable(GLES11.GL_NORMALIZE);
		GLES11.glTranslatef(0.0f, 0.0f, LightBikeGame.GetBikeBBox(aPlayerID).v[2] / 2.0f);
		//GLES11.glEnable(GLES11.GL_CULL_FACE);
		//gl.glTranslatef((GridSize/2.0f), (GridSize/2.0f), 0.0f);
		//gl.glTranslatef(_Player._PlayerXpos, _Player._PlayerYpos, 0.0f);
		// LH HACK
		LightBikeGame.GetBike(0).Draw2(mPlayerColourSpecular, mPlayerColourDiffuse);

		// OUT
		//GLES11.glDisable(GLES11.GL_CULL_FACE);
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		GLES11.glDisable(GLES11.GL_LIGHTING);
		//GLES11.glShadeModel(GLES11.GL_FLAT);

		GLES11.glPopMatrix();		
	}

	public void doCrashTestWalls(Segment Walls[])
	{
		Segment Current = tabTracks[aTrailOffset];
		Vector3 V;
		
		for (int j=0; j < 4; j++) {
			V = Current.Intersect(Walls[j]);
			
			if (V != null) {
				if (Current.t1 >= 0.0f && Current.t1 < 1.0f && Current.t2 >= 0.0f && Current.t2 < 1.0f) {
					
					Current.vDirection.v[0] = V.v[0] - Current.vStart.v[0];
					Current.vDirection.v[1] = V.v[1] - Current.vStart.v[1];
					//Explode = new Explosion(0.0f);
					aIsCrash = true;
					Explosion.Create(this);
					
					// EXPLOSION MAY NEED SPEED INFO
					aSpeed = 0.0f;
					aPower = 0;
					break;
				}
			}
		}
		
	}
	
	public boolean inBB (float x, float y) {
		// NOT USABLE AS WORKING WITH VECTOR AND NOT POSITION :-/
		boolean res = true;
		// FIXME
		return res;
/*		
		// MANAGE Tracks Bounding Box
		if (x < aBBminX) {
			res = false;
		}
		else if (x > aBBmaxX) {
			res = false;
		}
		else if (y < aBBminY) {
			res = false;
		}
		else if (y > aBBmaxY) {
			res = false;
		}
		
		return res;
*/
	}
	
	private boolean vertBB (float x0, float y0, float x1, float y1) {
		boolean res = false;
		if (x0 == x1) {
			boolean res0 = false;
			boolean res1 = false;
			// 0|X|0
			// 0|X|0
			// 0|X|0
			if ((x0 >= aBBminX) && (x0 <= aBBmaxX)) {
				// 0|0|0
				// 0|X|0
				// 0|0|0
				if ((y0 >= aBBminY) && (y0 <= aBBmaxY))
					res0 = true;
				if ((y1 >= aBBminY) && (y1 <= aBBmaxY))
					res1 = true;
				
				res = res0 || res1;
				if (!res) {
					res0 = true;
					res1 = true;
					if ((y0 < aBBminY) && (y1 < aBBminY)) {
						// 0|0|0
						// 0|0|0
						// 0|X|0
						res0 = false;
						res1 = false;						
					}
					if ((y0 > aBBmaxY) && (y1 > aBBmaxY)) {
						// 0|X|0
						// 0|0|0
						// 0|0|0
						res0 = false;
						res1 = false;						
					}
				}
			}
			res = res0 || res1;
		}
		else {
			// NEEDS FURTHER CHECK
			res = true;
		}
		
		return res;
	}

	private boolean inBB3 (float x0, float y0, float x1, float y1) {
		boolean res = false;
		boolean res0 = false;
		boolean res1 = false;
		
		if (x0 == x1) {
			res = vertBB(x0, y0, x1, y1);
		}
		else if (y0 == y1) {
			res = vertBB(y0, x0, y1, x1);
		}
		else {		
			if ((x0 > aBBminX) && (x0 < aBBmaxX)) res0 = true;
			if ((y0 > aBBminY) && (y0 < aBBmaxY)) res0 = true;
			if ((x1 > aBBminX) && (x1 < aBBmaxX)) res1 = true;
			if ((y1 > aBBminY) && (y1 < aBBmaxY)) res1 = true;
			res = res0 || res1;
		}		
		
		// FIXME
		return res;
	}

	private boolean maybeBB2 (float x0, float y0, float x1, float y1) {
		boolean res = false;
		boolean res0 = false;
		boolean res1 = false;
		
		if ((x0 > aBBminX) && (x0 < aBBmaxX)) res0 = true;
		if ((y0 > aBBminY) && (y0 < aBBmaxY)) res0 = true;
		if ((x1 > aBBminX) && (x1 < aBBmaxX)) res1 = true;
		if ((y1 > aBBminY) && (y1 < aBBmaxY)) res1 = true;
		res = res0 || res1;
		
		// FIXME
		return res;
	}

	
	private boolean canCrossBB (float x0, float y0, float dx, float dy) {
		boolean res = true;
		// VERTICAL VECTOR
		if (dx == 0) {
			if (x0 < aBBminX) {
				// LEFT
				res = false;
			}
			else if (x0 > aBBmaxX) {
				// RIGHT
				res = false;
			}
			else {
				// X CENTER
				if (y0 < aBBminY) {
					// BOTTOM
					if (y0 + dy > aBBminY)
						res = true;
					else
						res = false;
				}
				else if (y0 > aBBmaxY) {
					// TOP
					if (y0 + dy < aBBmaxY)
						res = true;
					else
						res = false;
				}
				else {
					// Y CENTER
					res = true;
				}
				
			}
		}

		// HORIZONTAL VECTOR
		if (dy == 0) {
			if (y0 < aBBminY) {
				// BOTTOM
				res = false;
			}
			else if (y0 > aBBmaxY) {
				// TOP
				res = false;
			}
			else {
				// Y CENTER
				if (x0 < aBBminX) {
					// LEFT
					if (x0 + dx > aBBminX)
						res = true;
					else
						res = false;
				}
				else if (x0 > aBBmaxX) {
					// RIGHT
					if (x0 + dx < aBBmaxX)
						res = true;
					else
						res = false;
				}
				else {
					// X CENTER
					res = true;
				}
				
			}
		}

		return res;
	}


	public void checkCrashBike (Player players[], long curTime)
	{
		int j,k;
		Segment Current = tabTracks[aTrailOffset];
		Segment Wall;
		Vector3 V;
		float curX0 = Current.vStart.v[0];
		float curY0 = Current.vStart.v[1];
		float curDx = Current.vDirection.v[0];
		float curDy = Current.vDirection.v[1];
		
		//float curX1 = getXpos();
		//float curY1 = getYpos();
		
		// try each bike
		for (j = 0; j < LightBikeGame.aNbPlayers0; j++) {
			Player testPlayer = players[j];
			// is bike alive ?
			if (!testPlayer.aIsCrash) {				
//				if (testPlayer.maybeBB2(curX0, curY0, curX1, curY1)) {
				if (testPlayer.canCrossBB(curX0, curY0, curDx, curDy)) {
					// check each trail segment for this bike
					for (k =0; k < testPlayer.aTrailOffset  + 1; k++) {
						if (testPlayer == this && k >= aTrailOffset - 1)
							break;
						
						Wall = testPlayer.getTrail(k);
						
						V = Current.Intersect(Wall);
						
						if (V != null) {
							if (Current.t1 >= 0.0f && Current.t1 < 1.0f && Current.t2 >= 0.0f && Current.t2 < 1.0f) {
								// UPDATE POSITION AT INTERSECTION POINT
								Current.vDirection.v[0] = V.v[0] - Current.vStart.v[0];
								Current.vDirection.v[1] = V.v[1] - Current.vStart.v[1];
								
								// CREATE EXPLOSION
								Explosion.Create(this);

								// RANDOM DAMAGE
								Random rand = new Random();
								int damage = aDamageMin + rand.nextInt(aDamageRange);							
								aPower -= damage;
								
								if (aPower <= 0)  {
									aPower = 0;
									aIsCrash = true;
									// EXPLOSION MAY NEED SPEED INFO
									aSpeed = 0.0f;
								}
								else {
									// FIXME
									// RESET TRACKS FOR BOTH BIKES
									restartTrack(curTime);
									testPlayer.restartTrack(curTime);
									// animate
									aCrashRotationTTL++;
								}
								
								//break;
							}
						}
					}				
					
				}
			}
		}
	}


	public void drawActiveTracks (TrackRenderer render, Camera cam)
	{
		if (aTrailHeight > 0.0f) {
			if (render != null) {
				render.drawTracks(tabTracks, aTrailOffset, aTrailHeight, mPlayerColourDiffuse);
			}
		}
	}	
	
	public boolean isVisible (Camera cam)
	{
		Vector3 v1;
		Vector3 v2;
		Vector3 tmp = new Vector3(getXpos(),getYpos(),0.0f);
		int lod_level = 2;
		float d,s;
		int i;
		int LC_LOD = 3;
		float fov = 120;
		
		boolean retValue;
		
		v1 = cam._target.sub(cam._cam);
		v1.Normalise();
		
		v2 = cam._cam.sub(tmp);
		
		d = v2.Length();
		
		for (i=0; (i<LC_LOD) && (d >= LOD_DIST[lod_level][i]); i++);
		
		if (i >= LC_LOD) {
			retValue = false;
		}
		else {
			v2 = tmp.sub(cam._cam);
			v2.Normalise();
			
			s = v1.Dot(v2);
			d = FloatMath.cos((float) (fov * Math.PI / 360.0f));
			
			if (s < d - (LightBikeGame.GetBikeBBoxRadius(aPlayerID) * 2.0f)) {
				retValue = false;
			}
			else {
				retValue = true;
			}
			
		}
		
		return retValue;
	}
	
	private float getDirAngle (long time)
	{
		int last_dir;
		float dir_angle;
		
		if (time < TURN_LENGTH) {
			last_dir = LastDirection;
			if (Direction == 3 && last_dir ==2) {
				last_dir = 4;
			}
			if (Direction == 2 && last_dir == 3) {
				last_dir = 5;
			}
			//dir_angle = ((TURN_LENGTH - time) * dirangles[last_dir] + time * dirangles[Direction]) / TURN_LENGTH;
			dir_angle = dirangles[last_dir] + (dirangles[Direction] - dirangles[last_dir]) * time / TURN_LENGTH;		}
		else {
			dir_angle = dirangles[Direction];
		}
		return dir_angle;
	}

	private void doBikeRotation (long CurrentTime)
	{
		  long time = CurrentTime - TurnTime;
		  float dirAngle;
		  if (time < TURN_LENGTH) {
			  dirAngle = getDirAngle(time);
			  // Z ROTATION
			  /*
			  float Angle;
			  float axis = -1.0f;
			  if (LastDirection != Direction) {
				  // LastDir 0 1 2
				  // Dir 0 1
				  if( (Direction < LastDirection) && (LastDirection != 3)) {
					  axis = 1.0f;
				  }
				  else if( ((LastDirection == 3) && (Direction == 2)) ||
						          ((LastDirection == 0) && (Direction == 3)) ) {
					  axis = 1.0f;
				  }
				  Angle = 25.0f * FloatMath.sin((float) (Math.PI * time / TURN_LENGTH));
				  // Y ROTATION
				  GLES11.glRotatef(Angle, 0.0f, axis, 0.0f);
			  }
			  */
		  }
		  else {
			  // Z ROTATION
			  dirAngle = dirangles[Direction];		  
		  }
		  GLES11.glRotatef(dirAngle, 0.0f, 0.0f, 1.0f);

		  // FIXME
		  if (aCrashRotationTTL > 0) {
			  // Z ROTATION
			  if (aCrashRotationZ < 360) {		  
				  aCrashRotationZ += 20;
				  GLES11.glRotatef(aCrashRotationZ, 0.0f, 0.0f, 1.0f);
			  }
			  else {
				  // reset
				  aCrashRotationTTL = 0;
				  aCrashRotationZ = 0;
			  }
		  }
	}

	public Segment getTrail(int offset)
	{
		return tabTracks[offset];
	}
	
	public Segment[] getTrails()
	{
		return tabTracks;
	}
	
	public float getTrailHeight()
	{
		return aTrailHeight;
	}
			
	public float getXpos()
	{
		return tabTracks[aTrailOffset].vStart.v[0] + tabTracks[aTrailOffset].vDirection.v[0];
	}
	
	public float getYpos()
	{
		return tabTracks[aTrailOffset].vStart.v[1] + tabTracks[aTrailOffset].vDirection.v[1];
	}

	public int getDirection()
	{
		return Direction;
	}
	
	public int getLastDirection()
	{
		return LastDirection;
	}

	public int getPower()
	{
		return aPower;
	}

	public float getSpeed()
	{
		return aSpeed;
	}
	
	public void setSpeed(float sp)
	{
		aSpeed = sp;
		//Speed0 = sp;
	}
	public void addScore(int val)
	{
		if (aSpeed > 0.0f)
			Score += val;
	}
	
	public int getScore()
	{
		return Score;
	}

	public boolean isCrashed () {
		return aIsCrash;
	}
}
