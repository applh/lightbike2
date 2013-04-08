/*
 * Copyright © 2012 Iain Churcher
 * Copyright © 2012 Noah NZM Tech
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

import com.applh.lightbike.R;

import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.ListView;

public class Preferences extends PreferenceActivity {
	
	public static long LastUpdate = SystemClock.uptimeMillis();
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        // TODO Auto-generated method stub
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.layout.preferences);
	    }
	 
	 @Override
	 protected void onListItemClick (ListView l, View v, int position, long id) {
		 super.onListItemClick(l, v, position, id);
	 }
	 @Override
	 protected void onStop () {
		 // update timestamp
		 LastUpdate = SystemClock.uptimeMillis();
		 
		 super.onStop();
	 }

}
