/*
 * Author: APPLH.COM
 * Creation: 24/08/12
 * 
 * Modification:
 * 24/08/12
 */

package com.applh.lightbike.fx;

import java.nio.FloatBuffer;
import java.util.Random;

import android.content.Context;
import android.opengl.GLES11;

import com.applh.lightbike2.R;
import com.applh.lightbike.Video.Font;
import com.applh.lightbike.Video.GLTexture;

public class SetupGL {

	private static Random aRand = null;
	private static Context aContext = null;
	
	private static GraphicSet aGSet;
	private static int aGSetFloor;
	private static int aGSetWall1;
	private static int aGSetWall2;
	private static int aGSetWall3;
	private static int aGSetWall4;
	
	private static int aTexIdWall1;
	private static int aTexIdWall2;
	private static int aTexIdWall3;
	private static int aTexIdWall4;
	private static int aTexIdFloor;

	private static int aGSetSkybox1;
	private static int aGSetSkybox2;
	private static int aGSetSkybox3;
	private static int aGSetSkybox4;
	private static int aGSetSkyboxB;
	private static int aGSetSkyboxT;
	
	private static int aTexIdSkybox1;
	private static int aTexIdSkybox2;
	private static int aTexIdSkybox3;
	private static int aTexIdSkybox4;
	private static int aTexIdSkyboxB;
	private static int aTexIdSkyboxT;
	
	// Textures
	private static GLTexture SplashTex = null;
	private static GLTexture ExplodeTex = null;
//	private static GLTexture SkyBoxTex[] = null; 

	private static int TexIdExplode = -1;
	
// static long aFreeMem = 0;
	
//	private static final long aMemHD = 256;
//	private static final long aMemSD = 192;
//	private static final long aMemLD = 128;

//	private static final long aHeightHD = 512;
//	private static final long aHeightSD = 256;
//	private static final long aHeightLD = 128;

	private static Font aXenoFont = null;

//	private static int aScreenW = 512;
//	private static int aScreenH = 512;
	
	private static int aMinFilter = GLES11.GL_LINEAR_MIPMAP_NEAREST;
	private static int aMaxFilter = GLES11.GL_LINEAR;

	// CONSTRUCTOR
	public SetupGL (Context c, int screenW, int screenH) {

		aContext = c;

		aRand = new Random();
		
		aGSet = new GraphicSet(aContext);
		
		aGSetFloor = 0;
		
		aGSetWall1 = 0;
		aGSetWall2 = 0;
		aGSetWall3 = 0;
		aGSetWall4 = 0;
		
//		aFreeMem = 0;
		
		aXenoFont = null;
		
//		aScreenW = screenW;
//		aScreenH = screenH;
		
		// reinit textures as context changed
		SplashTex = null;
//		SkyBoxTex = null; 

		ExplodeTex = null;
		TexIdExplode = -1;
		
		// reset BB Manager
		ByteBufferManager.Reinit();
		
	}
	
	// STATIC INIT FOR GLES11
	public boolean init (long freeM) {
		boolean res = true;
		
//		aFreeMem = freeM;
		
		return res;
	}
	
