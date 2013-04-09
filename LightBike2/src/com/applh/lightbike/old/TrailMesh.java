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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.util.FloatMath;

import com.applh.lightbike.Game.Player;
import com.applh.lightbike.Video.Segment;
import com.applh.lightbike.matrix.Vector3;

public class TrailMesh {

	int iSize;
	int iUsed;
	int piOffset;
	int pvOffset;
	
//	private static final float White[] = {1.0f,1.0f,1.0f,1.0f};
//	private static final float Yellow[] = {1.0f,1.0f,0.2f,1.0f};
	private static byte TmpColor[] = {0, 0, 0, 0};

	Player playerData;
	
	private static enum MeshColourType {
		E_COLOUR_TRAIL,
		E_COLOUR_BRIGHT,
		E_COLOUR_CYCLE
	}
	
	private final float DECAL_WIDTH = 20.0f;
	
	private final Vector3 normals[] = {
			new Vector3(1.0f, 0.0f, 0.0f),
			new Vector3(-1.0f, 0.0f, 0.0f),
			new Vector3(0.0f, 1.0f, 0.0f),
			new Vector3(0.0f,-1.0f,0.0f)
	};

	private final float DIRS_X[] = {0.0f, -1.0f, 0.0f, 1.0f};
	private final float DIRS_Y[] = {-1.0f, 0.0f, 1.0f, 0.0f};
	
	private final float BOW_LENGTH = 6.0f;
	private final float BOW_DIST2 = 0.85f;
	private final float BOW_DIST3 = 2.0f;
	private final float BOW_DIST1 = 0.4f;
	private final float dists[] = { BOW_DIST2, BOW_DIST3, BOW_DIST1, 0.0f };

	private static Segment TmpBow = new Segment();

	public static FloatBuffer Vbuf = FloatBuffer.allocate(6090);
	public static FloatBuffer Nbuf = FloatBuffer.allocate(6090);
	public static FloatBuffer Tbuf = FloatBuffer.allocate(4060);
	public static ShortBuffer Ibuf = ShortBuffer.allocate(3078);
	public static ByteBuffer Cbuf = ByteBuffer.allocate(8120);
	/*
	public static FloatBuffer Vbuf = GraphicUtils.ConvToFloatBuffer(null, 6090);
	public static FloatBuffer Nbuf = GraphicUtils.ConvToFloatBuffer(null, 6090);
	public static FloatBuffer Tbuf = GraphicUtils.ConvToFloatBuffer(null, 4060);
	public static ShortBuffer Ibuf = GraphicUtils.ConvToShortBuffer(null, 3078);
	public static ByteBuffer Cbuf = GraphicUtils.ConvToByteBuffer(null, 8120);

	// MAX TRAILS = 500
	public static final float Vertices[] = new float[6090];
	public static final float Normals[] = new float[6090];
	public static final float TexCoords[] = new float[4060];
	public static final short Indices[] = new short[3078];
	public static final byte Colors[] = new byte[8120];
*/
	/*

	public static FloatBuffer Vbuf = null;
	public static FloatBuffer Nbuf = null;
	public static FloatBuffer Tbuf = null;
	public static ShortBuffer Ibuf = null;
	public static ByteBuffer Cbuf = null;
*/	

	public static int Vsize = 0;
	public static int Nsize = 0;
	public static int Tsize = 0;
	public static int Isize = 0;
	public static int Csize = 0;
/*
	private static Vertices = new float[3 * vmeshSize]; // MAX 6090
	private static Normals = new float[3 * vmeshSize]; // MAX 6090
	private static TexCoords = new float[2 * vmeshSize]; // MAX 4060
	private static  Indices = new short[imeshSize]; // MAX 3078
	private static Colors = new byte[4 * vmeshSize]; // MAX 8120
*/

