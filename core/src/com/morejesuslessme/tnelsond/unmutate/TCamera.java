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
		if(target != null){
			float targy = target.calculateY(alpha);
			x = target.calculateX(alpha) + target.width/2;

			if(target.onGround){
				gy = targy;
			}

			float hm = viewportHeight*.27f;
			if(targy - hm + target.height > y){
				y = targy - hm + target.height;
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
		vy *= slowdown;

		if(x - viewportWidth/2 <= 0 || viewportWidth > Level.currentlevel.w * Level.currentlevel.tile){
			x = viewportWidth/2;	
		}
		else if(x + viewportWidth/2 >= Level.currentlevel.tile * Level.currentlevel.w){
			x = Level.currentlevel.w * Level.currentlevel.tile - viewportWidth/2;	
		}
		if(y - viewportHeight/2 <= 0 || viewportHeight > Level.currentlevel.h * Level.currentlevel.tile){
			y = viewportHeight/2;	
		}
		else if(y + viewportHeight/2 >= Level.currentlevel.tile * Level.currentlevel.h){
			y = Level.currentlevel.h * Level.currentlevel.tile - viewportHeight/2;	
		}

		position.set(new Vector3(px + (x - px) * alpha, py + (y - py) * alpha, 0));

		super.update();
	}
}
