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

	public GLTexture aSkyboxTex[] = null;
	public GLTexture aWallTex[] = null;
	public GLTexture aFloorTex[] = null;

	public int aNbSkybox = 18;
	public int aNbWall = 16;
	public int aNbFloor = 3;
	
	public GraphicSet (Context c) {
		aContext = c;
		
		aFloorTex = new GLTexture[aNbFloor];
		for (int i=0; i<aFloorTex.length; i++) {
			aFloorTex[i] = null;
		}

		aWallTex = new GLTexture[aNbWall];
		for (int i=0; i<aWallTex.length; i++) {
			aWallTex[i] = null;
		}
		
		aSkyboxTex = new GLTexture[aNbSkybox];
		for (int i=0; i<aSkyboxTex.length; i++) {
			aSkyboxTex[i] = null;
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

	public int getTexIdSkybox (int i) {
		int res = 0;
		if (i < aSkyboxTex.length) {
			GLTexture resTex = loadTexSkybox(i);
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

			// SET 4
			case 12:
				resId = R.drawable.hd4_wall_1;
				break;
			case 13:
				resId = R.drawable.hd4_wall_2;
				break;
			case 14:
				resId = R.drawable.hd4_wall_3;
				break;
			case 15:
				resId = R.drawable.hd4_wall_4;
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
	
	public GLTexture loadTexSkybox (int resIndex) {
		int resId = 0;
		GLTexture res = null;
		
		if (resIndex < aSkyboxTex.length)
			res = aSkyboxTex[resIndex];
		
		if (res == null) {
			switch (resIndex) {
			// SET 1
			case 0:
				resId = R.drawable.skybox_10;
				break;
			case 1:
				resId = R.drawable.skybox_11;
				break;
			case 2:
				resId = R.drawable.skybox_12;
				break;
			case 3:
				resId = R.drawable.skybox_13;
				break;
			case 4:
				resId = R.drawable.skybox_1t;
				break;
			case 5:
				resId = R.drawable.skybox_1b;
				break;

			// SET 2
			case 6:
				resId = R.drawable.skybox_20;
				break;
			case 7:
				resId = R.drawable.skybox_21;
				break;
			case 8:
				resId = R.drawable.skybox_22;
				break;
			case 9:
				resId = R.drawable.skybox_23;
				break;
			case 10:
				resId = R.drawable.skybox_2t;
				break;
			case 11:
				resId = R.drawable.skybox_2b;
				break;


			// SET 3
			case 12:
				resId = R.drawable.skybox_30;
				break;
			case 13:
				resId = R.drawable.skybox_31;
				break;
			case 14:
				resId = R.drawable.skybox_32;
				break;
			case 15:
				resId = R.drawable.skybox_33;
				break;
			case 16:
				resId = R.drawable.skybox_3t;
				break;
			case 17:
				resId = R.drawable.skybox_3b;
				break;

			// DEFAULT				
			default:
				resId = R.drawable.skybox_10;
				break;				
			}

			res = new GLTexture(aContext, resId, GLES11.GL_CLAMP_TO_EDGE, GLES11.GL_CLAMP_TO_EDGE, true);
			if (resIndex < aSkyboxTex.length)
				aSkyboxTex[resIndex] = res;
		}
		return res;	
	}

}
