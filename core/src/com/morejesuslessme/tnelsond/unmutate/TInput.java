package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input.Keys;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.lang.Math;

public class TInput implements InputProcessor {

	float touchpadminradius = 10;
	float touchpadmaxradius = 30;
	TTouch touchpad;	
	TTouch othertouch;
	Table table;
	GameScreen game;
	Stage stage;

	public TInput(GameScreen game){
		touchpad = new TTouch();
		othertouch = new TTouch(true);
		this.game = game;
		stage = new Stage(new ExtendViewport(800, 480), game.game.batch);
		table = new Table();
		table.align(Align.right);
		table.setFillParent(true);
		stage.addActor(table);
		//ButtonStyle bs = new ButtonStyle((Drawable)new TextureRegionDrawable(), (Drawable)new TextureRegionDrawable(game.currentlevel.yellowRegion), (Drawable)new TextureRegionDrawable(game.currentlevel.grassRegion));
		//table.addActor(new Button(bs));
	}

	public void update(){
		if(touchpad.pointer >= 0){
			float x = touchpad.x - touchpad.ox;
			boolean xpositive = x > 0;
			float y = touchpad.y - touchpad.oy;
			boolean ypositive = !(y > 0); // The ! compensates for the fact that libgdx has flipped y coordinates for the drawing
			x = MathUtils.clamp(Math.max(0, Math.abs(x) - touchpadminradius), 0, touchpadmaxradius)/touchpadmaxradius;
			y = MathUtils.clamp(Math.max(0, Math.abs(y) - touchpadminradius), 0, touchpadmaxradius)/touchpadmaxradius;
			if(!xpositive)
				x = -x;
			if(!ypositive)
				y = -y;
			game.selectedCreature.moveToward(x, y);
			stage.act(1);
		}
	}

	public void render(){
		if(touchpad.pointer >= 0) {
			Vector3 pos = touchpad.oldToVector3();
			game.viewport.unproject(pos);
			game.shapeRenderer.setColor(0.4f, 1, 0, .5f);
			game.shapeRenderer.circle(pos.x, pos.y, 30);
		}

		if(othertouch.pointer >= 0) {
			Vector3 pos = othertouch.toVector3();
			game.viewport.unproject(pos);
			game.shapeRenderer.setColor(1, 0.4f, 0, .5f);
			game.shapeRenderer.circle(pos.x, pos.y, 20);
		}

		stage.draw();
	}

	public boolean keyDown (int keycode) {
		if(keycode == Keys.ENTER)
			game.selectedCreature = null;
		return false;
	}

	public boolean keyUp (int keycode) {
		return false;
	}

	public boolean keyTyped (char character) {
		return false;
	}

	public boolean touchDown (int x, int y, int pointer, int button) {
		if(game.selectedCreature == null){
			for(Creature c : game.creatures){
				if(c != null){
					Vector3 pos = new Vector3(x, y, 0);
					game.viewport.unproject(pos);
					if(c.contains(pos.x, pos.y)){
						game.selectedCreature = c;			
						return true;
					}
				}
			}
			return true;
		}
		if(touchpad.pointer < 0){
			touchpad.pointer = pointer;
			touchpad.update(x, y);
		}
		else{
			othertouch.pointer = pointer;
			othertouch.update(x, y);
		}
		return false;
	}

	public boolean touchUp (int x, int y, int pointer, int button) {
		TTouch sel = (pointer == touchpad.pointer ? touchpad : othertouch);
		sel.pointer = -1;
		sel.ox = -1;
		
		return false;
	}

	public boolean touchDragged (int x, int y, int pointer) {
		TTouch sel = (pointer == touchpad.pointer ? touchpad : othertouch);
		sel.update(x, y);
		return false;
	}

	public boolean mouseMoved (int x, int y) {
		return false;
	}

	public boolean scrolled (int amount) {
		return false;
	}

	public void dispose() {
		stage.dispose();	
	}
}