	private static final short PpBase[][] = {
			{ 0, 2, 1, 2, 3, 1 },
			{ 0, 1, 2, 1, 3, 2}
	};

	
	public TrailMesh (Player player)
	{
		playerData = player;

		int vmeshSize = ((player.aTrailOffset * 4) + 8) + ((10 * 2) + 2); // MAX 2030
		int imeshSize = ((player.aTrailOffset * 6) + (2 * 6)) + ((10*6) + 6); // MAX 3078
		Vsize = 3 * vmeshSize;
		Nsize = 3 * vmeshSize;
		Tsize = 2 * vmeshSize;
		Isize = imeshSize;
		Csize = 4 * vmeshSize;
		
		/*

		int TrailOffset = playerData.aTrailOffset;

		Vertices = new float[3 * vmeshSize]; // MAX 6090
		Normals = new float[3 * vmeshSize]; // MAX 6090
		TexCoords = new float[2 * vmeshSize]; // MAX 4060
		Indices = new short[imeshSize]; // MAX 3078
		Colors = new byte[4 * vmeshSize]; // MAX 8120
*/		
		trailGeometry();
		bowGeometry();
	}
	
	private void bowGeometry ()
	{
		TmpBow.reset();
		
		int bdist = 2;
		int i;
		float trail_height = playerData.getTrailHeight();
		float t, fTop, fFloor;
		int iOffset = piOffset;
		
		TmpBow.vStart.v[0] = getSegmentEndX(0) ;
		TmpBow.vStart.v[1] = getSegmentEndY(0) ;
		TmpBow.vDirection.v[0] = getSegmentEndX(bdist) - TmpBow.vStart.v[0];
		TmpBow.vDirection.v[1] = getSegmentEndY(bdist) - TmpBow.vStart.v[1];
		
		// TRAIL NEXT TO BIKE
		for (i = 0; i < 10; i++) {
			t = i * 1.0f / 10.0f;
			fTop = FloatMath.sqrt(1.0f - t * t);
			fFloor = (t < 0.6f) ? 0.0f : 0.5f * (t - 0.6f);
			
			if (fTop < 0.3f) fTop = 0.3f;
			
			storeVertex(pvOffset, TmpBow, t, (fFloor * trail_height), (fTop * trail_height), DECAL_WIDTH, 0.0f);
			storeColor(pvOffset, MeshColourType.E_COLOUR_BRIGHT);
			pvOffset += 2;
			
			if (i > 0) {
				storeIndices(iOffset,pvOffset - 4);
				iOffset += 6;
			}
			
		}
		storeVertex(pvOffset, TmpBow, 1.0f, (0.2f * trail_height), ( 0.3f * trail_height), DECAL_WIDTH, 0.0f);
		storeColor(pvOffset, MeshColourType.E_COLOUR_CYCLE);
		pvOffset += 2;
		storeIndices(iOffset, pvOffset - 4);
		iUsed += iOffset -  piOffset;
		piOffset = iOffset;
	}
	
