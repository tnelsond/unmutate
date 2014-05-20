package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input.Keys;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.Scaling;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.lang.Math;

public class TInput implements InputProcessor {

	float touchpadminradius = 10;
	float touchpadmaxradius = 30;
	AtlasRegion touchpadouter;
	AtlasRegion touchpadinner;
	TTouch touchpad;	
	TTouch othertouch;
	Table table;
	GameScreen game;
	Stage stage;
	ExtendViewport viewport;

	public TInput(GameScreen game){
		touchpadouter = game.game.atlas.findRegion("joystickouter");
		touchpadinner = game.game.atlas.findRegion("joystickinner");
		touchpad = new TTouch();
		othertouch = new TTouch();
		this.game = game;
		stage = new Stage(new ExtendViewport(800, 480), game.game.batch);
		table = new Table();
		table.align(Align.right | Align.top);
		table.setFillParent(true);
		table.debug();
		table.debugTable();
		stage.addActor(table);
		Drawable button_normal = (Drawable) new NinePatchDrawable(game.game.atlas.createPatch("button_normal"));
		Drawable button_pressed = (Drawable) new NinePatchDrawable(game.game.atlas.createPatch("button_pressed"));
		TextButtonStyle bs = new TextButtonStyle(button_normal, button_pressed, button_normal, game.game.font);
		TextButton b = new TextButton("Deselect", bs);
		TextButton b2 = new TextButton("Breed", bs);
		b.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y){
				deselect();
			}
		});
		b2.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y){
				breed();
			}
		});
		b.setColor(0.5f, 0, 1, 1);
		b2.setColor(.3f, .5f, .0f, 1);
		table.add(b);
		table.row();
		table.add(b2);
		table.layout();
	}

	public final void deselect(){
		this.game.selectedCreature = null;
	}

	public final void breed(){
		Creature temp = game.currentlevel.breeder1.breed(game.game.atlas);
		if(temp != null){
			for(int i = 0; i < game.creatures.length; ++i){
				if(game.creatures[i] == null){
					game.creatures[i] = temp;
					for(int j = 0; j < game.needUpdates.length; ++j){
						if(game.needUpdates[j] == null){
							game.needUpdates[j] = temp;
							return;
						}
					}
					return;
				}
			}
		}
	}

	public void update(){
		if(touchpad.pointer >= 0 && game.selectedCreature != null){
			float x = touchpad.x - touchpad.sx;
			boolean xpositive = x > 0;
			float y = touchpad.y - touchpad.sy;
			boolean ypositive = !(y > 0); // The ! compensates for the fact that libgdx has flipped y coordinates for the drawing
			x = MathUtils.clamp(Math.max(0, Math.abs(x) - touchpadminradius), 0, touchpadmaxradius)/touchpadmaxradius;
			y = MathUtils.clamp(Math.max(0, Math.abs(y) - touchpadminradius), 0, touchpadmaxradius)/touchpadmaxradius;
			if(!xpositive)
				x = -x;
			if(!ypositive)
				y = -y;
			game.selectedCreature.moveToward(x, y);
		}
	}

	public void render(){
		if(touchpad.pointer >= 0) {
			Vector3 pos = touchpad.oldToVector3();
			Vector3 pos2 = touchpad.toVector3();
			game.viewport.unproject(pos);
			game.viewport.unproject(pos2);
			game.game.batch.setColor(0.0f, 0, 1, .7f);
			game.game.batch.draw(touchpadouter, pos.x - touchpadmaxradius, pos.y - touchpadmaxradius, touchpadmaxradius*2, touchpadmaxradius*2);
			game.game.batch.setColor(1.0f, 1, 0, .7f);
			game.game.batch.draw(touchpadinner,
					Math.min(Math.max(pos2.x,
							pos.x - touchpadmaxradius), pos.x + touchpadmaxradius) - touchpadminradius,
					Math.min(Math.max(pos2.y,
							pos.y - touchpadmaxradius), pos.y + touchpadmaxradius) - touchpadminradius,
					touchpadminradius*2, touchpadminradius*2);
		}

		/*
		if(othertouch.pointer >= 0) {
			Vector3 pos = othertouch.toVector3();
			//game.viewport.unproject(pos);
			//game.shapeRenderer.setColor(1, 0.4f, 0, .5f);
			//game.shapeRenderer.circle(pos.x, pos.y, 20);
		}
		*/

		game.game.batch.end();
		
		stage.draw();
		table.drawDebug(stage);
	}

	public boolean keyDown (int keycode) {
		switch(keycode){
			case Keys.ENTER:
				game.selectedCreature = null;
				break;
			case Keys.B:
				breed();
				break;
			case Keys.W:
				if(game.selectedCreature != null)
					game.selectedCreature.moveToward(0, 1);				
				break;
		}
			
			
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
						int temp = -1;
						for(int i = 0; i < game.needUpdates.length; ++i){
							if(game.needUpdates[i] == null){
								temp = i;
							}
							else if(game.needUpdates[i] == c){
								return true;
							}
						}
						if(temp != -1)
							game.needUpdates[temp] = c;
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
		sel.sx = -1;
		
		return false;
	}

	public boolean touchDragged (int x, int y, int pointer) {
		TTouch sel = (pointer == touchpad.pointer ? touchpad : othertouch);
		sel.update(x, y);	
		if(game.selectedCreature == null){
			game.camera.position.x -= sel.x - sel.ox;
			game.camera.position.y += sel.y - sel.oy;
		}
	
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

