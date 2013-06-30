package com.applh.lightbike.fx;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import android.opengl.GLES11;
import android.util.FloatMath;

import com.applh.lightbike.Game.Player;

public class PowerUp {

	// BASIC SET TO DRAW RECTANGLE
	private static final byte Indices[] = {0,1,2, 0,2,3}; // TL>BL>BR > TL>BR>TR
	private static final ByteBuffer IndicesBuffer = ByteBufferManager.CreateByteBuffer(Indices);
	private static final int NbIndices = IndicesBuffer.limit();
	private static final float TexVertex[] = {
		0.0f, 1.0f,
		0.0f, 0.0f,
		1.0f, 0.0f,
		1.0f, 1.0f,
	};

	private static final FloatBuffer TexBuffer = ByteBufferManager.CreateFloatBuffer(TexVertex);

	private static FloatBuffer TmpFB = null;
	private static PowerUp aCheckpoint = null;
	
	private static float Dist2 = 100.0f;
	
	private float aX = 0.0f;
	private float aY = 0.0f;
	private float aZ = 0.0f;
	
	private float aDX0 = 0.0f;
	private float aDY0 = 0.0f;
	private float aHmax = 20.0f;
	
	private float aZ0 = 64.0f;
	private float aZmin = 8.0f;
	
	
//	private float aH = 20.0f;
	private float aW = 20.0f;
	private int aDir = 0; // 0 = BOTTOM / 1 = LEFT / 2 = TOP / 3 = RIGHT  
	private int aFrameTTL0 = 0;
	private float aFrameTTL = 0;
		
	public static void ReInit0 () {
		aCheckpoint = null;
		TmpFB = null;
	}

	public static void NewGame () {
		aCheckpoint = null;
	}

	public static void ManageRules (float gridSize, Player userP, int ratio) {
		Random rand = new Random();
		int isPowerUp = rand.nextInt(100);
				
		if (isPowerUp < ratio) {
			float x = gridSize * (.2f + .6f * rand.nextFloat());
			float y = gridSize * (.2f + .6f * rand.nextFloat());
			int dir = rand.nextInt(3);
			// CREATE POWERUP
			Create(x, y, dir);
		}

	}
	
	public static PowerUp Create (float x, float y, int dir) {
		
		if (aCheckpoint == null) {
			aCheckpoint = new PowerUp(x, y, dir);
		}

		return aCheckpoint;
	}

	public static int checkPlayer (Player player) {
		int res=0;
		
		float pX = player.getXpos();
		float pY = player.getYpos();

		if (aCheckpoint != null) {
			PowerUp elt = aCheckpoint;
			// trick to always turn the powerup in player direction
			elt.aDir = player.getDirection();
			
			if (elt.aZ > elt.aZmin) {
				// not active yet
				res=0;
			}
			else {
				float dist2= (pX-elt.aX)*(pX-elt.aX)+(pY-elt.aY)*(pY-elt.aY);
				if (dist2 < Dist2) {
					res=1;					
					elt.aFrameTTL=0;
				}					
			}
		}
		
		return res;
	}
	
	public static void DrawFrame () {
		if (aCheckpoint != null) {
			PowerUp e = aCheckpoint;
			if (e.aFrameTTL > 0) {
				e.drawFrameTex();
				e.aFrameTTL--;
			}
			else {
				aCheckpoint=null;
			}
		}		
	}
	
	public PowerUp (float x, float y, int dir) {
		init(x, y, dir);
	}

	public void init (float x, float y, int dir) {
		aX = x;
		aY = y;
		aZ = aZ0;
		
		aDir = dir;
		aW = 8;
		aHmax = 8;
		//aW = 16;
		//aHmax = 16;
		
//		aH = aHmax / 2;
		
		aFrameTTL0 = 3000;
		
		// 0 = BOTTOM / 1 = LEFT / 2 = TOP / 3 = RIGHT
		switch (aDir) {
		case 0:
			aDX0 = aW/2.0f;
			aDY0 = 0;
			break;
		case 1:
			aDX0 = 0;
			aDY0 = aW/2.0f;
			break;
		case 2:
			aDX0 = aW/2.0f;
			aDY0 = 0;
			break;
		case 3:
		default:
			aDX0 = 0;
			aDY0 = aW/2.0f;
			break;				
		}

		aFrameTTL = aFrameTTL0;

	}

	public void drawFrameTex () {

		// 0 = BOTTOM / 1 = LEFT / 2 = TOP / 3 = RIGHT
		switch (aDir) {
		case 0:
			aDX0 = aW/2.0f;
			aDY0 = 0;
			break;
		case 1:
			aDX0 = 0;
			aDY0 = aW/2.0f;
			break;
		case 2:
			aDX0 = aW/2.0f;
			aDY0 = 0;
			break;
		case 3:
		default:
			aDX0 = 0;
			aDY0 = aW/2.0f;
			break;				
		}
		
		float curH = aHmax;
		float alpha = 1.0f;
		
		float dX = aDX0;
		float dY = aDY0;

		if (aFrameTTL0 > 0) {
			float ratio = aFrameTTL / aFrameTTL0;
			// ratio <= 1.0f 
			ratio = 0.5f * ratio * ratio;
			
			float factor = FloatMath.cos((float) (0.5f * Math.PI * (1.0f - ratio)));
			factor=1.0f; // FIXME
			curH = aHmax * factor;
			dX = aDX0 * factor;
			dY = aDY0 * factor;
			
			// landing
			if (aZ > aZmin) aZ-=0.10f;
		}

		float tempVertexRect[] = {
				aX - dX, 
				aY - dY, 
				aZ + curH,

				aX - dX, 
				aY - dY, 
				aZ - curH,

				aX + dX, 
				aY + dY, 
				aZ - curH,

				aX + dX, 
				aY + dY, 
				aZ + curH,
			
			};
		
		// FIXME
		// MAKE BRIGHTER ?
		float red = 1.0f;
		float green = 1.0f;
		float blue = 1.0f;
		
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
		
		GLES11.glDisable(GLES11.GL_CULL_FACE);

		//GLES11.glDepthMask(true);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, TmpFB);

		//SetupGL.PushTexFilter(GLES11.GL_LINEAR_MIPMAP_NEAREST, GLES11.GL_NEAREST);
		// IN
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);	
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, SetupGL.GetTexIdPowerUp(iExp));		
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, TexBuffer);

		GLES11.glColor4f(red, green, blue, alpha);
		//Draw the Rectangle
		GLES11.glDrawElements(GLES11.GL_TRIANGLES, NbIndices, GLES11.GL_UNSIGNED_BYTE, IndicesBuffer);

		//Disable the client state before leaving
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		//SetupGL.PopTexFilter();
		GLES11.glEnable(GLES11.GL_CULL_FACE);
		GLES11.glDisable(GLES11.GL_BLEND);

	}

}
