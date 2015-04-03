package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.graphics.Color;

public class Special{
	public Color color;
	public boolean eye;

	public Special(Color color, float eye){
		this.color = color;
		this.eye = eye >= .9f;
	}

	public boolean isSolid(Creature c){
		if(color != null){
			return ((!eye && !color.equals(c.color)) || (eye && !color.equals(c.eyeColor)));
		} 
		return false;
	}	
}
