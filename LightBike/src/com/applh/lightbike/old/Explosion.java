/*
 * Copyright © 2012 Iain Churcher
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

package com.applh.lightbike.old;

import java.nio.FloatBuffer;

import com.applh.lightbike.Video.GLTexture;
import com.applh.lightbike.fx.ByteBufferManager;
import com.applh.lightbike.fx.SetupGL;
import com.applh.lightbike.matrix.Vector3;

//import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES11;

public class Explosion {

	private float Radius;
	
	private static final float IMPACT_RADIUS_DELTA = 0.025f;
	private static final float IMPACT_MAX_RADIUS = 25.0f;
	
	// Shockwave behaviour constants 
	private static final float SHOCKWAVE_MIN_RADIUS  = 0.0f;
	private static final float SHOCKWAVE_MAX_RADIUS = 45.0f;
	private static final float SHOCKWAVE_WIDTH = 0.2f;
	private static final float SHOCKWAVE_SPACING = 6.0f;
	private static final float SHOCKWAVE_SPEED = 1.2f; // relative to impact radius delta
	private static final int SHOCKWAVE_SEGMENTS = 25;
	private static final int NUM_SHOCKWAVES = 3;

	// Glow constants
	private static final float GLOW_START_OPACITY = 1.2f;
	private static final float GLOW_INTENSITY = 1.0f;
	
	// Spire constants
	private static final float SPIRE_WIDTH = 0.40f;
	private static final int NUM_SPIRES = 21;
	private static final Vector3 ZUnit = new Vector3(0.0f, 0.0f, 1.0f);
	private static final float ImpactVertex[] = {
			-1.0f, -1.0f, 0.0f,
			1.0f, -1.0f, 0.0f,
			1.0f, 1.0f, 0.0f,
			1.0f, 1.0f, 0.0f,
			-1.0f, 1.0f, 0.0f,
			-1.0f, -1.0f,0.0f
	};
	
	private static final float TexVertex[] = {
			0.0f, 0.0f,
			1.0f, 0.0f,
			1.0f,1.0f,
			1.0f,1.0f,
			0.0f, 1.0f,
			0.0f, 0.0f
	};

	private static final Vector3 Spires[] = {
			 new Vector3(1.00f, 0.20f, 0.00f),
			 new Vector3(0.80f, 0.25f, 0.00f),
			 new Vector3(0.90f, 0.50f, 0.00f),
			 new Vector3(0.70f, 0.50f, 0.00f),
			 new Vector3(0.52f, 0.45f, 0.00f),
			 new Vector3(0.65f, 0.75f, 0.00f),
			 new Vector3(0.42f, 0.68f, 0.00f),
			 new Vector3(0.40f, 1.02f, 0.00f),
			 new Vector3(0.20f, 0.90f, 0.00f),
			 new Vector3(0.08f, 0.65f, 0.00f),
			 new Vector3(0.00f, 1.00f, 0.00f),
			 new Vector3(-0.08f, 0.65f, 0.00f),
			 new Vector3(-0.20f, 0.90f, 0.00f),
			 new Vector3(-0.40f, 1.02f, 0.00f),
			 new Vector3(-0.42f, 0.68f, 0.00f),
			 new Vector3(-0.65f, 0.75f, 0.00f),
			 new Vector3(-0.52f, 0.45f, 0.00f),
			 new Vector3(-0.70f, 0.50f, 0.00f),
			 new Vector3(-0.90f, 0.50f, 0.00f),
			 new Vector3(-0.80f, 0.30f, 0.00f),
			 new Vector3(-1.00f, 0.20f, 0.00f)
	};

	private static final float TriList[] = new float[3*3];
	private static final float WaveVertex[] = new float[(3*(2*(SHOCKWAVE_SEGMENTS + 1)))];

	
	public Explosion (float radius, long time)
	{
		Radius = radius;
	}
	
	public float getRadius()
	{
		return Radius;
	}
	
	public boolean runExplode()
	{
		boolean retVal = true;
		if (Radius > IMPACT_MAX_RADIUS) {
			retVal = false;
		}
		return retVal;
	}
	
	public void Draw (long GameDeltaTime)
	{
		GLES11.glDisable(GLES11.GL_LIGHTING);
		GLES11.glPushMatrix();
		GLES11.glRotatef(90,90,0,1);
		GLES11.glTranslatef(0.0f, -0.5f, -0.5f);
		GLES11.glColor4f(0.68f, 0.0f, 0.0f, 1.0f);
				
		drawShockwaves();
		
		if (Radius < IMPACT_MAX_RADIUS) {
			drawImpactGlow();
			drawSpires();
		}
		
		Radius += (GameDeltaTime * IMPACT_RADIUS_DELTA);
		
		GLES11.glPopMatrix();
		GLES11.glEnable(GLES11.GL_LIGHTING);

	}
	
	private void drawSpires ()
	{
		int i;
		
		Vector3 right,left;
		
		
		FloatBuffer SpireBuffer;
		
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 0.0f);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		//GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE);
		
		for (i=0; i < NUM_SPIRES;  i++) {
			right = Spires[i].Cross(ZUnit);
			right.Normalise();
			right.Mul(SPIRE_WIDTH);
			
			left = ZUnit.Cross(Spires[i]);
			left.Normalise();
			left.Mul(SPIRE_WIDTH);
			
			TriList[0] = right.v[0];
			TriList[1] = right.v[1];
			TriList[2] = right.v[2];
			TriList[3] = (Radius * Spires[i].v[0]);
			TriList[4] = (Radius * Spires[i].v[1]);
			TriList[5] = 0.0f;
			TriList[6] = left.v[0];
			TriList[7] = left.v[1];
			TriList[8] = left.v[2];
			
			SpireBuffer = ByteBufferManager.ReuseFloatBuffer(TriList);
			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, SpireBuffer);
			GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 3);
		}
		
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		//GLES11.glDisable(GLES11.GL_BLEND);
		
	}
	
	private void drawImpactGlow ()
	{
		float opacity;
		FloatBuffer ImpactBuffer;
		FloatBuffer TexBuffer;
		
		opacity = GLOW_START_OPACITY - (Radius / IMPACT_MAX_RADIUS);
		
		GLES11.glPushMatrix();
		GLES11.glScalef(Radius, Radius, 1.0f);

		//GLES11.glEnable(GLES11.GL_BLEND);
		//GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE);
		GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE_MINUS_SRC_ALPHA);
		
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		GLTexture explodeTex = SetupGL.GetExplodeTex();
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, explodeTex.getTextureID());
		
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		
		GLES11.glColor4f(GLOW_INTENSITY, GLOW_INTENSITY, GLOW_INTENSITY, opacity);
		GLES11.glDepthMask(false);
		
		ImpactBuffer = ByteBufferManager.ReuseFloatBuffer(ImpactVertex);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, ImpactBuffer);
		TexBuffer = ByteBufferManager.ReuseFloatBuffer(TexVertex);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, TexBuffer);
		
		//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLES ,0, 6);
		
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glDepthMask(true);
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		//GLES11.glDisable(GLES11.GL_BLEND);
		GLES11.glPopMatrix();
	}
	
	private void drawShockwaves ()
	{
		int waves;
		float radius = (Radius * SHOCKWAVE_SPEED);
		
		GLES11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		
		for (waves=0; waves<NUM_SHOCKWAVES; waves++) {
			if(radius > SHOCKWAVE_MIN_RADIUS && radius < SHOCKWAVE_MAX_RADIUS) {
				drawWave(radius);
			}
			radius -= SHOCKWAVE_SPACING;
		}
	}
	
	private void drawWave (float adj_radius)
	{
		int i,j,vertex;
		double angle;
		double delta_radius = SHOCKWAVE_WIDTH / SHOCKWAVE_SEGMENTS;
		double delta_angle = (180.0 / SHOCKWAVE_SEGMENTS) * (Math.PI / 180);
		double start_angle = (270.0 * (Math.PI / 180));
		int NumberOfIndices = (2*(SHOCKWAVE_SEGMENTS + 1));
		
		FloatBuffer WaveBuffer;
		
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);

		for(i=0; i < SHOCKWAVE_SEGMENTS; i++)
		{
			angle = start_angle;
			vertex = 0;
			for(j=0; j <= SHOCKWAVE_SEGMENTS; j++)
			{
				WaveVertex[vertex] = (float)((adj_radius + delta_radius) * Math.sin(angle));
				vertex++;
				WaveVertex[vertex] = (float)((adj_radius + delta_radius) * Math.cos(angle));
				vertex++;
				WaveVertex[vertex] = 0.0f;
				vertex++;
				
				WaveVertex[vertex] = (float)(adj_radius * Math.sin(angle));
				vertex++;
				WaveVertex[vertex] = (float)(adj_radius * Math.cos(angle));
				vertex++;
				WaveVertex[vertex] = 0.0f;
				vertex++;
				
				angle += delta_angle;
			}
			
			WaveBuffer = ByteBufferManager.ReuseFloatBuffer(WaveVertex);
			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, WaveBuffer);
			GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, NumberOfIndices);
			adj_radius += delta_radius;
		}
		
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
	}
}
