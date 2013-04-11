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

package com.applh.lightbike.Game;

import java.nio.FloatBuffer;
import java.util.Random;

//import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES11;
import android.util.FloatMath;

import com.applh.lightbike.fx.ByteBufferManager;
import com.applh.lightbike.matrix.Vector3;

public class Camera {

	private CamType _cameraType;
	//private int _interpolated_cam;
	//private int _interpolated_target;
	private int _coupled;
	private int _freedom[] = new int[3];
	//private int _type;
	
	private long _steadyCount = 0;
	private long _steadyCountMax = 100;
	private long _deltaR = 1;
	private volatile boolean _isTravelling = true;
	
	// LH TODO: review interdependence
	//private Player _PlayerData;
	public Vector3 _target = new Vector3();
	public Vector3 _cam = new Vector3();
	private float _movement[] = new float[4]; // indices CAM_R, CAM_CHI, CAM_PHI, CAM_PHI_OFFSET
	
	private static final float CAM_CIRCLE_DIST = 15.0f;
	private static final float CAM_FOLLOW_DIST = 15.0f;
	private static final float CAM_FOLLOW_FAR_DIST = 30.0f;
	private static final float CAM_FOLLOW_CLOSE_DIST = 7.0f;
	private static final float CAM_FOLLOW_BIRD_DIST = 45.0f;
	
	private static final float CAM_CIRCLE_Z  = 8.0f;
	private static final float CAM_COCKPIT_Z = 4.0f;
	
	//private static final float CAM_SPEED = 0.000349f;
	//private static final float CAM_SPEED = 0.000698f;
	private static final float CAM_SPEED = 0.0005f;
	
	private static final int CAM_R = 0;
	private static final int CAM_CHI = 1;
	private static final int CAM_PHI = 2;
	private static final int CAM_PHI_OFFSET = 3;
	
	private static final int CAM_FREE_R = 0;
	private static final int CAM_FREE_PHI = 1;
	private static final int CAM_FREE_CHI = 2;
	
	private static final float CLAMP_R_MIN = 7.0f;
	private static final float CLAMP_R_MAX = 40.0f;
	private static final float CLAMP_CHI_MIN = (((float)Math.PI) / 8.0f);
	private static final float CLAMP_CHI_MAX = (3.0f * (float)Math.PI / 8.0f);
	
	private static final float B_HEIGHT = 0.0f;
	
	private static final float cam_defaults[][] = {
		{ CAM_CIRCLE_DIST, (float)Math.PI / 3.0f, 0.0f }, // circle
		{ CAM_FOLLOW_DIST, (float)Math.PI / 4.0f, (float)Math.PI / 72.0f }, // follow
		{ CAM_FOLLOW_FAR_DIST, (float)Math.PI / 4.0f, (float)Math.PI / 72.0f }, // follow far
		{ CAM_FOLLOW_CLOSE_DIST, (float)Math.PI / 4.0f, (float)Math.PI / 72.0f }, // follow close
		{ CAM_FOLLOW_BIRD_DIST, (float)Math.PI / 4.0f, (float)Math.PI / 72.0f }, // birds-eye view
		{ CAM_COCKPIT_Z,   (float)Math.PI / 8.0f, 0.0f }, // cockpit
		{ CAM_CIRCLE_DIST, (float)Math.PI / 3.0f, 0.0f } // free
	};
	
	private static final float camAngles[] = {
		(float) Math.PI / 2.0f, 
		0.0f, 
		3.0f * (float) Math.PI / 2.0f, 
		(float)Math.PI,
		2.0f * (float)Math.PI
	};
	
	public enum CamType {
		E_CAM_TYPE_CIRCLING,
		E_CAM_TYPE_FOLLOW,
		E_CAM_TYPE_FOLLOW_FAR,
		E_CAM_TYPE_FOLLOW_CLOSE,
		E_CAM_TYPE_BIRD,
		E_CAM_TYPE_COCKPIT,
		E_CAM_TYPE_MOUSE,
		E_CAM_TYPE_AUTO,
	}

	private final static float TmpM[] = new float[16];
	private final Vector3 TmpUp = new Vector3(0.0f, 0.0f, 1.0f);
	private final float TmpDest[] = new float[3];
	private final float TmpTDest[] = new float[3];

