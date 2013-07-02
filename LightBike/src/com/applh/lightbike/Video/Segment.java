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

import com.applh.lightbike.matrix.Vector3;

public class Segment {
	
	
	public Vector3 vStart = null;
	public Vector3 vDirection = null;
	
	public float t1 = 0.0f;
	public float t2 = 0.0f;
	
	private boolean findT_Result = false;
	
	public Segment () {
		vStart = new Vector3();
		vDirection = new Vector3();
		findT_Result = false;
		reset();
	}
	
	public void reset () {
		vStart.reset();
		vDirection.reset();
		findT_Result = false;
		t1 = 0.0f;
		t2 = 0.0f;
	}
	
	
	private float findT(Segment S, Vector3 V)
	{
		float t = 0.0f;
		float epsilon = 0.001f;
		
		findT_Result = false;
		
		if(Math.abs(S.vDirection.v[0]) > Math.abs(S.vDirection.v[1])) {
			
			t = (V.v[0] - S.vStart.v[0]) / S.vDirection.v[0];
			
			if(Math.abs(V.v[1] - (S.vStart.v[1] + t * S.vDirection.v[1])) > epsilon) {
				findT_Result = true;
			}
		
		} else {
			
			t = (V.v[1] - S.vStart.v[1]) / S.vDirection.v[1];
			
			if(Math.abs(V.v[0] - (S.vStart.v[0] + t * S.vDirection.v[0])) > epsilon) {
				findT_Result = true;
			}
			
		}
		
		return t;
	}
	
	private Vector3 IntersectParallel2 (Segment S2)
	{
		Vector3 ReturnResult = new Vector3();
		Vector3 V = new Vector3();
		float t;
		
		// If the lines dont overlap return NULL
		// else find t2 for t1 = 0
		// if t2 in [0,1] return t2, t1 = 0
		// else find t1 for t2 ==0 and t2 == 1
		// if t1 < 0 return NULL (no intersection)
		// else return the smaller t1 and the corresponding t2
		
		// if the lines dont overlap return NULL
		// else find t2 for t1 == 0
		
		V.copy(vStart);
		t2 = findT(S2, V);
		if(findT_Result) {
			// vector is not collinear
			return null;
		}
		
		// if t2 in [0,1] return t2, t1 = 0
		if(t2 >= 0.0f && t2 <= 1.0f) {
			ReturnResult.copy(vStart);
			t1 = 0;
			return ReturnResult;
		}
		
		// else find t1 for t2 == 0 and t2 == 1
		V.copy(S2.vStart);
		t1 = findT(this, V);
		if(findT_Result) {
			return null;
		}
		
		// if t1 < 0 return NULL (no intersection)
		if(t1 < 0.0f) {
			return null;
		}
		
		S2.vStart.addBuf(S2.vDirection, V);
		
		t = findT(this,V);
		if(findT_Result) {
			return null;
		}
		
		if(t1 > 1.0f && t > 1.0f) {
			return null;
		}
		
		if (t < t1) {
			t1 = t;
			t2 = 1.0f;
			ReturnResult.copy(V);
		} else {
			t2 = 0.0f;
			ReturnResult.copy(S2.vStart);
		}
			
		return ReturnResult;
	}
	
	private Vector3 IntersectNonParallel(Segment S2)
	{
		Vector3 ReturnResult = new Vector3();
		Vector3 v1 = new Vector3();
		Vector3 v2 = new Vector3();
		Vector3 tmp1 = new Vector3();
		Vector3 tmp2 = new Vector3();
		Vector3 vIntersection = new Vector3();
		
		// compute the homogenous line coordinates
		tmp1.v[0] = vStart.v[0];
		tmp1.v[1] = vStart.v[1];
		tmp1.v[2] = 1.0f;
		
		tmp2.v[0] = vStart.v[0] + vDirection.v[0];
		tmp2.v[1] = vStart.v[1] + vDirection.v[1];
		tmp2.v[2] = 1.0f;
		
		v1 = tmp1.Cross(tmp2);
		
		tmp1.v[0] = S2.vStart.v[0];
		tmp1.v[1] = S2.vStart.v[1];
		tmp1.v[2] = 1.0f;
		
		tmp2.v[0] = S2.vStart.v[0] + S2.vDirection.v[0];
		tmp2.v[1] = S2.vStart.v[1] + S2.vDirection.v[1];
		tmp2.v[2] = 1.0f;
		
		v2 = tmp1.Cross(tmp2);
		
		// compute the intersection in homogenous coordinates and project back to 2d
		vIntersection = v1.Cross(v2);
		ReturnResult.v[0] = vIntersection.v[0] / vIntersection.v[2];
		ReturnResult.v[1] = vIntersection.v[1] / vIntersection.v[2];
		
		// compute t1, t2
		if(Math.abs(vDirection.v[0]) > Math.abs(vDirection.v[1])) {
			t1 = (ReturnResult.v[0] - vStart.v[0]) / vDirection.v[0];
		} else {
			t1 = (ReturnResult.v[1] - vStart.v[1]) / vDirection.v[1];
		}
		
		if(Math.abs(S2.vDirection.v[0]) > Math.abs(S2.vDirection.v[1])) {
			t2 = (ReturnResult.v[0] - S2.vStart.v[0]) / S2.vDirection.v[0];
		} else {
			t2 = (ReturnResult.v[1] - S2.vStart.v[1]) / S2.vDirection.v[1];
		}
		
		return ReturnResult;
	}
	
	public Vector3 Intersect2 (Segment S2)
	{
		Vector3 ReturnResult; // = new Vec();
		
		if (Math.abs(vDirection.Dot(S2.vDirection.Orthogonal())) < 0.1) {
			// Vector lines are parallel
			ReturnResult = IntersectParallel2(S2);
			if (ReturnResult == null) {
				t1 = 0.0f;
				t2 = 0.0f;
			}
		} else {
			// Non parallel
			ReturnResult = IntersectNonParallel(S2);
		}
		
		return ReturnResult;
	}
	
	public float Length()
	{
		return vDirection.Length();
	}
	
}
