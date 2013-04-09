/*
 * Author:   APPLH.COM
 * Creation: 30/08/12
 * Modifs:
 * 30/08/2012
 * USE OF AllocateDirect is MANDATORY TO WORK WITH OPENGL ES 
 */

package com.applh.lightbike.fx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class ByteBufferManager {

	private static int CurFrameFB = 0;
	private static int CurFrameReuseCount = 0;
	
	private static FloatBuffer TabReuseFB[] = null;
	private static int MaxTabFB = 400;
	
	private static FloatBuffer FB3 = null;
	private static FloatBuffer FB4 = null;
	private static FloatBuffer FB12 = null;
	
	public class Vec2 {
		public float v[] = new float[2];
	}
	
	public class Vec3 {
		public float v[] = new float[3];
	}
	
	public class Vec4 {
		public float v[] = new float[4];
	}

	
	public static Vec2 vec2Add(Vec2 Result, Vec2 v1, Vec2 v2) 
	{
		Result.v[0] = v1.v[0] + v2.v[0];
		Result.v[1] = v1.v[1] + v2.v[1];
		
		return Result;
	}

	public static void Reinit () {
		TabReuseFB = null;
		// RESET
		CurFrameFB = 0;
		CurFrameReuseCount = 0;

		FB3 = null;
		FB4 = null;
		FB12 = null;
	}
	
	public static int ResetFrameFB () {
		int res = CurFrameFB;
		
		// expand FB table if necessary
		if (CurFrameFB > MaxTabFB) {
			MaxTabFB = CurFrameFB;
			TabReuseFB = null;
		}
		// RESET
		CurFrameFB = 0;
		CurFrameReuseCount = 0;
		
		return res;
		
	}

	public static FloatBuffer ReuseFB3 (float buf[]) {
		if (FB3 == null) {
			float[] tmpFB = {0.0f, 0.0f, 0.0f};
			FB3 = CreateFloatBuffer(tmpFB);
		}
		
		if (FB3 != null) {
			// copy the values
			FB3.rewind();
			FB3.put(buf, 0, 3);
			// update limit
			FB3.limit(3);
			FB3.rewind();	
		}
		return FB3;
	}

	public static FloatBuffer ReuseFB4 (float buf[]) {
		if (FB4 == null) {
			float[] tmpFB = {0.0f, 0.0f, 0.0f, 0.0f};
			FB4 = CreateFloatBuffer(tmpFB);
		}
		
		if (FB4 != null) {
			// copy the values
			FB4.rewind();
			FB4.put(buf, 0, 4);
			// update limit
			FB4.limit(4);
			FB4.rewind();	
		}
		return FB4;
	}

	public static FloatBuffer ReuseFB12 (float buf[]) {
		if (FB12 == null) {
			float[] tmpFB = {
					0.0f, 0.0f, 0.0f, 
					0.0f, 0.0f, 0.0f, 
					0.0f, 0.0f, 0.0f, 
					0.0f, 0.0f, 0.0f,
					};
			FB12 = CreateFloatBuffer(tmpFB);
		}
		
		if (FB12 != null) {
			// copy the values
			FB12.rewind();
			FB12.put(buf, 0, 12);
			// update limit
			FB12.limit(12);
			FB12.rewind();	
		}
		return FB12;
	}

	public static FloatBuffer ReuseFloatBuffer (float buf[]) {
		
		int id = CurFrameFB;
		CurFrameFB++;

		FloatBuffer ReuseFB = null;
		
		if (TabReuseFB == null) {
			// RESET THE FB TABLE
			TabReuseFB = new FloatBuffer[MaxTabFB];
			for (int i=0; i< MaxTabFB; i++) {
				TabReuseFB[i] = null;
			}
		}

		if (id < MaxTabFB) {
			if (TabReuseFB[id] == null) {
				// initialise
				ReuseFB = CreateFloatBuffer(buf);
				// store
				TabReuseFB[id] = ReuseFB;
				return ReuseFB;
			}
			else {
				// reuse
				ReuseFB = TabReuseFB[id];
			}
		}

		if (ReuseFB == null) {
			// more FB needed
			// initialise
			ReuseFB = CreateFloatBuffer(buf);
		}
		else {
			int capa = ReuseFB.capacity();
			if (capa < buf.length) {
				// create another one
				ReuseFB = CreateFloatBuffer(buf);
				// store
				if (id < TabReuseFB.length)
					TabReuseFB[id] = ReuseFB;
			}
			else {				
				// copy the values
				ReuseFB.rewind();
				ReuseFB.put(buf);
				// update limit
				ReuseFB.limit(buf.length);
				ReuseFB.rewind();
				
				// get some stats
				CurFrameReuseCount++;
			}
		}


		return ReuseFB;
	}

	public static FloatBuffer CreateFloatBuffer (float buf[])
	{
		FloatBuffer ReturnBuffer;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(buf.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		ReturnBuffer = vbb.asFloatBuffer();
		ReturnBuffer.put(buf);
		ReturnBuffer.position(0);
		
		return ReturnBuffer;
	}

	public static FloatBuffer CreateFloatBuffer (float buf[], int blength)
	{
		FloatBuffer ReturnBuffer;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(blength * 4);
		vbb.order(ByteOrder.nativeOrder());
		ReturnBuffer = vbb.asFloatBuffer();
		if (buf != null)
			ReturnBuffer.put(buf);
		ReturnBuffer.position(0);
		
		return ReturnBuffer;
	}

	// USED ONLY ONCE
	public static ByteBuffer CreateByteBuffer (byte buf[])
	{
		ByteBuffer ReturnBuffer = ByteBuffer.allocateDirect(buf.length);
				
		ReturnBuffer.order(ByteOrder.nativeOrder());
		ReturnBuffer.put(buf);
		ReturnBuffer.position(0);
		
		return ReturnBuffer;
	}

	public static ByteBuffer CreateByteBuffer (byte buf[], int blength)
	{
		ByteBuffer ReturnBuffer = ByteBuffer.allocateDirect(blength);
		
		ReturnBuffer.order(ByteOrder.nativeOrder());
		if (buf != null)
			ReturnBuffer.put(buf, 0, blength);
		ReturnBuffer.position(0);
		
		return ReturnBuffer;
	}
	
	public static ShortBuffer CreateShortBuffer (short buf[])
	{
		ShortBuffer ReturnBuffer;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(buf.length * 2);
		vbb.order(ByteOrder.nativeOrder());
		ReturnBuffer = vbb.asShortBuffer();
		ReturnBuffer.put(buf);
		ReturnBuffer.position(0);
		return ReturnBuffer;
	}
	
	public static ShortBuffer CreateShortBuffer (short buf[], int blength)
	{
		ShortBuffer ReturnBuffer;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(buf.length * 2);
		vbb.order(ByteOrder.nativeOrder());
		ReturnBuffer = vbb.asShortBuffer();
		if (buf != null)
			ReturnBuffer.put(buf, 0, blength);
		ReturnBuffer.position(0);
		return ReturnBuffer;
	}
	
}
