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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

//import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES11;

import android.content.Context;

import com.applh.lightbike.fx.ByteBufferManager;
import com.applh.lightbike.fx.SetupGL;

public class WorldGraphics {

	private float aGridSize;
	private FloatBuffer _WallVertexBuffer[] = new FloatBuffer[4];
	private static ByteBuffer _IndicesBuffer;
	private static FloatBuffer _TexBuffer;
	private static FloatBuffer _FloorTexBuffer;
	private static FloatBuffer _SkyBoxVertexBuffers[] = new FloatBuffer[6];
	private int _NumOfIndices;
//	private float _wallH =  48.0f; // Wall height
	private float aGridWallH =  180.0f; // Wall height


	// TMP TABLES
	private static float TmpRawVertices[] = new float[4 * 3];

	public WorldGraphics (Context context, float grid_size)
	{
		// Save Grid Size
		aGridSize = grid_size;
		aGridWallH = aGridSize / 4;
		initGridWalls();
		initSkyBox();
		
		// Setup standard square index and tex buffers
		// Define indices and tex coords
		//float t = grid_size / 240.0f;
		//float t = grid_size / 480.0f;
		float t = 1.0f;
		// USED BY WALLS AND SKYBOX
		byte indices[] = {0,1,3, 0,3,2}; // TL>BL>BR>TL>BR>TR
		//float texCoords[] = {0.0f, 0.0f,  0.0f, 1.0f,  1.0f, 0.0f,  1.0f, 1.0f};
  		float texCoords[] = { 
				t,    1.0f, 
				0.0f, 1.0f,  
				t,    0.0f,  
				0.0f, 0.0f,
		};
		
		float l = grid_size / 4;
		t = l / 12; // square is about the same size as bike
		float florTexCoords [] = {0.0f, 0.0f, t, 0.0f, 0.0f, t, t, t};

		_FloorTexBuffer = ByteBufferManager.CreateFloatBuffer(florTexCoords);
		_TexBuffer = ByteBufferManager.CreateFloatBuffer(texCoords);

		_IndicesBuffer = ByteBufferManager.CreateByteBuffer(indices);
		_NumOfIndices = indices.length;
		
	}
	private void initSkyBox ()
	{
		float d = (float)aGridSize * 2.0f;
		//float h = _wallH;
		float xmin = 0 - 0.25f * d;
		float xmax = xmin + d;
		float ymin = 0 - 0.25f * d;
		float ymax = ymin + d;
		float zmin = 0 - 0.05f * d;
		float zmax = zmin + 0.5f * d;
		
		// TL>BL>BR>TL>BR>TR
		float sides[][] = {
		  { 
			xmax, ymin, zmax,    
			xmax, ymax, zmax,  
			xmax, ymin, zmin,    
			xmax, ymax, zmin,    
		  }, /* 0 */	  
		  { 
			xmin, ymin, zmax,    
			xmax, ymin, zmax, 
			xmin, ymin, zmin,   
			xmax, ymin, zmin,  
		  }, /* 1 */
		  { 
			xmin, ymax, zmax,    
			xmin, ymin, zmax, 
			xmin, ymax, zmin,    
			xmin, ymin, zmin, 
		  }, /* 2 */ 
		  { 
			xmax, ymax, zmax,    
			xmin, ymax, zmax,  
			xmax, ymax, zmin,     
			xmin, ymax, zmin,   
		  }, /* 3 */
		  { 
			xmin, ymin, zmin,    
			xmax, ymin, zmin,   
			xmin, ymax, zmin,  
			xmax, ymax, zmin, 
		  }, /* bottom */
		  { 
			xmin, ymin, zmax,    
			xmax, ymin, zmax, 
			xmin, ymax, zmax,     
			xmax, ymax, zmax,    
		  }, /* top */
		};

		for (int i=0; i<6; i++) {
			_SkyBoxVertexBuffers[i] = ByteBufferManager.CreateFloatBuffer(sides[i]);
		}
	}
		
