package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.lang.Math;

public class Breeder{
	public static Color MALECOLOR = new Color(.1f, .7f, .7f, 1);
	public static Color FEMALECOLOR = new Color(.7f, .2f, .7f, 1);
	public static Color CHILDCOLOR = new Color(.7f, .7f, .1f, 1);
	public int row;
	public int colFemale;
	public int colMale;
	public Creature female;
	public Creature male;
	public Breeder(int row, int col1, int col2){
		male = female = null;
		this.row = row;
		this.colFemale = col1;
		this.colMale = col2;
	}

	public Creature breed(TextureAtlas atlas){
		if(male != null && female != null)
			return male.breed(female, atlas);
		return null;
	}

	public Color femaleColor(){
		if(female == null)
			return FEMALECOLOR.cpy().mul(.7f, .7f, .7f, 1);
		return FEMALECOLOR;
	}

	public Color maleColor(){
		if(male == null)
			return MALECOLOR.cpy().mul(.7f, .7f, .7f, 1);
		return MALECOLOR;
	}

	public Color childColor(){
		if(male == null || female == null)
			return CHILDCOLOR.cpy().mul(.7f, .7f, .7f, 1);
		return CHILDCOLOR;
	}

	public void update(){
		if(male != null){
			if(Math.abs(male.vx) > .001 || Math.abs(male.vy) > .001)
				male = null;
		}
		if(female != null){
			if(Math.abs(female.vx) > .001 || Math.abs(female.vy) > .001)
				female = null;
		}
	}
}
