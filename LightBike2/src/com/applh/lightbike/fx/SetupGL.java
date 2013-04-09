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
	
	// Textures
	private static GLTexture SplashTex = null;
	private static GLTexture ExplodeTex = null;
	private static GLTexture SkyBoxTex[] = null; 

	private static int TexIdExplode = -1;
	
// static long aFreeMem = 0;
	
//	private static final long aMemHD = 256;
//	private static final long aMemSD = 192;
//	private static final long aMemLD = 128;

//	private static final long aHeightHD = 512;
//	private static final long aHeightSD = 256;
//	private static final long aHeightLD = 128;

	private static Font aXenoFont = null;

	private static int aScreenW = 512;
	private static int aScreenH = 512;
	
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
		
		aScreenW = screenW;
		aScreenH = screenH;
		
		// reinit textures as context changed
		SplashTex = null;
		SkyBoxTex = null; 

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
	}
	
	public static void PrepareTexFloor () {
		aGSetFloor = aRand.nextInt(3);
		aTexIdFloor = aGSet.getTexIdFloor(aGSetFloor);
	}

	public static void PrepareTexWalls () {
		// FIXME
		int w = aRand.nextInt(3);
		aGSetWall1 = w*4;
		aGSetWall2 = w*4 +1;
		aGSetWall3 = w*4 +2;
		aGSetWall4 = w*4 +3;
		
		aTexIdWall1 = aGSet.getTexIdWall(aGSetWall1);
		aTexIdWall2 = aGSet.getTexIdWall(aGSetWall2);
		aTexIdWall3 = aGSet.getTexIdWall(aGSetWall3);
		aTexIdWall4 = aGSet.getTexIdWall(aGSetWall4);
	}	
	
	public static void PushTexFilter (int minFilter, int maxFilter) {
    	GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MIN_FILTER, minFilter);
    	GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MAG_FILTER, maxFilter);
	}
	
	public static void PopTexFilter () {
    	GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MIN_FILTER, aMinFilter);
    	GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MAG_FILTER, aMaxFilter);
	}
	
	public static GLTexture[] GetSkyBoxTex () {
		if (SkyBoxTex == null) {
			GLTexture tabTex[] = { 
				new GLTexture(aContext, R.drawable.skybox0),
				new GLTexture(aContext, R.drawable.skybox1),
				new GLTexture(aContext, R.drawable.skybox2),
				new GLTexture(aContext, R.drawable.skybox3),
				new GLTexture(aContext, R.drawable.skybox4),
				new GLTexture(aContext, R.drawable.skybox5) };
			SkyBoxTex = tabTex;
		}
		return SkyBoxTex;
	}
	

	public static GLTexture GetExplodeTex () {
		if (ExplodeTex == null) {
			ExplodeTex = new GLTexture(aContext, R.drawable.gltron_impact , GLES11.GL_CLAMP_TO_EDGE, GLES11.GL_CLAMP_TO_EDGE, true);			
		}
		return ExplodeTex;
	}

	public static int GetTexIdExplode (int index) {
		GetExplodeTex();
		TexIdExplode = ExplodeTex.getTextureID();
		return TexIdExplode;
	}

	public static int GetTexIdFloor () {
		return aTexIdFloor;
	}

	public static int GetTexIdSkyBox_DEV (int b) {
		int res = -1;
		if (SkyBoxTex == null) {
			GLTexture tabTex[] = { 
				new GLTexture(aContext, R.drawable.t0),
				new GLTexture(aContext, R.drawable.t1),
				new GLTexture(aContext, R.drawable.t2),
				new GLTexture(aContext, R.drawable.t3),
		 		new GLTexture(aContext, R.drawable.t4),
				new GLTexture(aContext, R.drawable.t5) };
			SkyBoxTex = tabTex;
		}
		if (SkyBoxTex != null) {
			GLTexture sbTex = SkyBoxTex[b];
			if (sbTex != null) 
				res = sbTex.getTextureID();
		}
		return res;
	}

	public static int GetTexIdSkyBox(int b) {
		int res = -1;
		if (SkyBoxTex == null) {
			GLTexture tabTex[] = { 
				new GLTexture(aContext, R.drawable.skybox_0, GLES11.GL_CLAMP_TO_EDGE, GLES11.GL_CLAMP_TO_EDGE, true),
				new GLTexture(aContext, R.drawable.skybox_1, GLES11.GL_CLAMP_TO_EDGE, GLES11.GL_CLAMP_TO_EDGE, true),
				new GLTexture(aContext, R.drawable.skybox_2, GLES11.GL_CLAMP_TO_EDGE, GLES11.GL_CLAMP_TO_EDGE, true),
				new GLTexture(aContext, R.drawable.skybox_3, GLES11.GL_CLAMP_TO_EDGE, GLES11.GL_CLAMP_TO_EDGE, true),
		 		new GLTexture(aContext, R.drawable.skybox_bottom, GLES11.GL_CLAMP_TO_EDGE, GLES11.GL_CLAMP_TO_EDGE, true),
				new GLTexture(aContext, R.drawable.skybox_top, GLES11.GL_CLAMP_TO_EDGE, GLES11.GL_CLAMP_TO_EDGE, true) };
			SkyBoxTex = tabTex;
		}
		if (SkyBoxTex != null) {
			GLTexture sbTex = SkyBoxTex[b];
			if (sbTex != null) 
				res = sbTex.getTextureID();
		}
		return res;
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
	
	public static Font GetFont () {
		if (aXenoFont == null) {
			aXenoFont = new Font(aContext, R.drawable.xenotron0, R.drawable.xenotron1);

			// Hard code these values for now allow loadable fonts later...
			aXenoFont._texwidth = 256;
			aXenoFont._width = 32;
			aXenoFont._lower = 32;
			aXenoFont._upper = 126;

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

}