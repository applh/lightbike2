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

import java.util.HashMap;

import com.applh.lightbike.R;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundManager {

	// music
	private static SoundManager Instance = null;
	private static Context aContext = null;
	private static MediaPlayer aPlayer = null;
	private static SoundPool aSoundPool = null;
	
	// sound effects
	private static HashMap<Integer, Integer> mSoundPoolMap = null;
//	private static AudioManager mAudioManager = null;
	
//	private static final int MAX_SOUNDS = 4;
//	private static int MAX_INDEX = 10;
//	private static int mIndexToStream[] = new int[MAX_INDEX];

	private static boolean IsPlayMusic = true;
	
	static synchronized public SoundManager Get1 (Context context)
	{
		if (Instance == null) {
			Instance = new SoundManager();
			Instance.initManager(context);
		}
		return Instance;
	}
	
	private SoundManager()
	{
		Instance = null;
		aContext = null;
		aPlayer = null;
		aSoundPool = null;
		
		// sound effects
		mSoundPoolMap = null;

		IsPlayMusic = true;
	}

	/*
	 * Requests the instance if the sound manager and creates it
	 * if it does not exist
	 */
	
	public void initManager (Context theContext)
	{
		aContext = theContext;
		if (aContext != null) {
			try {
				// FIXME
				// should be moved
				addMusicBG(R.raw.music_bg1);

				//aSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
				//mSoundPoolMap = new HashMap<Integer, Integer>();
			}
			catch (Exception e) {
				// FIXME
			}
		}
	}

	public static void CleanUp ()
	{
		try {
			
			if (Instance != null) {
				Instance.stopMusicBG();
			}
			
			if (aSoundPool != null) {
				aSoundPool.release();
				aSoundPool = null;
			}
			
			if (mSoundPoolMap != null) {
				mSoundPoolMap.clear();
				mSoundPoolMap = null;
			}
			
			if (aContext != null) {
				AudioManager TheAudioManager = (AudioManager) aContext.getSystemService(Context.AUDIO_SERVICE);
				if (TheAudioManager != null)
					TheAudioManager.unloadSoundEffects();
			}

			Instance = null;
		}
		catch (Exception e) {
			// FIXME
		}
	}

	public void addSound (int Index, int SoundID)
	{
		if (aContext != null) {
			mSoundPoolMap.put(Index, aSoundPool.load(aContext, SoundID, 1));
		}
	}
	
	public void addMusicBG (int MusicResID)
	{
		// limited to one music stream FIXME
		if (aContext != null) {
			try {
				aPlayer = MediaPlayer.create(aContext, MusicResID);
//				aPlayer.prepare();
				AudioManager mAudioManager = (AudioManager) aContext.getSystemService(Context.AUDIO_SERVICE);
				float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				streamVolume /= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				aPlayer.setVolume(streamVolume, streamVolume);
			}
			catch (Exception e) {
				// FIXME
			}
		}
	}

	public void initUserSettings (UserPrefs up) {
		if (up != null) {
			IsPlayMusic = up.playMusic();			
		}
	}

	public void playMusic (boolean boLoop)
	{
		if ((aPlayer != null) && IsPlayMusic) {	
			try {
//				aPlayer.prepare();
				
				aPlayer.setLooping(boLoop);
				aPlayer.start();
			}
			catch (Exception e) {
				// FIXME
			}
		}
		
	}

	public void stopMusicBG ()
	{
		if (aPlayer != null) {
			aPlayer.stop();
		}
	}
	
	/*
	public void stopMusicBG ()
	{
		if (aPlayer != null) {
			if (aPlayer.isPlaying()) {
				aPlayer.stop();
				try {
//					aPlayer.prepare();
					aPlayer.seekTo(0);
				}
				catch (Exception e) {
					// do nothing here FIXME
				}
			}
		}
	}

	
	public static void playSound(int index, float speed)
	{
		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume /= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		streamVolume *= 0.5;
		mIndexToStream[index] = mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, speed);
	}
	
	public static void playSoundLoop(int index, float speed)
	{
		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume /= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		streamVolume *= 0.5;
		mIndexToStream[index] = mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, speed);
	}

	public static void stopSound(int index)
	{
		//mSoundPool.stop(mSoundPoolMap.get(index));
		mSoundPool.stop(mIndexToStream[index]);
	}

	public static void changeRate(int index, float rate)
	{
		//mSoundPool.setRate(mSoundPoolMap.get(index), rate);
		mSoundPool.setRate(mIndexToStream[index], rate);
	}
	public static void globalPauseSound()
	{
		if(mSoundPool != null)
			mSoundPool.autoPause();
		
		if (aPlayer != null && aPlayer.isPlaying())
			aPlayer.pause();
	}
	
	public static void globalResumeSound()
	{
		if(mSoundPool != null)
			mSoundPool.autoResume();
		
		if (aPlayer != null)
			aPlayer.start();
	}
*/	
	
}
