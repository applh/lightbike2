/*
 * Author: APPLH.COM
 * Creation: 31/08/12
 * Modif:
 */

package com.applh.lightbike.fx;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.applh.lightbike.Video.Segment;

import android.opengl.GLES11;
import android.util.FloatMath;

public class TrackRenderer {

	private static FloatBuffer aFb = null;
	public float aAlphaMin = 1.0f;
	
	private static final byte Indices[] = {0,1,2, 0,2,3}; // TL>BL>BR > TL>BR>TR
	private static final ByteBuffer IndicesBuffer = ByteBufferManager.CreateByteBuffer(Indices);
	private static final int NbIndices = IndicesBuffer.limit();
	

/*
	private static FloatBuffer Trailtopfb = null;
	private static final float Trail_top[] = {
		1.0f, 1.0f, 1.0f, .7f,
		1.0f, 1.0f, 1.0f, .7f,
		1.0f, 1.0f, 1.0f, .7f
		};
*/
	public TrackRenderer ()
	{
		aFb = null;
		//Trailtopfb = null;
	}

	
	public void drawTracks (Segment segs[], int trail_offset, float trackHeight, float[] color)
	{		
		int segOffset;

//		if (Trailtopfb == null)
//			Trailtopfb = ByteBufferManager.CreateFloatBuffer(Trail_top);
		
		//GLES11.glEnable(GLES11.GL_LINE_SMOOTH);
		//GLES11.glDepthMask(true);
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		GLES11.glDisable(GLES11.GL_CULL_FACE);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		float curH = trackHeight;
		//float stepH = 0.1f;
		float nextH = curH;
		for (segOffset = 0; segOffset <= trail_offset; segOffset++) {
			// Dont change alpha based on dist yet
			//GLES11.glColorPointer(4, GLES11.GL_FLOAT, 0, Trailtopfb);
			//GLES11.glColor4f(1.0f, 1.0f, 1.0f, 0.4f);
			float red = color[0];
			float green = color[1];
			float blue = color[2];
			float alpha = 1.0f - 0.4f * segOffset / (trail_offset + 1.0f);
			
			if (alpha < aAlphaMin) alpha = aAlphaMin;
			
			GLES11.glColor4f(red, green, blue, alpha);

			// last segment
			if (segOffset == trail_offset) {
				nextH = 0.5f * (trackHeight + nextH);
			}
			
			float tempVertexSquare[] = {
				segs[segOffset].vStart.v[0], 
				segs[segOffset].vStart.v[1], 
				curH,

				segs[segOffset].vStart.v[0], 
				segs[segOffset].vStart.v[1], 
				0,

				segs[segOffset].vStart.v[0] + segs[segOffset].vDirection.v[0],
				segs[segOffset].vStart.v[1] + segs[segOffset].vDirection.v[1],
				0,

				segs[segOffset].vStart.v[0] + segs[segOffset].vDirection.v[0],
				segs[segOffset].vStart.v[1] + segs[segOffset].vDirection.v[1],
				nextH,
			
			};
			// raise the wall :P
			curH = nextH;
			nextH = trackHeight + FloatMath.sqrt(0.25f*segOffset);

			if (aFb == null) {
				aFb = ByteBufferManager.CreateFloatBuffer(tempVertexSquare);
			}
			else {
			    aFb.rewind();
			    aFb.put(tempVertexSquare);
			    aFb.rewind();
			}
			//GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, aFb);
			//GLES11.glDrawArrays(GLES11.GL_LINES, 0, 2);

			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, aFb);
			//Draw the vertices as s, based on the Index Buffer information
			GLES11.glDrawElements(GLES11.GL_TRIANGLES, NbIndices, GLES11.GL_UNSIGNED_BYTE, IndicesBuffer);
			
		}
		//Disable the client state before leaving
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		// OUT
		GLES11.glEnable(GLES11.GL_CULL_FACE);
		GLES11.glDisable(GLES11.GL_BLEND);

	}

}
