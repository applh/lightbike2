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

package com.applh.lightbike;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.applh.lightbike.Preferences;

public class LightBike extends Activity {
    /** Called when the activity is first created. */
	private OpenGLView aView = null;
	public static int aResPref = -1;
		
    @Override
    public void onCreate (Bundle savedInstanceState) {
        
	   	super.onCreate(savedInstanceState);
	   	
	   	// Resources settings
	   	aResPref=R.layout.preferences;
	   	Preferences.aResPref=aResPref;
	   	
    	// GET SCREEN WIDTH AND HEIGHT
		WindowManager w = getWindowManager();
	    Display d = w.getDefaultDisplay();
	    int width = d.getWidth();
	    int height = d.getHeight();

	    // FULL SCREEN
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	     
		// BUILD THE APP CUSTOM VIEW
        aView = new OpenGLView(this, width, height);
        // ATTACH IT TO THE MAIN ACTIVITY
        setContentView(aView);
    }
    
    
    @Override
    public void onPause () {
    	if (aView != null)
    		aView.onPause();
    	super.onPause();
    }
    
    @Override
    public void onResume () {
    	if (aView != null)
    		aView.onResume();
    	super.onResume();
    }

    @Override
    public void onDestroy () {
    	if (isFinishing()) {
	    	if (aView != null) {
		        aView.onDestroy();
	    		aView = null;
	    	}
    	}
    	super.onDestroy();
    }

    /*

    @Override
    public void onRestart () {
    	if (aView != null)
    		aView.onRestart();
    	super.onRestart();
    }
    @Override
    public void onWindowFocusChanged (boolean focus) {
    	super.onWindowFocusChanged(focus);
    }   
*/
    
    //open menu when key pressed
     public boolean onKeyUp(int keyCode, KeyEvent event) {
    	 
     	if (aView != null) {
	        if (keyCode == KeyEvent.KEYCODE_MENU) {
	        	// OPEN THE SETTINGS MENU
	        	this.startActivity(new Intent(this, Preferences.class));
	        }
     	}        
        return super.onKeyUp(keyCode, event);
    }
}