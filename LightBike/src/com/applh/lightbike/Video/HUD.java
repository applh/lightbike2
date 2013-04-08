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

import android.content.Context;
import android.opengl.GLES11;
import android.util.FloatMath;

import com.applh.lightbike.Game.LightBikeGame;
import com.applh.lightbike.fx.SetupGL;
import com.applh.lightbike.OpenGLRenderer;

public class HUD {
	
	private static Font aXenoFont = null;

	// console members
	private final int CONSOLE_DEPTH = 3;
	private String consoleBuffer[] = new String[CONSOLE_DEPTH];
	private int curConsole = 0;
	//private int offset;

	private int aMidX;
	
	private String aTextBottomL;
	private String aTextBottomR;

	private long aPower2display;
	private long aPower2bike;
	private int aPowerX0;
	private int aPowerY0;
	private int aPowerX;
	private int aPowerY;
	private int aPowerFont;
	private boolean aPowerReset;
	
	
	// win lose
	private boolean dispWinner = false;
	private boolean dispLoser = false;
	private boolean dispInst = true;
	private boolean dispFPS = true;
	
	private LightBikeGame game = null;
	
	public HUD (LightBikeGame g, Context c)
	{
		if (g == null) return;
		
		game = g;
		
		dispFPS = game.showInfoFPS;
		
	    // Load font
		aXenoFont = SetupGL.GetFont();
	    
	    emptyConsole();
	    
	    // FIXME
	    aPower2display = 100;
	    aPower2bike = 100;
	    aPowerReset = true;
	}
	
	public void drawInfo ()
	{
		//GLES11.glDisable(GLES11.GL_DEPTH_TEST);
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		game.aVisual.rasonly();
				
		// Draw fps
		if(dispFPS)
			drawFPS();
		drawBottom();

		drawWinLose();
		drawPower();
		drawConsole();

		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
	}

	public void drawInfoHome ()
	{
		//GLES11.glDisable(GLES11.GL_DEPTH_TEST);
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		game.aVisual.rasonly();
				
		// Draw fps
		if(dispFPS)
			drawFPS();

		drawBottom();
		drawConsole();
		drawInstructions();

		GLES11.glDisable(GLES11.GL_TEXTURE_2D);

	}
	
	public void emptyConsole()
	{		
		curConsole = 0;
	    for (int i = 0; i < consoleBuffer.length; i++) {
	    	consoleBuffer[i] = null;
	    }
	    
	    dispWinner = false;
	    dispLoser = false;

	    // FIXME
	    aPower2display = 100;
	    aPower2bike = 100;
		aPowerReset = true;
		aMidX = game.aVisual._iwidth/2;
	}

	public void displayWin()
	{
		if (dispLoser != true)
			dispWinner = true;
	}
	
	public void displayLose()
	{
		if (dispWinner != true)
			dispLoser = true;
	}
	
	public void displayInstr(Boolean value)
	{
		dispInst = value;

		// LH debug
		if (dispInst) {
			addLineToConsole("");
			addLineToConsole("LIGHT BIKE");
			addLineToConsole("BY APPLH.COM ***");			
		}

	}
	
	
	public void addLineToConsole (String str)
	{
		int curC = curConsole % consoleBuffer.length;		
		consoleBuffer[curC] = str;
		curConsole++;
	}
	
	private void drawBottom ()
	{
		long score = game.getScore(LightBikeGame.OWN_PLAYER);				
		long nbWalls = game.getTotalTrails();
		long freeMem = game.getGcMemory();

		aTextBottomL = String.format(
				"SCORE:%d BIKES:%d", 
				score, 
				LightBikeGame.aCurrentBikes 
				);
		aTextBottomR = String.format(
				"TRACKS:%d FB:%d RAM:%d", 
				nbWalls, 
				OpenGLRenderer.LastCountFB, 
				freeMem 
				);
		
		GLES11.glColor4f(1.0f, 1.0f, 0.2f, 1.0f);
		aXenoFont.drawText(aMidX, 16, 16, aTextBottomR);
		aXenoFont.drawText(16, 16, 24, aTextBottomL);
	}
	
	private void drawWinLose ()
	{
		String str = null;
		
		if(dispWinner) {
			str = "[+] YOU WIN [+]";
			GLES11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
		}
		else if(dispLoser) {
			str = "[X] CRASH [X]";
			GLES11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		}
		
		if(str != null)
		{
			aXenoFont.drawText(
					5, 
					game.aVisual._vp_h / 2, 
					(game.aVisual._vp_w / (6 / 4 * str.length())), 
					str);
		}
		
	}
	
