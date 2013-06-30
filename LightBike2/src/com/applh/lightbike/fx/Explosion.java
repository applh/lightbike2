package com.applh.lightbike.fx;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import android.opengl.GLES11;
import android.util.FloatMath;

import com.applh.lightbike.Game.Player;

public class Explosion {

	// BASIC SET TO DRAW RECTANGLE
	private static final byte Indices[] = {0,1,2, 0,2,3}; // TL>BL>BR > TL>BR>TR
	private static final ByteBuffer IndicesBuffer = ByteBufferManager.CreateByteBuffer(Indices);
	private static final int NbIndices = IndicesBuffer.limit();
	private static final float TexVertex[] = {
		1.0f, 0.0f,
		0.0f, 0.0f,
		0.0f, 1.0f,
		1.0f, 1.0f,
	};

	private static final FloatBuffer TexBuffer = ByteBufferManager.CreateFloatBuffer(TexVertex);

	private static FloatBuffer TmpFB = null;
	private static int NB_MAX_EXPLOSION;
	private static int CurExp = 0;
	private static int CurExpCount = 0;
	private static Explosion[] ListExp = null;
	
	private float aX = 0.0f;
	private float aY = 0.0f;
	private float aDX0 = 0.0f;
	private float aDY0 = 0.0f;
	private float aHmax = 20.0f;
//	private float aH = 20.0f;
	private float aW = 20.0f;
	private int aDir = 0; // 0 = BOTTOM / 1 = LEFT / 2 = TOP / 3 = RIGHT  
	private int aFrameTTL0 = 0;
	private float aFrameTTL = 0;
	
	private float[] aColor;
	
	public static void ReInit0 () {
		ListExp = null;
		TmpFB = null;
		NB_MAX_EXPLOSION = 100;
		CurExp = 0;
		CurExpCount = 0;
	}

	public static void NewGame () {
		CurExp = 0;
		CurExpCount = 0;
		if (ListExp != null) {
			for (int i=0; i< ListExp.length; i++)
				ListExp[i] = null;
		}
	}

	public static Explosion Create (Player player) {
		if (ListExp == null) {
			ListExp = new Explosion[NB_MAX_EXPLOSION];
			for (int i=0; i< ListExp.length; i++)
				ListExp[i] = null;
		}
		
		Explosion res = null;
		
		if (CurExp < ListExp.length) {
			// ADD NEW EXPLOSION
			res = ListExp[CurExp];
			if (res == null) { 
				res = new Explosion(player);
				ListExp[CurExp] = res;
			}
			else { 
				res.init(player);
			}
			
			CurExp++;
			CurExpCount = CurExp;
			// recycle
			CurExp = CurExp % ListExp.length;
		}
		return res;
	}

	public static void DrawFrame () {
		for (int i=0; i< CurExpCount; i++) {
			Explosion e = ListExp[i];
			if (e != null) {
				if (e.aFrameTTL > 0) {
					e.drawFrameTex();
					e.aFrameTTL--;
				}
				else {
					ListExp[i] = null;
				}
			}
		}
	}
	
	public Explosion (Player player) {
		init(player);
	}

	public void init (Player player) {
		aX = player.getXpos();
		aY = player.getYpos();
		aDir = player.getDirection();
		aW = 16 + player.getSpeed();
		aHmax = 16 + player.getSpeed();
		//aW = 16;
		//aHmax = 16;
		aColor = player.mPlayerColourDiffuse;
		
//		aH = aHmax / 2;
		
		aFrameTTL0 = Math.round(player.getSpeed() * 16);
		
		// 0 = BOTTOM / 1 = LEFT / 2 = TOP / 3 = RIGHT
		switch (aDir) {
		case 0:
			aDX0 = aW/2.0f;
			aDY0 = 0;
			aY += 0.1f; // move back the explosion before wall
			break;
		case 1:
			aDX0 = 0;
			aDY0 = aW/2.0f;
			aX += 0.1f; // move back the explosion before wall
			break;
		case 2:
			aDX0 = aW/2.0f;
			aDY0 = 0;
			aY += -0.1f; // move back the explosion before wall
			break;
		case 3:
		default:
			aDX0 = 0;
			aDY0 = aW/2.0f;
			aX += -0.1f; // move back the explosion before wall
			break;
				
		}

		aFrameTTL = aFrameTTL0;

	}

	public void drawFrameTex () {
		float curH = aHmax;
		float alpha = 1.0f;
		
		float dX = aDX0;
		float dY = aDY0;

		if (aFrameTTL0 > 0) {
			float ratio = aFrameTTL / aFrameTTL0;
			// ratio <= 1.0f 
			ratio = 0.5f * ratio * ratio;
			
			float factor = FloatMath.cos((float) (0.5f * Math.PI * (1.0f - ratio)));
			curH = aHmax * factor;
			dX = aDX0 * factor;
			dY = aDY0 * factor;
			
			//curH = aHmax * (1.0f - ratio);
			//dX = dX  * (1.0f - ratio);
			//dY = dY * (1.0f - ratio);
			//alpha = 1.0f - (aFrameTTL / aFrameTTL0);
			//alpha = 0.7f + 0.3f * aFrameTTL / aFrameTTL0;
		}

		float tempVertexRect[] = {
				aX - dX, 
				aY - dY, 
				curH,

				aX - dX, 
				aY - dY, 
				-curH,

				aX + dX, 
				aY + dY, 
				-curH,

				aX + dX, 
				aY + dY, 
				curH,
			
			};
		
		float red = aColor[0];
		float green = aColor[1];
		float blue = aColor[2];
		// FIXME
		// MAKE BRIGHTER ?
//		red = 0.6f + 0.4f * red * red;
//		green = 0.6f + 0.4f * green * green;
//		blue = 0.6f + 0.4f * blue * blue;
		red = 1.0f;
		green = 1.0f;
		blue = 1.0f;
		
		if (TmpFB == null) {
			TmpFB = ByteBufferManager.CreateFloatBuffer(tempVertexRect);
		}
		else {
		    TmpFB.rewind();
		    TmpFB.put(tempVertexRect);
		    TmpFB.rewind();
		}

		// multi explosion texture
		Random rand = new Random();
		int iExp = rand.nextInt(10);
		
		//GLES11.glDisable(GLES11.GL_CULL_FACE);

		//GLES11.glDepthMask(true);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, TmpFB);

		//SetupGL.PushTexFilter(GLES11.GL_LINEAR_MIPMAP_NEAREST, GLES11.GL_NEAREST);
		// IN
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);	
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, SetupGL.GetTexIdExplode(iExp));		
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, TexBuffer);

		GLES11.glColor4f(red, green, blue, alpha);
		//Draw the Rectangle
		GLES11.glDrawElements(GLES11.GL_TRIANGLES, NbIndices, GLES11.GL_UNSIGNED_BYTE, IndicesBuffer);

		//Disable the client state before leaving
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		//SetupGL.PopTexFilter();
		//GLES11.glEnable(GLES11.GL_CULL_FACE);
		GLES11.glDisable(GLES11.GL_BLEND);

	}

}
