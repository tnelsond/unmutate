package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.graphics.Color;

public class Special{
	public Color outside;
	public Color inside;

	public Special(Color outside, Color inside){
		this.outside = outside;
		this.inside = inside;
	}

	public boolean isSolid(Creature c){
		if(outside != null){
			if(!outside.equals(c.color)){
				return false;
			}
		} 
	
		if(inside != null){
			if(!inside.equals(c.eyeColor)){
				return false;
			}
		}

		return true;
	}	
}
