/*
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

package com.applh.lightbike.Video;

import java.nio.FloatBuffer;

import com.applh.lightbike.fx.ByteBufferManager;

//import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES11;

import android.content.Context;

public class Font {

	//private int _nTextures;
	public int aTexwidth;
	public int aWidth;
	public int aLower;
	public int aUpper;

	public int aRatio;
	public int aRatio2;
	public float aRatioCF;
	
	private static float TmpVertex[] = new float[6 * 2];
	private static float TmpTextre[] = new float[6 * 2];
		
	// Hard code to only 2 textures as thats
	// what both fonts use in the default art pack
	private GLTexture Tex1;
	private GLTexture Tex2;
	private int aTexId1;
	private int aTexId2;
	
	
	public Font (Context context, int tex1, int tex2)
	{
		Tex1 = new GLTexture(context, tex1, GLES11.GL_CLAMP_TO_EDGE, GLES11.GL_CLAMP_TO_EDGE, false);
		Tex2 = new GLTexture(context, tex2, GLES11.GL_CLAMP_TO_EDGE, GLES11.GL_CLAMP_TO_EDGE, false); 
		
		aTexId1 = Tex1.getTextureID();
		aTexId2 = Tex2.getTextureID();
		//_nTextures = 2;
	}
	
	public void drawText (int x, int y, int size, String text)
	{
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		
		GLES11.glPushMatrix();
		GLES11.glTranslatef(x, y, 0);
		GLES11.glScalef(size, size, size);
		
		renderString(text);
		
		GLES11.glPopMatrix();
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
	}
	
	private void renderString (String str)
	{
		float cx,cy;
		int i, index;
		int tex;
		int bound = -1;
		
		//float vertex[] = new float[6 * 2];
		//float textre[] = new float[6 * 2];
		FloatBuffer vertexBuff = null;
		FloatBuffer texBuff = null;
		
		// skip color bit is it used?
		
		for (i = 0; i < str.length(); i++) {
			index = str.charAt(i) - aLower + 1;
			
			if (index >= aUpper)
				return; // index out of bounds
			
			tex = index / aRatio2;
			
			// Bind texture
			if (tex != bound) {
				if(tex == 0)
					GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, aTexId1);
				else
					GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, aTexId2);
				
				GLES11.glTexEnvf(GLES11.GL_TEXTURE_ENV, GLES11.GL_TEXTURE_ENV_MODE, GLES11.GL_MODULATE);
				bound = tex;
			}
			
			// find texture coordinates
			index = index % aRatio2;
			cx = (float)(index % aRatio) / (float)aRatio;
			cy = (float)(index / aRatio) / (float)aRatio;
			
			// draw character
			TmpTextre[0] = cx;
			TmpTextre[1] = 1.0f-cy-aRatioCF;
			TmpVertex[0] = (float)i;
			TmpVertex[1] = 0.0f;
			
			TmpTextre[2] = cx+aRatioCF;
			TmpTextre[3] = 1.0f-cy-aRatioCF;
			TmpVertex[2] = i + 1.0f;
			TmpVertex[3] = 0.0f;
			
			TmpTextre[4] = cx + aRatioCF;
			TmpTextre[5] = 1.0f - cy;
			TmpVertex[4] = i + 1.0f;
			TmpVertex[5] = 1.0f;
			
			TmpTextre[6] = cx + aRatioCF;
			TmpTextre[7] = 1.0f - cy;
			TmpVertex[6] = i + 1.0f;
			TmpVertex[7] = 1.0f;
			
			TmpTextre[8] = cx;
			TmpTextre[9] = 1.0f - cy;
			TmpVertex[8] = i;
			TmpVertex[9] = 1.0f;
			
			TmpTextre[10] = cx;
			TmpTextre[11] = 1.0f - cy - aRatioCF;
			TmpVertex[10] = i;
			TmpVertex[11] = 0.0f;
			
			vertexBuff = ByteBufferManager.ReuseFloatBuffer(TmpVertex);
			texBuff = ByteBufferManager.ReuseFloatBuffer(TmpTextre);
			
			GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, vertexBuff);
			GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texBuff);
			GLES11.glDrawArrays(GLES11.GL_TRIANGLES, 0, 6);
			
		}
	}
}
