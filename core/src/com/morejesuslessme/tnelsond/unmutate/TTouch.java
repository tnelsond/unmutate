package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.math.Vector3;

class TTouch{
	public int sx;
	public int sy;
	public int ox; //old x
	public int oy; //old y
	public int x;
	public int y;
	public int pointer;	

	public TTouch(){
		ox = -1;
		oy = -1;
		x = 0;
		y = 0;
		sx = -1;
		sy = -1;
		pointer = -1;
	}

	public void update(int x, int y){
		if(sx < 0){
			sx = x;
			sy = y;
			ox = x;
			oy = y;
		}
		else {
			ox = this.x;
			oy = this.y;
		}
		this.x = x;
		this.y = y;	
	}

	public Vector3 toVector3(){
		return new Vector3(x, y, 0);
	}

	public Vector3 oldToVector3(){
		return new Vector3(sx, sy, 0);
	}
}
