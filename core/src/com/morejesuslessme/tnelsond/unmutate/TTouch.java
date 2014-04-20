package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.math.Vector3;

class TTouch{
	public int ox; //old x
	public int oy; //old y
	public int x;
	public int y;
	public int pointer;	
	public boolean pan;

	public TTouch(){
		ox = -1;
		oy = -1;
		x = 0;
		y = 0;
		pan = false;
		pointer = -1;
	}

	public TTouch(boolean pan){
		this();
		this.pan = pan;
	}

	public void update(int x, int y){
		if(ox < 0){
			ox = x;
			oy = y;
		}
		if(pan){
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
		return new Vector3(ox, oy, 0);
	}
}
