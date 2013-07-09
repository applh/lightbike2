/*
 * Copyright © 2012 Iain Churcher
 *
 * Based on GLtron by Andreas Umbach (www.gltron.org)
 * 
 * Preferences implementation based on work by Noah NZM TECH
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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.applh.lightbike.Game.Camera;

public class UserPrefs {

	// Pref defaults
	private final int C_PREF_FOLLOW_CAM = 10;
	private final int C_PREF_FOLLOW_CAM_FAR = 20;
	private final int C_PREF_FOLLOW_CAM_CLOSE = 30;
	private final int C_PREF_BIRD_CAM = 40;
	
	// change camera each play
	private final String C_DEFAULT_CAM_TYPE = "0";
	
	//private static final float C_GRID_SIZES[] = {480.0f, 720.0f, 1440.0f};
	private static final float C_GRID_SIZES[] = {480.0f, 720.0f, 1440.0f};
	
	private static final float C_SPEED[] = {5.0f, 10.0f, 15.0f, 20.0f};
	
	private Context aContext;
	private Camera.CamType mCameraType;
	
	private boolean aMusic;
	private boolean mSFX;
	
	private boolean mFPS;
	
	private int aNumOfPlayers;
	private float aGridSize;
	private float aSpeed;
	private int aPlayerColor;
	
	public int aPowerMax=0;
	public int aBikesMax=0;
	
	public UserPrefs (Context ctx)
	{
		aContext = ctx;
		reloadPrefs();
	}
	
	public void reloadPrefs()
	{
		int cameraType;
		int gridIndex;
		int speedIndex;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
		cameraType = Integer.valueOf(prefs.getString("cameraPref", C_DEFAULT_CAM_TYPE));

		aPowerMax = Integer.valueOf(prefs.getString("powerMax", "200"));
		aBikesMax = Integer.valueOf(prefs.getString("bikesMax", "3"));
		
		switch(cameraType)
		{
			case C_PREF_FOLLOW_CAM:
				mCameraType = Camera.CamType.E_CAM_TYPE_FOLLOW;
				break;
			case C_PREF_FOLLOW_CAM_FAR:
				mCameraType = Camera.CamType.E_CAM_TYPE_FOLLOW_FAR;
				break;
			case C_PREF_FOLLOW_CAM_CLOSE:
				mCameraType = Camera.CamType.E_CAM_TYPE_FOLLOW_CLOSE;
				break;
			case C_PREF_BIRD_CAM:
				mCameraType = Camera.CamType.E_CAM_TYPE_BIRD;
				break;
			default:
				Random rand = new Random();
				int autoCam = 10 + 10 * rand.nextInt(5);
				// default
				mCameraType = Camera.CamType.E_CAM_TYPE_BIRD;
				
				if (autoCam == C_PREF_FOLLOW_CAM)
					mCameraType = Camera.CamType.E_CAM_TYPE_FOLLOW;
				if (autoCam == C_PREF_FOLLOW_CAM_FAR)
					mCameraType = Camera.CamType.E_CAM_TYPE_FOLLOW_FAR;
				if (autoCam == C_PREF_FOLLOW_CAM_CLOSE)
					mCameraType = Camera.CamType.E_CAM_TYPE_FOLLOW_CLOSE;
				if (autoCam == C_PREF_BIRD_CAM)
					mCameraType = Camera.CamType.E_CAM_TYPE_BIRD;
				break;
		}
		
		aMusic = prefs.getBoolean("musicOption", true);
		aNumOfPlayers = Integer.valueOf(prefs.getString("playerNumber", "10"));
		gridIndex = Integer.valueOf(prefs.getString("arenaSize", "0"));
		aGridSize = C_GRID_SIZES[gridIndex];
		aPlayerColor = Integer.valueOf(prefs.getString("playerBike","2"));

		mSFX = prefs.getBoolean("sfxOption", false);
		mFPS = prefs.getBoolean("fpsOption", true);
		speedIndex = Integer.valueOf(prefs.getString("gameSpeed", "1"));
		aSpeed = C_SPEED[speedIndex];
	}
	
	public void saveMaxPower (int power) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
		aPowerMax = Integer.valueOf(prefs.getString("powerMax", "200"));
		if (aPowerMax < power) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("powerMax", ""+power);
			editor.commit();
		}
	}

	public void saveMaxBikes (int bikes) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
		aBikesMax = Integer.valueOf(prefs.getString("bikesMax", "3"));
		if (aBikesMax < bikes) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("bikesMax", ""+bikes);
			editor.commit();
		}
	}

	public Camera.CamType CameraType()
	{
		
		return mCameraType;
	}
	
	public boolean playMusic()
	{
		return aMusic;
	}
	
	public boolean PlaySFX()
	{
		return mSFX;
	}
	
	public boolean DrawFPS()
	{
		return mFPS;
	}
	
	public int numberOfPlayers()
	{
		return aNumOfPlayers;
	}
	
	public float gridSize()
	{
		return aGridSize;
	}
	
	public float speed()
	{
		return aSpeed;
	}
	
	public int playerColor()
	{
		return aPlayerColor;
	}
}