	private void drawInstructions ()
	{		
		if (dispInst) {
			String str1 = null;
			String str2 = null;
			str1 = "[+] TOUCH SCREEN TO START [+]";
			str2 = ". . . or press menu key for settings . . .";
			
			GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			aXenoFont.drawText(
					5,
					game.aVisual._vp_h / 4,
					(game.aVisual._vp_w / (6 / 4 * str1.length())),
					str1);
			
			aXenoFont.drawText(
					5,
					game.aVisual._vp_h / 8,
					(game.aVisual._vp_w / (6/4 * str2.length())),
					str2);
				
		}
	}
	
	private void drawConsole ()
	{
		int i0 = 0;
		if (curConsole >= consoleBuffer.length) {
			// i0 is same to console Cursor
			i0 = curConsole % consoleBuffer.length;
		}
		int lines = consoleBuffer.length; // lines of console to display
		for (int i=0; i < lines; i++) {
			//index = (position + i - lines - offset + CONSOLE_DEPTH) % CONSOLE_DEPTH;
			int i1 = (i0 + i) % consoleBuffer.length;
			if (consoleBuffer[i1] != null) {
				int size = 24;								
				GLES11.glColor4f(1.0f, 1.0f, 0.2f, 1.0f);
				aXenoFont.drawText(16, game.aVisual._iheight - 20 * (i + 1), size, consoleBuffer[i1]);
			}
		}
	}
	
	private void drawPower ()
	{
		// update the value
		long curP = game.getPower(0);
		if (curP != aPower2bike) {
			// VALUE CHANGED
			// RESET ANIMATION
			aPower2bike = curP;
			aPowerReset = true;
		}

		// animate
		if (aPower2display > aPower2bike) {
			aPower2display--;
		}
		else if (aPower2display < aPower2bike) {
			aPower2display++;
		}
		
		if (aPowerX != aPowerX0) {
			int dx = 1 + (int) Math.floor(0.1f * (aPowerX - aPowerX0));
			aPowerX -=dx;
		}
		if (aPowerY != aPowerY0) {
			int dy = 1 + (int) Math.floor(0.1f * (aPowerY - aPowerY0));
			aPowerY -= dy;
		}
		
		String textPower = String.format(
				"POWER %d", 
//				aPower2display
				aPower2bike
				);

//		int delta = (int) Math.abs(aPower2display - aPower2bike);
		float ratio = 1.0f - aPower2display/100.0f;
//		float ratio0 = 1.0f - aPower2bike/100.0f;
//		int fontSize = 32 + (int) Math.floor(32 * ratio0);
		
		float red = ratio;
		float green = 1.0f - ratio;
		float blue = FloatMath.sin((float) (ratio * Math.PI));
		GLES11.glColor4f(red, green, blue, 1.0f);

		//int dX = 8 * delta;
		//int dY = 8 * delta;
//		aXenoFont.drawText(game.aVisual._iwidth/2 - dX, game.aVisual._iheight - 20 - fontSize - dY, fontSize, textPower);
		if (aPowerReset) {
			aPowerFont = 24 + (int) (32 * (1.0f - aPower2bike/100.0f));

			//aPowerX0 = game.aVisual._iwidth/2;
			//aPowerY0 = game.aVisual._iheight - 8 - aPowerFont;
			aPowerX0 = 16;
			aPowerY0 = game.aVisual._iheight - 55 - aPowerFont;
			
//			aPowerX = (int) (game.aVisual._iwidth  * 0.30);
			aPowerX = aMidX;
			aPowerY = (int) (game.aVisual._iheight * 0.60);
			
			aPowerReset = false;
		}

		aXenoFont.drawText(aPowerX, aPowerY, aPowerFont, textPower);
	}

	private void drawFPS ()
	{
		long fpsPlay = game.getFPS();
		//long fpsMinPlay = game.getFPSmin();
		long fpsAvgPlay = game.getFPSavg();
		long fpsMaxPlay = game.getFPSmax();		
		long playTime = game.getPlayTime() / 100;

		String textFPS = String.format(
				"TIME:%d FPS:%d/%d/%d/", 
				playTime, 
				fpsPlay, 
				fpsAvgPlay, 
				fpsMaxPlay
				);
		
		GLES11.glColor4f(1.0f, 1.0f, 0.2f, 1.0f);
		aXenoFont.drawText(aMidX, game.aVisual._iheight - 20, 16, textFPS);
	}
}
