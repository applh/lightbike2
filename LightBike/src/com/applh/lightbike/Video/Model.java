/*
 * Copyright � 2012 Iain Churcher
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

//import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES11;

import android.content.Context;

import com.applh.lightbike.Video.Material.ColourType;
import com.applh.lightbike.fx.ByteBufferManager;
import com.applh.lightbike.matrix.Vector3;

public class Model {

	// member vars
	private Material mMaterials;
	private int MaterialsCount[];
	private float rawVertex[];
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mNormalBuffer;
	private ShortBuffer mIndicesBuffer[];
	private int mNumOfVertices;
	private Vector3 mBBoxMin;
	private Vector3 mBBoxSize;
	private float mBBoxfRadius;
	
	//String Debug;
	//StringBuffer sb = new StringBuffer(40);
	
	private class face {
		int vertex[] = new int[3];
		int normal[] = new int[3];
		int material;
	}
	
	private class vec3 {
		float v[] = {0.0f, 0.0f, 0.0f};
	}
	
	public Model(Context ctx, int resId)
	{
		readMesh(ctx,resId);
	}
	
	private void readMesh (Context ctx, int resId)
	{
		String buf;
		String[] temp;
		String[] temp2;
		String restemp;
		int resourceId;
		int faceIndex = -1;
		ArrayList<vec3> mVertices = new ArrayList<vec3>();
		ArrayList<vec3> mNormals = new ArrayList<vec3>();
		ArrayList<face> mFaces = new ArrayList<face>();
		int numOfVertices = 0;
		int numOfNormals = 0;
		int numOfFaces = -1;
		vec3 Vertex;
		face currFace;
		face faceArray[];
				
		InputStream inputStream = ctx.getResources().openRawResource(resId);
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		
		try
		{
			while((buf = buffreader.readLine()) != null) {
				
				if(buf != null && (buf.length() > 1)) {
					temp = buf.split(" ");
					switch(buf.charAt(0)) {
						case 'm':
							//obtain the resource id
							restemp = temp[1].substring(0, (temp[1].length()-4));
							resourceId = ctx.getResources().getIdentifier(restemp.trim(), "raw", ctx.getPackageName());
							if(mMaterials == null) {
								mMaterials = new Material(ctx,resourceId);
							} else {
								mMaterials.AddMaterial(ctx,resourceId);
							}
							break;
						case 'u':
							//search all materials for mesh for name
							int result;
							result = mMaterials.GetIndex(temp[1]); 
							if(result > -1) {
								faceIndex = result; 
							}
							break;
						case 'v':
							// populate vertex, normal and texture coords
							switch(buf.charAt(1)) {
								case ' ':
									mVertices.add(new vec3());
									Vertex = (vec3)mVertices.get(numOfVertices);
									Vertex.v[0] = Float.valueOf(temp[1].trim()).floatValue();
									Vertex.v[1] = Float.valueOf(temp[2].trim()).floatValue();
									Vertex.v[2] = Float.valueOf(temp[3].trim()).floatValue();
									numOfVertices++;
									break;
								case 'n':
									mNormals.add(new vec3());
									Vertex = (vec3)mNormals.get(numOfNormals);
									Vertex.v[0] = Float.valueOf(temp[1].trim()).floatValue();
									Vertex.v[1] = Float.valueOf(temp[2].trim()).floatValue();
									Vertex.v[2] = Float.valueOf(temp[3].trim()).floatValue();
									numOfNormals++;
									break;
								case 't':
									// ignore textures
									break;
							}
							break;
						case 'f':
							// Load face data
							numOfFaces++;
							mFaces.add(new face());
							currFace = (face)mFaces.get(numOfFaces);
							temp2 = temp[1].split("//"); 
							currFace.vertex[0] = Integer.parseInt(temp2[0].trim());
							currFace.normal[0] = Integer.parseInt(temp2[1].trim());
							temp2 = temp[2].split("//");
							currFace.vertex[1] = Integer.parseInt(temp2[0].trim());
							currFace.normal[1] = Integer.parseInt(temp2[1].trim());
							temp2 = temp[3].split("//");
							currFace.vertex[2] = Integer.parseInt(temp2[0].trim());
							currFace.normal[2] = Integer.parseInt(temp2[1].trim());
							currFace.material = faceIndex;
							break;
					}
				}
			}
		}
		catch (IOException e) 
		{
		}
		
		faceArray = new face[mFaces.size()];
		faceArray = (face[])mFaces.toArray(faceArray);
		int nVertices = 0;
		
		// Create an array of the size of the total number of materials
		int numOfMaterials = mMaterials.GetNumber();
		MaterialsCount = new int[numOfMaterials];
		
		for(int x=0; x<numOfMaterials; x++) {
			MaterialsCount[x] = 0;
		}
		
		for(int x=0; x<numOfFaces; x++) {
			MaterialsCount[faceArray[x].material] += 1;
		}
		
		// Combine vectors and normals for each vertex
		int lookup[][] = new int[numOfVertices][numOfNormals];
		
		for(int i=0; i<numOfVertices; i++) {
			for(int j=0; j<numOfNormals; j++) {
				lookup[i][j] = -1;
			}
		}
		
		for(int i=0; i<numOfFaces; i++) {
			for(int j=0; j<3; j++) {
				int vertex = faceArray[i].vertex[j] - 1;
				int normal = faceArray[i].normal[j] - 1;
				if(lookup[vertex][normal] == -1) {
					lookup[vertex][normal] = nVertices;
					nVertices++;
				}
			}
		}
		
		
		// Now that we have loaded all the data build the vertexarray
		mNumOfVertices = nVertices;
		
//		ByteBuffer vbb = ByteBuffer.allocateDirect(mNumOfVertices * 3 * 4);
//		vbb.order(ByteOrder.nativeOrder());
//		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer = ByteBufferManager.CreateFloatBuffer(null, 3 * mNumOfVertices);
			
//		ByteBuffer nbb = ByteBuffer.allocateDirect(mNumOfVertices * 3 * 4);
//		nbb.order(ByteOrder.nativeOrder());
//		mNormalBuffer = nbb.asFloatBuffer();
		mNormalBuffer = ByteBufferManager.CreateFloatBuffer(null, 3 * mNumOfVertices);
		
		float tempVertex[] = new float[mNumOfVertices * 3];
		float tempnormals[] = new float[mNumOfVertices * 3];
		vec3 VertexArr[] = new vec3[mVertices.size()]; 
		VertexArr = (vec3[])mVertices.toArray(VertexArr);
		vec3 NormalArr[] = new vec3[mNormals.size()]; 
		NormalArr = (vec3[])mNormals.toArray(NormalArr);
		
		for (int i=0;i<numOfVertices;i++) {
			for(int j=0;j<numOfNormals; j++) {
				int vertex = lookup[i][j];
				if(vertex != -1) {
					for(int k=0; k<3; k++) {
						tempVertex[3 * vertex + k] = VertexArr[i].v[k];
						tempnormals[3 * vertex + k] = NormalArr[i].v[k];
					}
				}
			}
		}
		
		rawVertex = tempVertex;
		
		mVertexBuffer.put(tempVertex);
		mVertexBuffer.position(0);
		mNormalBuffer.put(tempnormals);
		mNormalBuffer.position(0);
		
		// Create the indices (per Material)
		mIndicesBuffer = new ShortBuffer[numOfMaterials];
		int tempface[] = new int[numOfMaterials];

		ArrayList<short[]> Indices = new ArrayList<short[]>();
		short tempIndices[];
		
		for (int i=0;i<numOfMaterials;i++) {
			ByteBuffer ibb = ByteBuffer.allocateDirect(2 * 3 * MaterialsCount[i]);
			ibb.order(ByteOrder.nativeOrder());
			mIndicesBuffer[i] = ibb.asShortBuffer();
			tempface[i] = 0;
			Indices.add(new short[MaterialsCount[i] * 3]);
		}
		
		for (int i=0;i<numOfFaces;i++) {
			int materialtemp = faceArray[i].material;
			tempIndices = (short[])Indices.get(materialtemp);
			for(int j=0;j<3;j++) {
				int vertex = faceArray[i].vertex[j] - 1;
				int normal = faceArray[i].normal[j] - 1;
				tempIndices[3 * tempface[materialtemp] + j] = (short)lookup[vertex][normal];
			}
			tempface[materialtemp] += 1;
		}
		
		for(int i=0;i<numOfMaterials;i++) {
			tempIndices = (short[])Indices.get(i);
			mIndicesBuffer[i].put(tempIndices);
			mIndicesBuffer[i].position(0);
		}
		
		float[] HullColour = {0.0f, 0.1f, 0.9f, 1.0f};
		
		mMaterials.SetMaterialColour("Hull", ColourType.E_AMBIENT, HullColour);
		mMaterials.SetMaterialColour("Hull", ColourType.E_DIFFUSE, HullColour);

		computeBBox();
		
	}

	public void Draw (float ambient_color[], float diffuse_color[])
	{
		int MaterialCount = mMaterials.GetNumber();
		
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, mVertexBuffer);
		GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, mNormalBuffer);

		mMaterials.SetMaterialColour("Hull", ColourType.E_AMBIENT, ambient_color);
		mMaterials.SetMaterialColour("Hull", ColourType.E_DIFFUSE, diffuse_color);
		
		for (int i=0; i<MaterialCount;i++) {
			if (mIndicesBuffer[i].capacity() > 0) {
				GLES11.glMaterialfv(GLES11.GL_FRONT_AND_BACK, GLES11.GL_AMBIENT, mMaterials.GetAmbient2(i));
				GLES11.glMaterialfv(GLES11.GL_FRONT_AND_BACK, GLES11.GL_DIFFUSE, mMaterials.GetDiffuse2(i));
				GLES11.glMaterialfv(GLES11.GL_FRONT_AND_BACK, GLES11.GL_SPECULAR, mMaterials.GetSpecular2(i));
				GLES11.glMaterialf(GLES11.GL_FRONT_AND_BACK, GLES11.GL_SHININESS, mMaterials.GetShininess(i));
				
				GLES11.glDrawElements(GLES11.GL_TRIANGLES, mIndicesBuffer[i].capacity(),
						GLES11.GL_UNSIGNED_SHORT, mIndicesBuffer[i]);
			}
		}
		
	}

	private void computeBBox()
	{
		int i, j;
		Vector3 vMin = new Vector3(rawVertex[0],rawVertex[1],rawVertex[2]);
		Vector3 vMax = new Vector3(rawVertex[0],rawVertex[1],rawVertex[2]);
		Vector3 vSize = new Vector3();
		
		for (i=0; i<mNumOfVertices; i++) {
			for(j=0; j<3; j++) {
				if(vMin.v[j] > rawVertex[3 * i + j]) {
					vMin.v[j] = rawVertex[3 * i + j];
				}
				if(vMax.v[j] < rawVertex[3 * i + j]) {
					vMax.v[j] = rawVertex[3 * i + j];
				}
			}
		}
		
		vSize = vMax.sub(vMin);
		mBBoxMin = vMin;
		mBBoxSize = vSize;
		mBBoxfRadius = vSize.Length() / 10.0f;
		
	}
	
	public Vector3 GetBBoxSize()
	{
		return mBBoxSize;
	}
	
	public Vector3 GetBBoxMin()
	{
		return mBBoxMin;
	}

	public float GetBBoxRadius()
	{
		return mBBoxfRadius;
	}
}
