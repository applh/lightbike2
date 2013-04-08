package com.applh.lightbike.Game;

import com.applh.lightbike.Video.Segment;

public class BikeTracks extends Segment {

	private static long NbTotal = 0;
	private Player aBike;
	
	public BikeTracks (Player bike) {
		NbTotal++;
		aBike = bike;
	}

	public static long GetTotal () {
		return NbTotal;
	}
	
	public static void ResetTotal () {
		NbTotal = 0;
	}
}
