package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Vector3;

class TButton extends Rectangle{
	Color color;
	public TButton(int x, int y, int w, int h, Color color){
		super(x, y, w, h);
		this.color = color;
	}
	public void draw(ShapeRenderer ren, Viewport view){
		ren.setColor(color);
		Vector3 pos = new Vector3(view.getViewportWidth() - width - 10, view.getViewportHeight() - height, 0);
		view.unproject(pos);
		ren.rect(pos.x, pos.y, width, height);
	}
}