	private void trailGeometry ()
	{
		int i;
		int curVertex = 0;
		int curIndex = 0;
		int TrailOffset = playerData.aTrailOffset;
		Segment segs[] = playerData.getTrails();
		float trail_height = playerData.getTrailHeight();
		float fTotalLength = 0.0f;
		float fsegLength;

		TmpBow.reset();
		
		for (i=0; i < TrailOffset; i++) {
			fsegLength = segs[i].Length();
			if ((i == 0) || cmpdir(segs[i-1], segs[i]) ) {
				storeVertex(curVertex, segs[i], 0.0f, 0.0f, trail_height, fsegLength, fTotalLength);
				storeColor(curVertex, MeshColourType.E_COLOUR_TRAIL);
				curVertex += 2;
			}
			
			storeVertex(curVertex, segs[i], 1.0f, 0.0f, trail_height, fsegLength, fTotalLength);
			storeColor(curVertex, MeshColourType.E_COLOUR_TRAIL);
			curVertex += 2;
			
			storeIndices(curIndex, curVertex - 4);
			curIndex += 6;
			
			fTotalLength += fsegLength;
		}
		
		TmpBow.vStart.v[0] = segs[TrailOffset].vStart.v[0];
		TmpBow.vStart.v[1] = segs[TrailOffset].vStart.v[1];
		TmpBow.vDirection.v[0] = getSegmentEndX(1) - TmpBow.vStart.v[0];
		TmpBow.vDirection.v[1] = getSegmentEndY(1) - TmpBow.vStart.v[1];
		
		fsegLength = TmpBow.Length();
		
		storeVertex(curVertex, TmpBow, 0.0f, 0.0f, trail_height, fsegLength, fTotalLength);
		storeColor(curVertex, MeshColourType.E_COLOUR_TRAIL);
		curVertex += 2;
		
		storeVertex(curVertex, TmpBow, 1.0f, 0.0f, trail_height, fsegLength, fTotalLength);
		storeColor(curVertex,MeshColourType.E_COLOUR_TRAIL );
		curVertex += 2;
		
		storeIndices(curIndex,curVertex - 4);
		curIndex += 6;
		
		fTotalLength += fsegLength;
		
		TmpBow.vStart.v[0] += TmpBow.vDirection.v[0];
		TmpBow.vStart.v[1] += TmpBow.vDirection.v[1];
		TmpBow.vDirection.v[0] = getSegmentEndX(0) - TmpBow.vStart.v[0];
		TmpBow.vDirection.v[1] = getSegmentEndY(0) - TmpBow.vStart.v[1];
		fsegLength = TmpBow.Length();
		
		storeVertex(curVertex, TmpBow, 0.0f, 0.0f, trail_height, fsegLength, fTotalLength);
		storeColor(curVertex, MeshColourType.E_COLOUR_TRAIL);
		curVertex += 2;
		
		storeVertex(curVertex, TmpBow, 1.0f, 0.0f, trail_height, fsegLength, fTotalLength);
		storeColor(curVertex, MeshColourType.E_COLOUR_BRIGHT);
		curVertex += 2;
		
		storeIndices(curIndex,curVertex - 4);
		curIndex += 6;
		
		iUsed = curIndex;
		piOffset = curIndex;
		pvOffset = curVertex;

	}
/*	
	private void storeColor(int offset, MeshColourType colourType)
	{
		int colOffset = offset * 4;
		
		switch(colourType) {
			case E_COLOUR_TRAIL:
				TmpColor = playerData.mColorAlpha255;
				break;
			case E_COLOUR_BRIGHT:
				// TO IMPROVE FOR EFFECT
				TmpColor = playerData.mColorDiffuse255;
				break;
			case E_COLOUR_CYCLE:
				TmpColor = playerData.mColorDiffuse255;
				break;
		}
		Colors[colOffset++] = TmpColor[0];
		Colors[colOffset++] = TmpColor[1];
		Colors[colOffset++] = TmpColor[2];
		Colors[colOffset++] = TmpColor[3];

		Colors[colOffset++] = TmpColor[0];
		Colors[colOffset++] = TmpColor[1];
		Colors[colOffset++] = TmpColor[2];
		Colors[colOffset++] = TmpColor[3];

	}
*/	
	private void storeColor(int offset, MeshColourType colourType)
	{
		int colOffset = offset * 4;
		
		switch(colourType) {
			case E_COLOUR_TRAIL:
				TmpColor = playerData.mColorAlpha255;
				break;
			case E_COLOUR_BRIGHT:
				// TO IMPROVE FOR EFFECT
				TmpColor = playerData.mColorDiffuse255;
				break;
			case E_COLOUR_CYCLE:
				TmpColor = playerData.mColorDiffuse255;
				break;
		}
		Cbuf.put(colOffset++, TmpColor[0]);
		Cbuf.put(colOffset++, TmpColor[1]);
		Cbuf.put(colOffset++, TmpColor[2]);
		Cbuf.put(colOffset++, TmpColor[3]);

		Cbuf.put(colOffset++, TmpColor[0]);
		Cbuf.put(colOffset++, TmpColor[1]);
		Cbuf.put(colOffset++, TmpColor[2]);
		Cbuf.put(colOffset++, TmpColor[3]);

	}

