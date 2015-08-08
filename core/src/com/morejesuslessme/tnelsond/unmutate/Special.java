package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.graphics.Color;
import java.lang.Math;
import com.badlogic.gdx.Gdx;

public class Special{
	public Color color;
	public boolean eye;

	public Special(Color color, float eye){
		this.color = color;
		this.eye = eye >= .9f;
	}

	public boolean equals(Color other){
		return 
			Math.abs(color.r - other.r) < 0.01 &&
			Math.abs(color.g - other.g) < 0.01 &&
			Math.abs(color.b - other.b) < 0.01;
	}

	public boolean isSolid(Creature c){
		if(color != null){
			Gdx.app.log(Unmutate.tag, "color.equals: " + color.equals(c.color));
			return ((!eye && !equals(c.color)) || (eye && !equals(c.eyeColor)));
		} 
		return true;
	}	
}