	public Camera(Player player, CamType camtype)
	{
		_cameraType = camtype;
		//_PlayerData = PlayerData;
		
		switch(camtype) {
			case E_CAM_TYPE_CIRCLING:
				initCircleCamera();
				break;
			case E_CAM_TYPE_FOLLOW:
			case E_CAM_TYPE_FOLLOW_FAR:
			case E_CAM_TYPE_FOLLOW_CLOSE:
			case E_CAM_TYPE_BIRD:
				initFollowCamera(camtype);
				break;
			case E_CAM_TYPE_COCKPIT:
				break;
			case E_CAM_TYPE_MOUSE:
				break;
			default:
				_cameraType = CamType.E_CAM_TYPE_FOLLOW;
				break;
		}
		
		// if auto then switch auto zoom on
		if (camtype == CamType.E_CAM_TYPE_AUTO) {
			setTravelling(true);
		}
		
		_target.v[0] = player.getXpos();
		_target.v[1] = player.getYpos();
		_target.v[2] = 0.0f;
		
		_cam.v[0] = player.getXpos()  + CAM_CIRCLE_DIST;
		_cam.v[1] = player.getYpos();
		_cam.v[2] = CAM_CIRCLE_Z;
		
	}
	
	public void updateType(CamType camtype)
	{
		_cameraType = camtype;
		_movement[CAM_R] = cam_defaults[camtype.ordinal()][CAM_R];
	}
	
	public boolean setTravelling(boolean b) {
		_isTravelling = b;
		return _isTravelling;
	}
	
	private void initCircleCamera()
	{
		_movement[CAM_R] = cam_defaults[0][CAM_R];
		_movement[CAM_CHI] = cam_defaults[0][CAM_CHI];
		_movement[CAM_PHI] = cam_defaults[0][CAM_PHI];
		_movement[CAM_PHI_OFFSET] = 0.0f;
		
		//_interpolated_cam = 0;
		//_interpolated_target = 0;
		_coupled = 0;
		_freedom[CAM_FREE_R] = 1;
		_freedom[CAM_FREE_PHI] = 0;
		_freedom[CAM_FREE_CHI] = 1;
	}
	
	private void initFollowCamera(CamType type)
	{
		_movement[CAM_R] = cam_defaults[type.ordinal()][CAM_R];
		_movement[CAM_CHI] = cam_defaults[type.ordinal()][CAM_CHI];
		_movement[CAM_PHI] = cam_defaults[type.ordinal()][CAM_PHI];
		_movement[CAM_PHI_OFFSET] = 0.0f;
		
		//_interpolated_cam = 1;
		//_interpolated_target = 0;
		_coupled = 1;
		_freedom[CAM_FREE_R] = 1;
		_freedom[CAM_FREE_PHI] = 1;
		_freedom[CAM_FREE_CHI] = 1;
	}
	
	private void clampCam()
	{
		if(_freedom[CAM_FREE_R] == 1) {
			if(_movement[CAM_R] < CLAMP_R_MIN) {
				_movement[CAM_R] = CLAMP_R_MIN;
			}
			if(_movement[CAM_R] > CLAMP_R_MAX) {
				_movement[CAM_R] = CLAMP_R_MAX;
			}
		}
		
		if(_freedom[CAM_FREE_CHI] == 1) {
			if(_movement[CAM_CHI] < CLAMP_CHI_MIN) {
				_movement[CAM_CHI] = CLAMP_CHI_MIN;
			}
			if(_movement[CAM_CHI] > CLAMP_CHI_MAX) {
				_movement[CAM_CHI] = CLAMP_CHI_MAX;
			}
		}
	}

	public void moveCameraHome (Player PlayerData, long CurrentTime, long dt) 
	{
		float phi,chi,r,x,y;
		
		clampCam();

		phi = _movement[CAM_PHI] + _movement[CAM_PHI_OFFSET];
		chi = _movement[CAM_CHI];
		r = _movement[CAM_R];
				
		x = PlayerData.getXpos();
		y = PlayerData.getYpos();
		
		// position the camera
		TmpDest[0] = x + r * FloatMath.cos(phi) * FloatMath.sin(chi);
		TmpDest[1] = y + r * FloatMath.sin(phi) * FloatMath.sin(chi);
		TmpDest[2] = r * FloatMath.cos(chi);
		
		_cam.v[0] = TmpDest[0];
		_cam.v[1] = TmpDest[1];
		_cam.v[2] = TmpDest[2];

		// LH NOTE
		// tdest is for target bike ? 
		

		switch (_cameraType) {
			case  E_CAM_TYPE_CIRCLING:
			default:
				// prepare next frame ?
				// change the angle PHI value for CIRCLING CAMERA
				//_movement[CAM_PHI] += CAM_SPEED * dt;
				_movement[CAM_PHI] += CAM_SPEED * dt;
								
				TmpTDest[0] = x;
				TmpTDest[1] = y;
				TmpTDest[2] = B_HEIGHT;
				break;
				
		}

		_target.v[0] = TmpTDest[0];
		_target.v[1] = TmpTDest[1];
		_target.v[2] = TmpTDest[2];

 	}
	