	private float getSegmentEndY( int dist)
	{
		float tlength,blength;
		float retVal;
		int dir = playerData.getDirection();
		int TrailOffset = playerData.aTrailOffset;
		Segment segs[] = playerData.getTrails();
		
		if (DIRS_Y[dir] == 0) {
			retVal = segs[TrailOffset].vStart.v[1] + segs[TrailOffset].vDirection.v[1];
		}
		else {
			tlength = segs[TrailOffset].Length();
			blength = (tlength < 2 * BOW_LENGTH) ? tlength / 2 : BOW_LENGTH;
			
			retVal = (segs[TrailOffset].vStart.v[1] + segs[TrailOffset].vDirection.v[1] - 
					dists[dist] * blength * DIRS_Y[dir]);
		}
		
		return retVal;
	}
	
	private float getSegmentEndX(int dist)
	{
		float tlength, blength;
		float retVal;
		int dir = playerData.getDirection();
		int TrailOffset = playerData.aTrailOffset;
		Segment segs[] = playerData.getTrails();
		
		if (DIRS_X[dir] == 0) {
			retVal = segs[TrailOffset].vStart.v[0] + segs[TrailOffset].vDirection.v[0];
		}
		else {
			tlength = segs[TrailOffset].Length();
			blength = (tlength < 2 * BOW_LENGTH) ? tlength /2 : BOW_LENGTH;
			
			retVal = (segs[TrailOffset].vStart.v[0] + segs[TrailOffset].vDirection.v[0] -
					dists[dist] * blength * DIRS_X[dir]);
		}
		return retVal;
	}
	
	private boolean cmpdir(Segment s1, Segment s2)
	{
		boolean returnval = true;
		if( (s1.vDirection.v[0] == 0.0f && s2.vDirection.v[0] == 0.0f) ||
			  (s1.vDirection.v[1] == 0.0f && s2.vDirection.v[1] == 0.0f) )
		{
			returnval = false;
		}
		return returnval;
	}
	
