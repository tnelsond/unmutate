package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

import java.lang.Math;

public class TCamera extends OrthographicCamera{
	float x, y, px, py;
	float vy = 0;
	float accel = 0.2f;
	float tolerance = 5f;
	float slowdown = 0.9f;
	float gy;
	Creature target = null;
	
	public TCamera(){
		super();
		x = y = px = py = 0;
	}

	public TCamera(Creature target){
		super();
		this.target = target;
		px = target.x;
		py = target.y;
		x = target.x;
		y = target.y;
	}

	public void setTarget(Creature tar){
		if(tar != null && target != null && tar != target){
			x = tar.x;
			y = tar.y;
			vy = 0;
		}
		target = tar;
	}

	public void update(float alpha){
		px = x;
		py = y;
		float pvy = vy;
		if(target != null){
			float targy = target.calculateY(alpha);
			x = target.calculateX(alpha) + target.width/2;

			if(target.onGround){
				gy = targy;
			}

			float hm = viewportHeight/6f;
			if(targy - hm > y){
				y = targy - hm;
			}
			if(targy + hm < y){
				y = targy + hm;
			}
			else{
				float diff = Math.abs(y - gy);
				if(diff > 10){
					if(y < gy){
						vy += .3;
					}
					else if(y > gy){
						vy += -.3;
					}
					if(vy < -3)
						vy = -3;
					else if(vy > 3)
						vy = 3;
				}
				else{
					vy *= .8f;
				}
			}
		}

		y += vy;
		float dx = x;
		float dy = y;
		vy *= slowdown;

/*
		if(dx - viewportWidth/2 < 0){
			dx = viewportWidth/2;
		}
		else if(Level.currentlevel.w * Level.currentlevel.tile > viewportWidth
					&& dx + viewportWidth/2 > Level.currentlevel.w * Level.currentlevel.tile){
			dx = Level.currentlevel.w * Level.currentlevel.tile - viewportWidth;
		}
*/

/*
		if(dy - viewportHeight/2 < 0){
			dy = viewportHeight/2;
		}
		else if(Level.currentlevel.h * Level.currentlevel.tile > viewportHeight
					&& dy + viewportHeight/2 > Level.currentlevel.h * Level.currentlevel.tile){
			dy = Level.currentlevel.h * Level.currentlevel.tile - viewportHeight;
		}
*/

		position.set(new Vector3(dx, dy, 0));

		super.update();
	}
}
