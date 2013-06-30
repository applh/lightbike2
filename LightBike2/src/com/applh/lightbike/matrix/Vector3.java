/*
 * Author: APPLH.COM
 * Creation: 31/08/2012
 * Modif:
 */

package com.applh.lightbike.matrix;

import android.util.FloatMath;

public class Vector3 {

	public float v[] = null; 
	
	public Vector3()
	{
		v = new float[3];
		reset();
	}

	public Vector3 (float x, float y, float z)
	{
		v = new float[3];
		v[0] = x;
		v[1] = y;
		v[2] = z;
	}

	public void reset () {
		v[0] = 0.0f;
		v[1] = 0.0f;
		v[2] = 0.0f;
		
	}

	public void reinit (float x, float y, float z) {
		v[0] = x;
		v[1] = y;
		v[2] = z;
	}
	public void copy (Vector3 V1)
	{
		v[0] = V1.v[0];
		v[1] = V1.v[1];
		v[2] = V1.v[2];
	}
	
	public Vector3 add (Vector3 V1)
	{
		Vector3 res = new Vector3();
		
		res.v[0] = v[0] + V1.v[0];
		res.v[1] = v[1] + V1.v[1];
		res.v[2] = v[2] + V1.v[2];
		
		return res;
	}

	public void middle (Vector3 V1, Vector3 middle)
	{
		if (middle == null) middle = new Vector3();
		
		middle.v[0] = 0.5f * (v[0] + V1.v[0]);
		middle.v[1] = 0.5f * (v[1] + V1.v[1]);
		middle.v[2] = 0.5f * (v[2] + V1.v[2]);	
	}

	public Vector3 sub(Vector3 V1)
	{
		Vector3 res = new Vector3();
		
		res.v[0] = v[0] - V1.v[0];
		res.v[1] = v[1] - V1.v[1];
		res.v[2] = v[2] - V1.v[2];
		
		return res;
	}
	
	public void Mul(float Mul)
	{
		v[0] *= Mul;
		v[1] *= Mul;
		v[2] *= Mul;
	}
	
	public void Scale(float fScale)
	{
		v[0] *= fScale;
		v[1] *= fScale;
		v[2] *= fScale;
	}
	
	public Vector3 Cross(Vector3 V1)
	{
		Vector3 ReturnResult = new Vector3();
		
		ReturnResult.v[0] = v[1] * V1.v[2] - v[2] * V1.v[1];
		ReturnResult.v[1] = v[2] * V1.v[0] - v[0] * V1.v[2];
		ReturnResult.v[2] = v[0] * V1.v[1] - v[1] * V1.v[0];
		
		return ReturnResult;
	}
	
	public float Dot(Vector3 V1)
	{
		return (v[0] * V1.v[0] + v[1] * V1.v[1] + v[2] * V1.v[2]);
	}
	
	public float Length()
	{
		return FloatMath.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	}
	
	public float Length2()
	{
		return FloatMath.sqrt(v[0] * v[0] + v[1] * v[1]);
	}

	public void Normalise()
	{
		float d = Length();
		
		if (d != 0) {
			v[0] /= d;
			v[1] /= d;
			v[2] /= d;
		}
	}
	
	public void Normalise2()
	{
		float d = Length2();
		
		if(d != 0)
		{
			v[0] /= d;
			v[1] /= d;
		}
	}
	
	public Vector3 Orthogonal()
	{
		Vector3 ReturnResult = new Vector3();
		
		ReturnResult.v[0] = v[1];
		ReturnResult.v[1] = -v[0];
		
		return ReturnResult;
	}
	
}
