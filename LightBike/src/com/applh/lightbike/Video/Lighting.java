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

package com.applh.lightbike.Video;

import java.nio.FloatBuffer;

import com.applh.lightbike.fx.ByteBufferManager;

//import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES11;

public class Lighting {

	
	private static final float white[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	private static final float gray66[] = { 0.66f, 0.66f, 0.66f, 1.0f };
	private static final float gray10[] = { 0.1f, 0.1f, 0.1f, 1.0f };
	private static final float black[] = { 0.0f, 0.0f, 0.0f, 1.0f };
	
	private static final float posWorld0[] = {1.0f, 0.8f, 0.0f, 0.0f};
	private static final float posWorld1[] = {-1.0f, -0.8f, 0.0f, 0.0f};
	private static final float posCycles[] = {0.0f, 0.0f, 0.0f, 1.0f};
	
	public enum LightType {
		E_WORLD_LIGHTS,
		E_CYCLE_LIGHTS,
		E_RECOGNISER_LIGHTS
	}

	private static FloatBuffer white_fb = ByteBufferManager.CreateFloatBuffer(white);
	private static FloatBuffer gray66_fb = ByteBufferManager.CreateFloatBuffer(gray66);
	private static FloatBuffer gray10_fb = ByteBufferManager.CreateFloatBuffer(gray10);
	private static FloatBuffer black_fb = ByteBufferManager.CreateFloatBuffer(black);
	private static FloatBuffer posWorld0_fb = ByteBufferManager.CreateFloatBuffer(posWorld0);
	private static FloatBuffer posWorld1_fb = ByteBufferManager.CreateFloatBuffer(posWorld1);
	private static FloatBuffer posCycles_fb = ByteBufferManager.CreateFloatBuffer(posCycles);

	public Lighting() {
		
	}
	
	public static void SetupLights (LightType lightType)
	{
		
		// Turn off global ambient lighting
		GLES11.glLightModelfv(GLES11.GL_LIGHT_MODEL_AMBIENT, black_fb);
		GLES11.glLightModelf(GLES11.GL_LIGHT_MODEL_TWO_SIDE, 1.0f);
		
		if(lightType == LightType.E_WORLD_LIGHTS) {
			//GLES11.glEnable(GLES11.GL_LIGHTING);
			GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
			GLES11.glPushMatrix();
			
			GLES11.glEnable(GLES11.GL_LIGHT0);
			GLES11.glLightfv(GLES11.GL_LIGHT0, GLES11.GL_POSITION, posWorld0_fb);
			GLES11.glLightfv(GLES11.GL_LIGHT0, GLES11.GL_AMBIENT, gray10_fb);
			GLES11.glLightfv(GLES11.GL_LIGHT0, GLES11.GL_SPECULAR, white_fb);
			GLES11.glLightfv(GLES11.GL_LIGHT0, GLES11.GL_DIFFUSE, white_fb);
			
			GLES11.glEnable(GLES11.GL_LIGHT1);
			GLES11.glLightfv(GLES11.GL_LIGHT1, GLES11.GL_POSITION, posWorld1_fb);
			GLES11.glLightfv(GLES11.GL_LIGHT1, GLES11.GL_AMBIENT, black_fb);
			GLES11.glLightfv(GLES11.GL_LIGHT1, GLES11.GL_SPECULAR, gray66_fb);
			GLES11.glLightfv(GLES11.GL_LIGHT1, GLES11.GL_DIFFUSE, gray66_fb);
			GLES11.glPopMatrix();
			
		} else {
			//GLES11.glEnable(GLES11.GL_LIGHTING);
			GLES11.glEnable(GLES11.GL_LIGHT0);
			
			GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
			GLES11.glPushMatrix();
			GLES11.glLoadIdentity();
			
			GLES11.glLightfv(GLES11.GL_LIGHT0, GLES11.GL_POSITION, posCycles_fb);
			GLES11.glLightfv(GLES11.GL_LIGHT0, GLES11.GL_AMBIENT, gray10_fb);
			GLES11.glLightfv(GLES11.GL_LIGHT0, GLES11.GL_SPECULAR, white_fb);
			GLES11.glLightfv(GLES11.GL_LIGHT0, GLES11.GL_DIFFUSE, white_fb);
			
			GLES11.glDisable(GLES11.GL_LIGHT1);
			
			GLES11.glPopMatrix();
		}
			
	}
	
}