	private void initSkyBox_SAV ()
	{
		float d = (float)aGridSize * 2.0f;
		//float h = _wallH;
		
		// TL>BL>BR>TL>BR>TR
		float sides[][] = {
		  { 
			d, -d,  d,    
			d,  d,  d,  
			d, -d, -d,    
		  	d,  d, -d,    
		  }, /* 0 */	  
		  { 
			-d, -d,  d,    
		  	 d, -d,  d, 
			-d, -d, -d,   
		  	 d, -d, -d,  
		  }, /* 1 */
		  { 
			-d,  d,  d,    
			-d, -d,  d, 
			-d,  d, -d,    
			-d, -d, -d, 
		  }, /* 2 */ 
		  { 
			 d,  d,  d,    
			-d,  d,  d,  
			 d,  d, -d,     
			-d,  d, -d,   
		  }, /* 3 */
		  { 
			-d, -d, -d,    
			 d, -d, -d,   
			-d,  d, -d,  
			 d,  d, -d, 
		  }, /* bottom */
		  { 
			-d, -d,  d,    
			 d, -d,  d, 
			-d,  d,  d,     
			 d,  d,  d,    
		  }, /* top */
		};

		for (int i=0; i<6; i++) {
			_SkyBoxVertexBuffers[i] = ByteBufferManager.CreateFloatBuffer(sides[i]);
		}
	}

	
	private void initGridWalls ()
	{
		// Setup Wall buffers
		//float t = _grid_size / 240.0f;
		float h = aGridWallH;
		
		float WallVertices[][] = 
		{
			{ // Wall 4
				0.0f,     aGridSize, h,
				0.0f,     0.0f,      h,
				0.0f,     aGridSize, 0.0f,
				0.0f,     0.0f,      0.0f
			},
			{ // Wall 3
				aGridSize, aGridSize,  h,
				0.0f,      aGridSize,  h,
				aGridSize, aGridSize,  0.0f,
				0.0f,      aGridSize,  0.0f
			},
			{ // Wall 2
				aGridSize, 0.0f,       h,
				aGridSize, aGridSize,  h,
				aGridSize, 0.0f,       0.0f,
				aGridSize, aGridSize,  0.0f
			},
			{ // Wall 1
				0.0f,       0.0f, h,
				aGridSize,  0.0f, h,
				0.0f,       0.0f, 0.0f,
				aGridSize,  0.0f, 0.0f
			},
		};
		
		_WallVertexBuffer[0] = ByteBufferManager.CreateFloatBuffer(WallVertices[0]);
		_WallVertexBuffer[1] = ByteBufferManager.CreateFloatBuffer(WallVertices[1]);
		_WallVertexBuffer[2] = ByteBufferManager.CreateFloatBuffer(WallVertices[2]);
		_WallVertexBuffer[3] = ByteBufferManager.CreateFloatBuffer(WallVertices[3]);
		
	}
	
	public void drawWallsBlend ()
	{
		GLES11.glFrontFace(GLES11.GL_CCW);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		// CAMERA CAN BE BEHIND WALLS
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		GLES11.glEnable(GLES11.GL_CULL_FACE);
		//GLES11.glEnable(GLES11.GL_TEXTURE_WRAP_S);
	    //GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_WRAP_S, GLES11.GL_CLAMP_TO_EDGE);

		for (int Walls=0; Walls<4; Walls++) {
			
			GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, SetupGL.GetTexIdWalls(Walls));
			GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
			GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, _WallVertexBuffer[Walls]);
			GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, _TexBuffer);
			//Draw the vertices as s, based on the Index Buffer information
			GLES11.glDrawElements(GLES11.GL_TRIANGLES, _NumOfIndices, GLES11.GL_UNSIGNED_BYTE, _IndicesBuffer);
			//Disable the client state before leaving
			GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
			GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		}
		
		GLES11.glDisable(GLES11.GL_CULL_FACE);
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		
	}

	public void drawFloorTexturedBlend ()
	{
		int i,j,l;
		
		//GLES11.glShadeModel(GLES11.GL_FLAT);
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, SetupGL.GetTexIdFloor());
		
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		l = (int)(aGridSize / 4);

		for (i = 0; i < (int)aGridSize; i += l) {
			for (j = 0; j < (int)aGridSize; j += l) {
				
				// BETTER MEMORY FOR GC
				// USE STATIC ATTRIBUTE
				TmpRawVertices[0] = (float)i;
				TmpRawVertices[1] = (float)j;
				TmpRawVertices[2] = 0.0f;
				
				TmpRawVertices[3] = (float)(i + l);
				TmpRawVertices[4] = (float)j;
				TmpRawVertices[5] = 0.0f;
				
				TmpRawVertices[6] = (float)i;
				TmpRawVertices[7] = (float)(j + l);
				TmpRawVertices[8] = 0.0f;

				TmpRawVertices[9] = (float)(i + l);
				TmpRawVertices[10] = (float)(j + l);
				TmpRawVertices[11] = 0.0f;

				//FloatBuffer VertexBuffer = ByteBufferManager.ReuseFloatBuffer(TmpRawVertices);
 				FloatBuffer VertexBuffer = ByteBufferManager.ReuseFB12(TmpRawVertices);
				
				GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
				GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
				//GLES11.glFrontFace(GLES11.GL_CCW);

				GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, VertexBuffer);
				GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, _FloorTexBuffer);
				
				//Draw the vertices as s, based on the Index Buffer information
				GLES11.glDrawElements(GLES11.GL_TRIANGLES, _NumOfIndices, GLES11.GL_UNSIGNED_BYTE, _IndicesBuffer);
				
				//Disable the client state before leaving
				GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
				GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			}
		}
		//GLES11.glDisable(GLES11.GL_BLEND);
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
	}

	public void drawSkyBox1 ()
	{
		// LH HACK
		//GLES11.glFrontFace(GLES11.GL_CCW); // we want texture inside the box
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		GLES11.glDepthMask(false);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		for (int i=0; i<6; i++)
		{
			GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, SetupGL.GetTexIdSkyBox(i));
			
			GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
			GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);

			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, _SkyBoxVertexBuffers[i]);
			GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, _TexBuffer);
			
			GLES11.glDrawElements(GLES11.GL_TRIANGLES, _NumOfIndices, GLES11.GL_UNSIGNED_BYTE, _IndicesBuffer);

			GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
			GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);

		}
		// LH HACK
		GLES11.glFrontFace(GLES11.GL_CW);
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		GLES11.glDepthMask(true);

	}
	
	
}