	private void storeIndices(int indexOffset, int vertexOffset)
	{
		int i;
		int winding = 0;
		
		if (Vbuf.get(vertexOffset * 3) == Vbuf.get((vertexOffset + 2) * 3)) {
				//winding = (Vertices[(vertexOffset * 3) + 1] <= Vertices[((vertexOffset + 2) * 3) + 1]) ? 0 : 1; 
				winding = (Vbuf.get((vertexOffset * 3) + 1) <= Vbuf.get(((vertexOffset + 2) * 3) + 1)) ? 0 : 1; 
		}
		else {
				//winding = (Vertices[vertexOffset * 3] < Vertices[(vertexOffset + 2) * 3]) ? 1 : 0;;
				winding = (Vbuf.get(vertexOffset * 3) <= Vbuf.get((vertexOffset + 2) * 3)) ? 0 : 1; 
		}
		
		for (i = 0; i < 6; i++) {
			Ibuf.put(indexOffset + i, (short)(PpBase[winding][i] + vertexOffset));
		}
	}
	private void storeVertex(
			int offset, Segment s, float t, float fFloor, float fTop, 
			float fSegLength, float fTotalLength)
	{
		
		Vector3 v;
		int texOffset = 2 * offset;
		
		int iNormal;
		float fUStart;
		
		if (s.vDirection.v[0] == 0.0f)
			iNormal = 0;
		else
			iNormal = 2;
		
		fUStart = (fTotalLength / DECAL_WIDTH) - FloatMath.floor((float) fTotalLength / DECAL_WIDTH);
		
		v = new Vector3(
				 (s.vStart.v[0] + t * s.vDirection.v[0]),
				 ( s.vStart.v[1] + t * s.vDirection.v[1]),
				 fFloor);
				
		Tbuf.put(texOffset, fUStart + t * fSegLength / DECAL_WIDTH);
		Tbuf.put(texOffset +1, 0.0f);
		
//		Vertices[offset * 3] = v.v[0];
//		Vertices[(offset * 3) + 1] = v.v[1];
//		Vertices[(offset * 3) + 2] = v.v[2];
		Vbuf.put(offset * 3, v.v[0]);
		Vbuf.put((offset * 3) +1, v.v[1]);
		Vbuf.put((offset * 3) +2, v.v[2]);
		
		Nbuf.put(offset * 3, normals[iNormal].v[0]);
		Nbuf.put((offset * 3) +1, normals[iNormal].v[1]);
		Nbuf.put((offset * 3) +2, normals[iNormal].v[2]);
		
		texOffset += 2;
		v.v[2] = fTop;
		Tbuf.put(texOffset, fUStart + t * fSegLength / DECAL_WIDTH);
		Tbuf.put(texOffset +1, 0.0f);

//		Vertices[(offset + 1) * 3] = v.v[0];
//		Vertices[((offset + 1) * 3) + 1] = v.v[1];
//		Vertices[((offset + 1) * 3) + 2] = v.v[2];
		Vbuf.put((offset +1) * 3, v.v[0]);
		Vbuf.put(((offset +1) * 3) +1, v.v[1]);
		Vbuf.put(((offset +1) * 3) +2, v.v[2]);
		
		Nbuf.put((offset + 1) *3, normals[iNormal].v[0]);
		Nbuf.put(((offset +1) *3) +1, normals[iNormal].v[1]);
		Nbuf.put(((offset +1) *3) +2, normals[iNormal].v[2]);
	}
	
}

	/*	
	private void storeVertex(
			int offset, Segment s, float t, float fFloor, float fTop, 
			float fSegLength, float fTotalLength)
	{
		
		Vec v;
		int texOffset = 2 * offset;
		
		int iNormal;
		float fUStart;
		
		if (s.vDirection.v[0] == 0.0f)
			iNormal = 0;
		else
			iNormal = 2;
		
		fUStart = (fTotalLength / DECAL_WIDTH) - FloatMath.floor((float) fTotalLength / DECAL_WIDTH);
		
		v = new Vec(
				 (s.vStart.v[0] + t * s.vDirection.v[0]),
				 ( s.vStart.v[1] + t * s.vDirection.v[1]),
				 fFloor);
				
		TexCoords[texOffset] = fUStart + t * fSegLength / DECAL_WIDTH;
		TexCoords[texOffset + 1] = 0.0f;
		
		Vertices[offset * 3] = v.v[0];
		Vertices[(offset * 3) + 1] = v.v[1];
		Vertices[(offset * 3) + 2] = v.v[2];
		
		Normals[offset * 3] = normals[iNormal].v[0];
		Normals[(offset * 3) + 1] = normals[iNormal].v[1];
		Normals[(offset * 3) + 2] = normals[iNormal].v[2];
		
		texOffset += 2;
		v.v[2] = fTop;
		TexCoords[texOffset] =  fUStart + t * fSegLength / DECAL_WIDTH;
		TexCoords[texOffset + 1] = 1.0f;
		Vertices[(offset + 1) * 3] = v.v[0];
		Vertices[((offset + 1) * 3) + 1] = v.v[1];
		Vertices[((offset + 1) * 3) + 2] = v.v[2];
		
		Normals[(offset + 1) * 3] = normals[iNormal].v[0];
		Normals[((offset + 1) *3) + 1] = normals[iNormal].v[1];
		Normals[((offset + 1) *3) + 2] = normals[iNormal].v[2];
	}
	
}
*/