/*
 * AUTHOR: APPLH.COM
 */

package com.applh.lightbike.fx;

import android.content.Context;
import android.opengl.GLES11;

import com.applh.lightbike2.R;
import com.applh.lightbike.Video.GLTexture;

public class GraphicSet {

	private Context aContext = null;
	public int aGraphicSet;

	public GLTexture aWallTex[] = null;
	public GLTexture aFloorTex[] = null;
	public int aNbWall;
	public int aNbFloor;
	
	public GraphicSet (Context c) {
		aContext = c;
		
		aNbFloor = 3;
		aFloorTex = new GLTexture[aNbFloor];
		for (int i=0; i<aFloorTex.length; i++) {
			aFloorTex[i] = null;
		}

		aNbWall = 12;
		aWallTex = new GLTexture[aNbWall];
		for (int i=0; i<aWallTex.length; i++) {
			aWallTex[i] = null;
		}
		
	}
	
	public int getTexIdFloor (int i) {
		int res = 0;
		if (i < aFloorTex.length) {
			GLTexture resTex = loadTexFloor(i);
			if (resTex != null)
				res = resTex.getTextureID();
		}
		return res;
		
	}
	
	public int getTexIdWall (int i) {
		int res = 0;
		if (i < aWallTex.length) {
			GLTexture resTex = loadTexWall(i);
			if (resTex != null)
				res = resTex.getTextureID();
		}
		return res;
	}

	public GLTexture loadTexFloor (int resIndex) {
		GLTexture res = null;
		
		if (resIndex < aFloorTex.length)
			res = aFloorTex[resIndex];
		
		int resId = 0;
		if (res == null) {
			switch (resIndex) {
			case 0:
				resId = R.drawable.hd1_floor;
				break;
			case 1:
				resId = R.drawable.hd2_floor;
				break;
			case 2:
				resId = R.drawable.hd3_floor;
				break;
			default:
				resId = R.drawable.hd1_floor;
				break;				
			}

			res = new GLTexture(aContext, resId);
			if (resIndex < aFloorTex.length)
				aFloorTex[resIndex] = res;
		}
		return res;	
	}

	public GLTexture loadTexWall (int resIndex) {
		int resId = 0;
		GLTexture res = null;
		
		if (resIndex < aWallTex.length)
			res = aWallTex[resIndex];
		
		if (res == null) {
			switch (resIndex) {
			// SET 1
			case 0:
				resId = R.drawable.hd1_wall_1;
				break;
			case 1:
				resId = R.drawable.hd1_wall_2;
				break;
			case 2:
				resId = R.drawable.hd1_wall_3;
				break;
			case 3:
				resId = R.drawable.hd1_wall_4;
				break;
			// SET 2
			case 4:
				resId = R.drawable.hd2_wall_1;
				break;
			case 5:
				resId = R.drawable.hd2_wall_2;
				break;
			case 6:
				resId = R.drawable.hd2_wall_3;
				break;
			case 7:
				resId = R.drawable.hd2_wall_4;
				break;
			// SET 3
			case 8:
				resId = R.drawable.hd3_wall_1;
				break;
			case 9:
				resId = R.drawable.hd3_wall_2;
				break;
			case 10:
				resId = R.drawable.hd3_wall_3;
				break;
			case 11:
				resId = R.drawable.hd3_wall_4;
				break;
			// DEFAULT				
			default:
				resId = R.drawable.hd1_wall_1;
				break;				
			}

			res = new GLTexture(aContext, resId, GLES11.GL_CLAMP_TO_EDGE, GLES11.GL_CLAMP_TO_EDGE, true);
			if (resIndex < aWallTex.length)
				aWallTex[resIndex] = res;
		}
		return res;	
	}

}
