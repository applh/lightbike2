/*
 * free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * See the GNU General Public License for more details.
 *
 */

package com.applh.lightbike.Game;

import java.util.Random;

import android.opengl.GLES11;

import com.applh.lightbike.Video.*;
import com.applh.lightbike.fx.Explosion;
import com.applh.lightbike.matrix.Vector3;

public class Player {

	public int aPower = 0;
	public int aPowerMax = 0;
	
	
	public float aSpeed;
	public int aCrashRotationTTL = 0;
	public boolean aIsCrash;

	public float aGridSize;
	
	public int aPlayerID;
	private int Direction;
	private int LastDirection;
	
	private int Score;
	
	private TrackManager aTrackManager;
	public final int MaxTracks = 150;
	public BikeTracks[] tabTracks = new BikeTracks[MaxTracks] ;
	
	private int aDamageMin = 10;
	private int aDamageRange = 40;
	
	private float aCrashRotationZ = 0;
	
	public int aTrailOffset;
	public float aTrailHeight;
	public int aMaxTrail = 5;
	
	private float aSpeedRef;
	private int aSpeedKmh;	
	private float aBoost;
	private float aBoostFactor;
	
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
/*	public static final int LOD_DIST[][] = {
			{ 1000, 1000, 1000 },
			{100, 200, 400},
			{30,100,200},
			{10,30,150}
	};
*/
	public Player (TrackManager manager, int player_number, float gridSize, float speed, int colorPref)
	{
		// when power = 0, Bike is crashed 
		aPower = 100;
		aIsCrash = false;
		
		aGridSize = gridSize;
		
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

		// INITIAL POSITION
		Direction = aRand.nextInt(3); // accepts values 0..3;
		LastDirection = Direction;
		
		tabTracks[0] = new BikeTracks(this);
		aTrailOffset = 0;
		if (player_number == 0) {
			tabTracks[aTrailOffset].vStart.v[0] = 0.5f * aGridSize;
			tabTracks[aTrailOffset].vStart.v[1] = 0.5f * aGridSize;
		}
		else {
			// pair so cant crash with player at start
			tabTracks[aTrailOffset].vStart.v[0] = 2 * (1 + aRand.nextInt(99))* 0.005f * aGridSize;
			tabTracks[aTrailOffset].vStart.v[1] = 2 * (1 + aRand.nextInt(99))* 0.005f * aGridSize;
		
			// give some disadvantage to bots 
			aDamageRange = 80;
		}
		
		tabTracks[aTrailOffset].vDirection.v[0] = 0.0f;
		tabTracks[aTrailOffset].vDirection.v[1] = 0.0f;
		
		aTrailHeight = TRAIL_HEIGHT;
		aMaxTrail = 5;
		
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
	public void doTurn (int direction, long curTime)
	{
		float x = getXpos();
		float y = getYpos();
		
		// FIXME
		if (aTrailOffset > (tabTracks.length-2)) {
			restartTrack(curTime);
		}
		else if (aTrailOffset > aMaxTrail) {
			restartTrack(curTime);			
		}
		
		aTrailOffset++;
		tabTracks[aTrailOffset] = new BikeTracks(this);
		tabTracks[aTrailOffset].vStart.v[0] = x;
		tabTracks[aTrailOffset].vStart.v[1] = y;
		tabTracks[aTrailOffset].vDirection.v[0] = 0.0f;
		tabTracks[aTrailOffset].vDirection.v[1] = 0.0f;
		
		LastDirection = Direction;
		Direction = (Direction + direction) % 4;
		TurnTime = curTime;

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
		// turn is good :-)
		Score += bonus;
		
		// FIXME makes game too difficult
		// turn is bad :-/ 
		// aPower -= 1;
		
		// ref speed
		int speedRef1 = Math.round(15 * aSpeedRef) + 50;
		int speedRef0 = Math.round(15 * aSpeedRef) - 50;

		float fBrake = 0.05f * aRand.nextFloat();
		if (aSpeedKmh > speedRef1)
			fBrake = 0.10f * aRand.nextFloat();
		else if (aSpeedKmh < speedRef0)
			fBrake = 0;
		
		aSpeed = (1 - fBrake) * aSpeed;

		if (aSpeed < aSpeedRef) 
			aSpeed = aSpeedRef;
		
		// random boost 0.1%-0.5% or more if slow
		aSpeedKmh = (int) (15 * aSpeed);
		// brake factor
		aBoostFactor = 3;
		//int SpeedRef = 200;
		float minBoost = 0;
		if (aSpeedKmh < speedRef0)
			minBoost = 1.0f;
		else if (aSpeedKmh < speedRef1)
			minBoost = .5f;
		
		if (aSpeedKmh > 0) {
			aBoostFactor = Math.round(2.0f * aRand.nextFloat());
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
			doCrashTestWalls(walls, curTime);			
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

	public void doCrashTestWalls(Segment Walls[], long curTime)
	{
		Segment Current = tabTracks[aTrailOffset];
		Vector3 V;

		// CROSS THE MIRROR ?
		if (Current.vStart.v[0] <= 0) {
			Current.vStart.v[0] = aGridSize-.5f;
			Current.vDirection.v[0] = -.5f;
			Current.vDirection.v[1] = 0;
		}
		else if (Current.vStart.v[0] >= aGridSize) {
			Current.vStart.v[0] = .5f;
			Current.vDirection.v[0] = .5f;
			Current.vDirection.v[1] = 0;
		}
		
		if (Current.vStart.v[1] <= 0) {
			Current.vStart.v[1] = aGridSize-.5f;
			Current.vDirection.v[0] = 0;
			Current.vDirection.v[1] = -.5f;
		}
		else if (Current.vStart.v[1] >= aGridSize) {
			Current.vStart.v[1] = .5f;
			Current.vDirection.v[0] = 0;
			Current.vDirection.v[1] = .5f;
		}
		
		for (int j=0; j < 4; j++) {
			V = Current.Intersect2(Walls[j]);
			
			if (V != null) {
				if (Current.t1 >= 0.0f && Current.t1 < 1.0f && Current.t2 >= 0.0f && Current.t2 < 1.0f) {
					
					Current.vDirection.v[0] = V.v[0] - Current.vStart.v[0];
					Current.vDirection.v[1] = V.v[1] - Current.vStart.v[1];
					
					doDamage(curTime, 0, 0, 100);
					if (aPower > 0) {
						aSpeed = aSpeedRef;
					}
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
						
						V = Current.Intersect2(Wall);
						
						if (V != null) {
							if (Current.t1 >= 0.0f && Current.t1 < 1.0f && Current.t2 >= 0.0f && Current.t2 < 1.0f) {
								// UPDATE POSITION AT INTERSECTION POINT
								Current.vDirection.v[0] = V.v[0] - Current.vStart.v[0];
								Current.vDirection.v[1] = V.v[1] - Current.vStart.v[1];
								
								// RANDOM DAMAGE
								doDamage(curTime, testPlayer.aTrailOffset/4, 0, 100);
								
								// kamikaze counts
								testPlayer.restartTrack(curTime);
								
								//break;
							}
						}
					}				
					
				}
			}
		}
	}
	
	public void doSpecial (int maxPower, long curTime) {

		// Gain Power
		if (aPower < maxPower) 
			aPower += aTrailOffset;
	
		// RESET TRACKS
		restartTrack(curTime);
		// RESET SPEED
		aSpeed = aSpeedRef;
		// animate
		aCrashRotationTTL++;				
	}
	
	public void doDamage (long curTime, int extra, int minD, int maxD) {
		
		// CREATE EXPLOSION
		Explosion.Create(this);
		
		// RANDOM DAMAGE
		int damage = aDamageMin + extra + aRand.nextInt(aDamageRange);

		if (damage < minD) damage = minD;
		else if (damage > maxD) damage = maxD;
		
		aPower -= damage;

		if (aPower <= 0)  {
			aPower = 0;
			aIsCrash = true;
			// EXPLOSION MAY NEED SPEED INFO
			aSpeed = 0.0f;
		}
		else {
			// RESET TRACKS
			restartTrack(curTime);
			// RESET SPEED
			aSpeed = aSpeedRef;
			// animate
			aCrashRotationTTL++;			
		}
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

	public void doBikeRotation (long CurrentTime)
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
	
	public void updatePowerMax () {
		if (aPower > aPowerMax) aPowerMax = aPower;
	}
	
}
