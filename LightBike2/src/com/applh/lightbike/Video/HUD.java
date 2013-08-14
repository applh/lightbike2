/*
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

import com.applh.lightbike.Game.LightBikeGame;
import com.applh.lightbike.fx.SetupGL;

public class HUD {
	
	public long aPower2ref;
	public long aPower2display;
	public long aPower2bike;

	public int aPowerFontMin = 24;
	public int aPowerFont = 24;

	public int aFont0 = 32;
	public int aFont1 = 24;
	public int aFont2 = 16;

	private static Font aXenoFont = null;

	// console members
	private final int CONSOLE_DEPTH = 3;
	private String consoleBuffer[] = new String[CONSOLE_DEPTH];
	private int curConsole = 0;
	//private int offset;

	private int aMidX;
	
	private String aTextBottomL0;
	private String aTextBottomL1;
	private String aTextBottomR;

	private int aPowerX0;
	private int aPowerY0;
	private int aPowerX;
	private int aPowerY;
	private boolean aPowerReset;
	
	
	// win lose
	private boolean aDispWinner = false;
	private boolean aDispLoser = false;
	
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
	    aPower2ref = 100;
	    aPower2display = aPower2ref;
	    aPower2bike = aPower2ref;
	    aPowerReset = true;
	    
	    // setting font size
	    // 720 => 16, 768 => 16, 1080 => 32
    	aFont0 = 32;
	    aFont1 = 24; 	    	
	    aFont2 = 16;
	    // adjust to various screen sizes
	    if (game.aVisual._iheight > 999) {
	    	aFont0 = 40;
		    aFont1 = 32;
	    	aFont2 = 24;
	    }
	    else if (game.aVisual._iheight < 666) {
	    	aFont0 = 24;
		    aFont1 = 24;
	    	aFont2 = 16;
	    }
	    
	    aPowerFont = aFont1;
	    aPowerFontMin = aFont1;
	}
	

	public void drawInfo ()
	{
		//GLES11.glDisable(GLES11.GL_DEPTH_TEST);
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		game.aVisual.rasonly();
				
		// Draw fps
		if(dispFPS)
			drawFPS();
		drawBottom();
		
		if (aDispWinner || aDispLoser)
			drawWinLose();
		else
			drawCommands();

		drawPower();
		drawConsole();

		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		GLES11.glDisable(GLES11.GL_BLEND);
	}

	
	public void drawInfoHome ()
	{
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);

		game.aVisual.rasonly();
				
		// Draw fps
		if(dispFPS)
			drawFPS();

		drawBottom();
		
		drawConsole();
		drawInstructions();

		drawCursor();
		
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		GLES11.glDisable(GLES11.GL_BLEND);

	}
	
	public void emptyConsole()
	{		
		curConsole = 0;
	    for (int i = 0; i < consoleBuffer.length; i++) {
	    	consoleBuffer[i] = null;
	    }
	    
	    aDispWinner = false;
	    aDispLoser = false;

	    // FIXME
	    aPower2display = aPower2ref;
	    aPower2bike = aPower2ref;
		aPowerReset = true;
		aMidX = game.aVisual._iwidth/2;
	}

	public void displayYouWin()
	{
		if (aDispLoser != true) {
			aDispWinner = true;
		    aDispLoser = false;
		}
	}
	
	public void displayCrash()
	{
		if (aDispWinner != true) {
			aDispLoser = true;
			aDispWinner = false;
		}
	}
	
	public void displayInstr(Boolean value)
	{
		dispInst = value;

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

	public void drawCommands () 
	{
		GLES11.glColor4f(0.8f, 0.8f, 0.8f, 0.5f);
		aXenoFont.drawText(
				5,
				game.aVisual._vp_h / 2,
				(game.aVisual._vp_w / (6 / 4 * 14)),
				"[+]        [+]");

		int x8 = aMidX / 5;
		int ts = aFont0 * 3 / 2;
		int ts2 = 2*ts;
		
		GLES11.glColor4f(1.0f, 1.0f, 0.2f, 0.8f);
		aXenoFont.drawText(3*x8 -ts, ts2, aFont0, "-M-");	
		aXenoFont.drawText(5*x8 -ts, ts2, aFont0, "(0)");	
		aXenoFont.drawText(7*x8 -ts, ts2, aFont0, "+M+");	
		
	
	}

	public void drawCursor () 
	{
		int x=(int) game.aMoveX;
		int y=(int) game.aVisual._iheight - (int) game.aMoveY;
		int s=aFont0;
		GLES11.glColor4f(1.0f, 1.0f, 0.2f, 1.0f);
		aXenoFont.drawText(x, y, s, "+");
	}
	
	private void drawBottom ()
	{
		long maxPower = game.getMaxPower(LightBikeGame.OWN_PLAYER);
		long curHacking = game.aPowerUpDamage;

		aTextBottomL0 = String.format(
				"HACKING:%d%%", 
				curHacking
				);
		aTextBottomL1 = String.format(
				"HIGH SCORE:%d", 
				maxPower
				);
		aTextBottomR="";
		
		GLES11.glColor4f(1.0f, 1.0f, 0.2f, 1.0f);
		aXenoFont.drawText(16, 8,         aFont1, aTextBottomL1);
		aXenoFont.drawText(16, 16+aFont1, aFont1, aTextBottomL0);

		if (!aDispLoser || !aDispWinner) {
			if (LightBikeGame.aCurrentBikes > 2) {
				aTextBottomR = String.format(
						"X %d/%d BIKES", 
						LightBikeGame.aCurrentBikes-1,
						game.aNbPlayers1-1
					);
			}
			else if (LightBikeGame.aCurrentBikes > 1) {
				aTextBottomR = String.format(
						"X %d/%d BIKE", 
						LightBikeGame.aCurrentBikes-1,
						game.aNbPlayers1-1
					);
			}
			aXenoFont.drawText(aMidX, 8, aFont1, aTextBottomR);
		}

	}
	
	private void drawWinLose ()
	{
		String str = null;
		
		if (aDispWinner) {
			str = "[+]  YOU WIN  [+]";
			GLES11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
		}
		else if (aDispLoser) {
			str = "[X]  CRASH  [X]";
			GLES11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		}
		
		if (str != null) {
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
			String str0 = null;
			String str1 = null;
			String str2 = null;
			str0 = "[+]        [+]";
			str1 = "        TOUCH YOUR BIKE TO START        ";
			str2 = "                [options]                ";
			
			GLES11.glColor4f(0.8f, 0.8f, 0.8f, 0.8f);
			
			aXenoFont.drawText(
					5,
					game.aVisual._vp_h / 2,
					(game.aVisual._vp_w / (6 / 4 * str0.length())),
					str0);

			GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			aXenoFont.drawText(
					5,
					game.aVisual._vp_h / 3,
					(game.aVisual._vp_w / (6 / 4 * str1.length())),
					str1);
			
			GLES11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);
			aXenoFont.drawText(
					5,
					game.aVisual._vp_h / 7,
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
				GLES11.glColor4f(1.0f, 1.0f, 0.2f, 1.0f);
				aXenoFont.drawText(16, game.aVisual._iheight - aFont1 * (i + 1), aFont1, consoleBuffer[i1]);
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
				aPower2bike
				);

		float ratio = 0.0f;
		float red = ratio;
		float green = 1.0f - ratio;
		float blue = (float) Math.sin((float) (ratio * Math.PI));
		
		if (aPower2bike > aPower2ref) {
			red = 0;
			green = 1.0f;
			blue = 0;
			aPowerFont = aPowerFontMin;				
		}
		else {
			ratio = 1.0f - aPower2bike/(1.0f * aPower2ref);
			red = ratio;
			green = 1.0f - ratio;
			blue = (float) Math.sin((float) (ratio * Math.PI));

			aPowerFont = aPowerFontMin + (int) (aPowerFontMin * (1.0f - aPower2bike/aPower2ref));				
		}
		if (aPowerFont < aPowerFontMin) 
			aPowerFont = aPowerFontMin;
		
		if (aPowerReset) {			
			aPowerX0 = 16;
			aPowerY = (int) (game.aVisual._iheight * 0.60f);
			
			aPowerX = aMidX;
			aPowerY0 = (int) (game.aVisual._iheight - 5 * aFont1);
			
			aPowerReset = false;
		}
		GLES11.glColor4f(red, green, blue, 1.0f);

		aXenoFont.drawText(aPowerX, aPowerY, aPowerFont, textPower);
	}

	private void drawFPS ()
	{
		long fpsPlay = game.getFPS();
		//long fpsMinPlay = game.getFPSmin();
		long fpsAvgPlay = game.getFPSavg();
		long fpsMaxPlay = game.getFPSmax();		
		long playTime = game.getPlayTime() / 100;

		long freeMem = game.getGcMemory();

		String textFPS = String.format(
				"FPS:%d/%d/%d/ TIME:%d RAM:%d", 
				fpsPlay, 
				fpsAvgPlay, 
				fpsMaxPlay,
				playTime, 
				freeMem
				);
		
		GLES11.glColor4f(1.0f, 1.0f, 0.2f, 1.0f);
		aXenoFont.drawText(aMidX, game.aVisual._iheight - aFont1, aFont2, textFPS);
	}
}