	private void playerCamera (Player PlayerData, long CurrentTime, long dt)
	{
		float phi,chi,r,x,y;
		long time;
		int dir;
		int ldir;
		
		clampCam();

		phi = _movement[CAM_PHI] + _movement[CAM_PHI_OFFSET];
		chi = _movement[CAM_CHI];
		r = _movement[CAM_R];
		
		if (_coupled == 1) {
			// do turn stuff here
			time = CurrentTime - PlayerData.TurnTime;
			if (time < PlayerData.TURN_LENGTH) {
				dir = PlayerData.getDirection();
				ldir = PlayerData.getLastDirection();
				if(dir == 1 && ldir == 2) {
					dir = 4;
				}
				if(dir == 2 && ldir == 1) {
					ldir = 4;
				}
				phi += ((PlayerData.TURN_LENGTH - time) * camAngles[ldir] +
						time * camAngles[dir]) / PlayerData.TURN_LENGTH;
			}
			else {
				phi += camAngles[PlayerData.getDirection()];
			}
		}
		
		x = PlayerData.getXpos();
		y = PlayerData.getYpos();
		
		// position the camera
		TmpDest[0] = x + r * FloatMath.cos(phi) * FloatMath.sin(chi);
		TmpDest[1] = y + r * FloatMath.sin(phi) * FloatMath.sin(chi);
		TmpDest[2] = r * FloatMath.cos(chi);
		
		// LH NOTE
		// tdest is for target bike ? 
		switch (_cameraType)
		{
			case  E_CAM_TYPE_CIRCLING:
				// prepare next frame ?
				// change the angle PHI value for CIRCLING CAMERA
				_movement[CAM_PHI] += CAM_SPEED * dt;
				
				TmpTDest[0] = x;
				TmpTDest[1] = y;
				TmpTDest[2] = B_HEIGHT;
				break;
				
			case E_CAM_TYPE_FOLLOW:
			case E_CAM_TYPE_FOLLOW_FAR:
			case E_CAM_TYPE_FOLLOW_CLOSE:
			case E_CAM_TYPE_BIRD:
				// prepare next frame ?
				if (_isTravelling) {
					// make travelling close/far of the bike ?
					_steadyCount +=1;
					if (_steadyCount > _steadyCountMax) {
						// reinit
						_deltaR = - _deltaR; // inverse camera movement
						_steadyCount = 0;
						Random rand = new Random();
						if (_deltaR > 0)
							_steadyCountMax = 1000 + rand.nextInt(200);
						else 
							_steadyCountMax = 50 + rand.nextInt(100);
					}					
				}
				// securised with CLAMP_R_MIN and CLAMP_R_MAX
				_movement[CAM_R] += _deltaR;

				TmpTDest[0] = x;
				TmpTDest[1] = y;
				TmpTDest[2] = B_HEIGHT;
				break;
		}
		
		_cam.v[0] = TmpDest[0];
		_cam.v[1] = TmpDest[1];
		_cam.v[2] = TmpDest[2];
		
		_target.v[0] = TmpTDest[0];
		_target.v[1] = TmpTDest[1];
		_target.v[2] = TmpTDest[2];
		
	}
	
	public FloatBuffer getCamBuffer ()
	{
		return ByteBufferManager.ReuseFB3(_cam.v);
	}
	
	public void doCameraMovement (Player PlayerData, long CurrentTime, long dt)
	{
		playerCamera(PlayerData,CurrentTime,dt);
	}
	
	public void doLookAt()
	{
		Vector3 x,y,z;
		
		z = _cam.sub(_target);
		z.Normalise();
		x = TmpUp.Cross(z);
		y = z.Cross(x);
		x.Normalise();
		y.Normalise();
		
		TmpM[0*4+0] = x.v[0];
		TmpM[1*4+0] = x.v[1];
		TmpM[2*4+0] = x.v[2];
		TmpM[3*4+0] = 0.0f;
		
		TmpM[0*4+1] = y.v[0];
		TmpM[1*4+1] = y.v[1];
		TmpM[2*4+1] = y.v[2];
		TmpM[3*4+1] = 0.0f;
		
		TmpM[0*4+2] = z.v[0];
		TmpM[1*4+2] = z.v[1];
		TmpM[2*4+2] = z.v[2];
		TmpM[3*4+2] = 0.0f;
		
		TmpM[0*4+3] = 0.0f;
		TmpM[1*4+3] = 0.0f;
		TmpM[2*4+3] = 0.0f;
		TmpM[3*4+3] = 1.0f;
		
		FloatBuffer M = ByteBufferManager.ReuseFloatBuffer(TmpM);
		GLES11.glMultMatrixf(M);
		
		// Translate Eye to origin
		GLES11.glTranslatef(-_cam.v[0], -_cam.v[1], -_cam.v[2]);
	}
	
}
