package com.applh.lightbike.Game;

import android.opengl.GLES11;

import com.applh.lightbike.fx.TrackRenderer;

public class TrackManager {

	private TrackSequence[] aTabTS;
	private TrackRenderer aRenderer;
	private int aCurPos;
	private int aMaxPos;
	private float aScale;
	
	public TrackManager (int nbBike, TrackRenderer renderer) {
		aTabTS = new TrackSequence[200];
		aCurPos = 0;
		aMaxPos = 0;
		
		aRenderer = renderer;
		
		reset();
	}
	
	public void reset () {
		// reset
		for (int i=0; i<aTabTS.length; i++) {
			aTabTS[i] = null;
		}
		//aScale = 0.05f;
		aScale = 0.1f;
	}
	
	public void addPastTracks (BikeTracks [] tabTracks, int nbTracks, float height, float[] color) {
		TrackSequence newTS = new TrackSequence(tabTracks, nbTracks, height, color);
		if (aCurPos < aTabTS.length) {
			aTabTS[aCurPos] = newTS;
			aCurPos++;
			if (aCurPos < aTabTS.length) {
				aMaxPos = aCurPos;
			}
			else {
				aMaxPos = aTabTS.length;
			}
			// recycle
			aCurPos = aCurPos % aTabTS.length;
		}
	}
	
	public void drawFrameOldTracks (float delta) {
		
		// SECURITY
		if (aMaxPos > aTabTS.length) 
			aMaxPos = aTabTS.length;
		
		// IN
		GLES11.glDisable(GLES11.GL_CULL_FACE);
		
		for (int t=0; t <aMaxPos; t++) {
			TrackSequence curTS = aTabTS[t];
			if (curTS != null) {
				float trackH = curTS.aHeight;
				int nbTracks = curTS.aNbTracks;
				
				if (trackH > 0.0f) {
					if (aRenderer != null) {
						aRenderer.drawTracks(curTS.aTabTracks, nbTracks, trackH, curTS.aColor);
						// melt down
						curTS.aHeight-= aScale * delta;
					}
				}
				else {
					// reset
					aTabTS[t]=null;
				}

			}
		}
		// OUT
		GLES11.glEnable(GLES11.GL_CULL_FACE);							
	}
	
}
