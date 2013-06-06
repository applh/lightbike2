/*
 * free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License. 
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.applh.lightbike;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.widget.Toast;

// OUR CUSTOM OPEN GL VIEW
public class OpenGLView extends GLSurfaceView {
	
	private OpenGLRenderer aRenderer = null;
	
	private float aX = 0;
	private float aY = 0;
	private Context aContext = null;
	private boolean aIsPaused = false;
	
	public OpenGLView (Context context, int width, int height) {
		super(context);
	
		aContext = context;
		
		showToast("GAME LOADING... PLEASE WAIT... " + LightBike.aGameVersion);
		
		// FIXME
		aRenderer = null;
		
		if (aRenderer == null) {
			// BUILD OUR CUSTOM RENDERER
			aRenderer = new OpenGLRenderer(this, context, width, height);
			// ATTACH THE RENDERER TO THIS VIEW
			setRenderer(aRenderer);
		}

	}
	
	public void showToast (String txt) {
	    Context appC = aContext.getApplicationContext();
	    int duration = Toast.LENGTH_LONG;
	    Toast toast = Toast.makeText(appC, txt, duration);
	    toast.show();	
	}
	
	public void onPause () {
		aIsPaused = true;
		
		// DELEGATE TO RENDERER
		if (aRenderer != null)
			//showToast("<<< PAUSE >>>");
			aRenderer.onPause();
	}
	
	public void onResume() {
		aIsPaused = false;
		// DELEGATE TO RENDERER
		if (aRenderer != null) {
			//showToast("<<< RESUME >>>");
			aRenderer.onResume();
		}
	}

	public void onDestroy () {
		if (aRenderer != null) {
			//showToast("<<< GAME EXIT ... PLEASE WAIT >>>");
			aRenderer.onDestroy();
		}
	}

	public void onStart () {
		if (aRenderer != null) {
			//showToast("<<< START >>>");
		}
	}
/*
	public void onRestart () {
		if (aRenderer != null) {
			showToast("<<< RESTART >>>");
		}
	}

*/
	public boolean onTouchEvent (final MotionEvent event) {
		
		if (! aIsPaused) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				aX = event.getX();
				aY = event.getY();
				if (aRenderer != null)
					aRenderer.onTouch(aX, aY);
			}
			else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				aX = event.getX();
				aY = event.getY();
				if (aRenderer != null)
					aRenderer.onMove(aX, aY);
			}
		}
		
		return true;
	}
}