	public void clearScreen () {
		GLES11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT | GLES11.GL_DEPTH_BUFFER_BIT);

	}
	public void myInitSurface () {
		
		// GENERAL SETUP
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glEnable(GLES11.GL_DEPTH_TEST);

		//GLES11.glDisable(GLES11.GL_BLEND);

		GLES11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT | GLES11.GL_DEPTH_BUFFER_BIT);

		GLES11.glLoadIdentity();
		
		
	}
	
	public void myInitSplash () {
		// SCREEN SETUP
		GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT | GLES11.GL_DEPTH_BUFFER_BIT);
		GLES11.glDisable(GLES11.GL_LIGHTING);

	}
	
	public void myInitGame () {
		
		// GENERAL SETUP
		GLES11.glShadeModel(GLES11.GL_SMOOTH);
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE_MINUS_SRC_ALPHA);
		GLES11.glEnable(GLES11.GL_DEPTH_TEST);

		GLES11.glEnable(GLES11.GL_LIGHTING);

		// CAN BE LOADED LATER ?
		// LOAD TEXTURES
		PrepareNewGame2();
		GetExplodeTex();
		
	}

	public static void PrepareNewGame2 () {
		PrepareTexWalls();
		PrepareTexFloor();
		PrepareTexSkybox();
	}
	
	public static void PrepareTexFloor () {
		aGSetFloor = aRand.nextInt(aGSet.aNbFloor);
		aTexIdFloor = aGSet.getTexIdFloor(aGSetFloor);
	}

	public static void PrepareTexWalls () {
		int setWall = aGSet.aNbSetWall;
		int w = aRand.nextInt(setWall);

		aGSetWall1 = w*aGSet.aNbImgWall;
		aGSetWall2 = w*aGSet.aNbImgWall +1;
		aGSetWall3 = w*aGSet.aNbImgWall +2;
		aGSetWall4 = w*aGSet.aNbImgWall +3;
		
		aTexIdWall1 = aGSet.getTexIdWall(aGSetWall1);
		aTexIdWall2 = aGSet.getTexIdWall(aGSetWall2);
		aTexIdWall3 = aGSet.getTexIdWall(aGSetWall3);
		aTexIdWall4 = aGSet.getTexIdWall(aGSetWall4);
	}	

	public static void PrepareTexSkybox () {
		int setSkyBox = aGSet.aNbSetSkybox;
		int w = aRand.nextInt(setSkyBox);
		
		aGSetSkybox1 = w*aGSet.aNbImgSkybox;
		aGSetSkybox2 = w*aGSet.aNbImgSkybox +1;
		aGSetSkybox3 = w*aGSet.aNbImgSkybox +2;
		aGSetSkybox4 = w*aGSet.aNbImgSkybox +3;
		aGSetSkyboxB = w*aGSet.aNbImgSkybox +4;
		aGSetSkyboxT = w*aGSet.aNbImgSkybox +5;
		
		aTexIdSkybox1 = aGSet.getTexIdSkybox(aGSetSkybox1);
		aTexIdSkybox2 = aGSet.getTexIdSkybox(aGSetSkybox2);
		aTexIdSkybox3 = aGSet.getTexIdSkybox(aGSetSkybox3);
		aTexIdSkybox4 = aGSet.getTexIdSkybox(aGSetSkybox4);
		aTexIdSkyboxB = aGSet.getTexIdSkybox(aGSetSkyboxB);
		aTexIdSkyboxT = aGSet.getTexIdSkybox(aGSetSkyboxT);
	}	

	public static void PushTexFilter (int minFilter, int maxFilter) {
    	GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MIN_FILTER, minFilter);
    	GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MAG_FILTER, maxFilter);
	}
	
	public static void PopTexFilter () {
    	GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MIN_FILTER, aMinFilter);
    	GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MAG_FILTER, aMaxFilter);
	}

	public static GLTexture GetExplodeTex () {
		if (ExplodeTex == null) {
			ExplodeTex = new GLTexture(aContext, R.drawable.gltron_impact , GLES11.GL_CLAMP_TO_EDGE, GLES11.GL_CLAMP_TO_EDGE, true);			
			TexIdExplode = ExplodeTex.getTextureID();
		}
		return ExplodeTex;
	}

	public static int GetTexIdExplode (int index) {
		GetExplodeTex();
		return TexIdExplode;
	}

	public static int GetTexIdFloor () {
		return aTexIdFloor;
	}
	
	public static int GetTexIdWalls (int w) {
		int res = 0;
		switch (w) {
		case 0:
			res = aTexIdWall1;
			break;
		case 1:
			res = aTexIdWall2;
			break;
		case 2:
			res = aTexIdWall3;
			break;
		case 3:
			res = aTexIdWall4;
			break;
		default:
			res = aTexIdWall1;
			break;
		}
		return res;
	}

	public static int GetTexIdSkybox (int w) {
		int res = 0;
		switch (w) {
		case 0:
			res = aTexIdSkybox1;
			break;
		case 1:
			res = aTexIdSkybox2;
			break;
		case 2:
			res = aTexIdSkybox3;
			break;
		case 3:
			res = aTexIdSkybox4;
			break;
		case 4:
			res = aTexIdSkyboxB;
			break;
		case 5:
			res = aTexIdSkyboxT;
			break;
		default:
			res = aTexIdSkybox1;
			break;
		}
		return res;
	}

	public static Font GetFont () {
		if (aXenoFont == null) {
			aXenoFont = new Font(aContext, R.drawable.xenotron0, R.drawable.xenotron1);

			// Hard code these values for now allow loadable fonts later...
			aXenoFont.aTexwidth = 256;
			aXenoFont.aWidth = 32;
			aXenoFont.aLower = 32;
			aXenoFont.aUpper = 126;
			aXenoFont.aRatio = aXenoFont.aTexwidth / aXenoFont.aWidth;
			aXenoFont.aRatio2 = aXenoFont.aRatio * aXenoFont.aRatio;
			aXenoFont.aRatioCF = (float) aXenoFont.aWidth / (float) aXenoFont.aTexwidth;

		}
		return aXenoFont;
	}
	
	public boolean drawSplash() {
				
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);

		if (SplashTex == null)
			SplashTex = new GLTexture(aContext, R.drawable.hd_splash);

		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, SplashTex.getTextureID());

		float verts[] = {
				-1.0f, 1.0f, 0.0f,
				1.0f,  1.0f, 0.0f,
				-1.0f, -1.0f,0.0f,
				1.0f,  -1.0f, 0.0f
			};
			
		float texture[] = {
			0.0f, 1.0f, 
			1.0f, 1.0f,
			0.0f, 0.0f, 
			1.0f, 0.0f
		};


		// IN
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);

		FloatBuffer vertfb = ByteBufferManager.ReuseFloatBuffer(verts);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, vertfb);

		FloatBuffer texfb = ByteBufferManager.ReuseFloatBuffer(texture);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texfb);

		// DRAW THE BITMAP AS TEXTURE
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 4);
		
		// OUT
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		
		SplashTex = null;


		return true;
	}

	public void drawTile (int texID, float verts[]) {
		
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);

		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, texID);
/*
		float verts[] = {
				-1.0f,  1.0f, 0.0f,
				 1.0f,  1.0f, 0.0f,
				-1.0f, -1.0f, 0.0f,
				 1.0f, -1.0f, 0.0f
			};
*/			
		float texture[] = {
			0.0f, 1.0f, 
			1.0f, 1.0f,
			0.0f, 0.0f, 
			1.0f, 0.0f
		};
		FloatBuffer vertfb = ByteBufferManager.ReuseFloatBuffer(verts);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, vertfb);

		FloatBuffer texfb = ByteBufferManager.ReuseFloatBuffer(texture);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texfb);

		// DRAW THE BITMAP AS TEXTURE
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 4);

		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		
	}
	
}
