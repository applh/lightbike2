package com.applh.lightbike.Game;

public class TrackSequence {

	public BikeTracks[] aTabTracks = null;
	public int aNbTracks;
	public float aHeight;
	public float[] aColor;
	
	TrackSequence (BikeTracks[] tabTracks, int nbTracks, float height, float[] color) {
		aHeight = height;
		aNbTracks = nbTracks;
		aTabTracks = tabTracks;
		aColor = color;
	}
	
}
