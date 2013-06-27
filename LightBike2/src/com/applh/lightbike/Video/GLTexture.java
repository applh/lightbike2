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

import android.opengl.GLES11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLUtils;

public class GLTexture {

	int aTexID[];
	private boolean boGenMipMap = true; // LH HACK ?
	
	int WrapS;
	int WrapT;
	
	public int DebugType;
	
	public GLTexture(Context context, int resource)
	{
		WrapS = GLES11.GL_REPEAT;
		WrapT = GLES11.GL_REPEAT;
		aTexID = new int[1];
		loadTexture(context,resource);
	}

	public GLTexture(Context context, int resource, int wrap_s, int wrap_t)
	{
		WrapS = wrap_s;
		WrapT = wrap_t;
		aTexID = new int[1];
		loadTexture(context,resource);
	}
	
	public GLTexture(Context context, int resource, int wrap_s, int wrap_t, boolean mipMap)
	{
		WrapS = wrap_s;
		WrapT = wrap_t;
		boGenMipMap = mipMap;
		aTexID = new int[1];
		loadTexture(context,resource);
	}

	// Will load a texture out of a drawable resource file, and return an OpenGL texture ID:
	private void loadTexture (Context context, int resource) {
	    
	    // In which ID will we be storing this texture?
		GLES11.glGenTextures(1, aTexID, 0);
	    
	    // We need to flip the textures vertically:
	    Matrix flip = new Matrix();
	    flip.postScale(1f, -1f);
	    
	    // This will tell the BitmapFactory to not scale based on the device's pixel density:
	    // (Thanks to Matthew Marshall for this bit)
	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inScaled = false;
	    
	    // Load up, and flip the texture:
	    Bitmap temp = BitmapFactory.decodeResource(context.getResources(), resource, opts);
	    
	    Bitmap bmp = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), flip, true);
	    temp.recycle();
	    
	    GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, aTexID[0]);
	    
	    // Set all of our texture parameters:
	    if (boGenMipMap) {
	    	GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MIN_FILTER, GLES11.GL_LINEAR_MIPMAP_NEAREST);
	    	// LH HACK
	    	//GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MAG_FILTER, GLES11.GL_LINEAR_MIPMAP_NEAREST);
	    	GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MAG_FILTER, GLES11.GL_LINEAR);
	    }
	    else
	    {
	    	GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MIN_FILTER, GLES11.GL_LINEAR);
	    	GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MAG_FILTER, GLES11.GL_LINEAR);
	    }
	    GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_WRAP_S, WrapS);
	    GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_WRAP_T, WrapT);

	    DebugType = GLUtils.getInternalFormat(bmp);
	    
	    // Generate, and load up all of the mipmaps:
	    if (boGenMipMap) {
		    for (int level=0, height = bmp.getHeight(), width = bmp.getWidth(); true; level++) {
		        // Push the bitmap onto the GPU:
		        GLUtils.texImage2D(GLES11.GL_TEXTURE_2D, level, bmp, 0);
		        
		        // We need to stop when the texture is 1x1:
		        if (height==1 && width==1) break;
		        
		        // Resize, and let's go again:
		        width >>= 1; height >>= 1;
		        if (width<1)  width = 1;
		        if (height<1) height = 1;
		        
		        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, width, height, true);
		        bmp.recycle();
		        bmp = bmp2;
		    }
	    }
	    else {
	    	GLUtils.texImage2D(GLES11.GL_TEXTURE_2D, 0, bmp, 0);
	    }
	    
	    bmp.recycle();
	    
	}

	public int getTextureID()
	{
		return aTexID[0];
	}
	
}
