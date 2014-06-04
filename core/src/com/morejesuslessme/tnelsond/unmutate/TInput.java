package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;

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

import com.morejesuslessme.tnelsond.unmutate.genome.*;

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
		TextButton b = new TextButton("Deselect", game.game.bs);
		TextButton b2 = new TextButton("Breed", game.game.bs);
		TextButton b3 = new TextButton("Kill", game.game.bs);
		TextButton b4 = new TextButton("Switch", game.game.bs);
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
		b3.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y){
				kill();
			}
		});
		b4.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y){
				tab();
			}
		});

		b.setColor(0.5f, 0, 1, 1);
		b2.setColor(.3f, .5f, .0f, 1);
		b3.setColor(.9f, .0f, .0f, 1);
		b4.setColor(.0f, .2f, .4f, 1);
		table.add(b);
		table.row();
		table.add(b2);
		table.row();
		table.add(b3);
		table.row();
		table.add(b4);
		table.layout();
	}

	public final void deselect(){
		this.game.selectedCreature = null;
	}

	public final void kill(){
		this.game.kill(this.game.selectedCreature);
	}

	public final void breed(){
		int index = -1;
		Creature temp = null;
		Creature otherparent = null;
		if(game.selectedCreature == null)
			return;
		if(!game.selectedCreature.breedable)
			game.selectedCreature.checkForGrass(game.currentlevel);
		if(game.selectedCreature.breedable && game.selectedCreature.sex != Genome.Sex.STERILE){
			for(int i = 0; i < game.creatures.length; ++i){
				if(game.creatures[i] == null){
					index = i;
				}
				// TODO: make breeding check for grass spots under the creatures. Even if breedable is not true.
				else if(game.creatures[i] != game.selectedCreature
						&& game.selectedCreature.sex != game.creatures[i].sex
						&& game.creatures[i].sex != Genome.Sex.STERILE
						&& (int)(game.creatures[i].y/10) == (int)(game.selectedCreature.y/10)
						&& game.creatures[i].overlaps(game.selectedCreature)){
					if(!game.creatures[i].breedable){
						game.creatures[i].checkForGrass(game.currentlevel);
					}
					if(game.creatures[i].breedable){
						otherparent = game.creatures[i];
						temp = game.creatures[i].breed(game.selectedCreature, game);
					}
				}

				if(temp != null && index != -1)
					break;
			}
			if(index != -1 && temp != null){
				game.creatures[index] = temp;
				game.selectedCreature.vx = 4;
				otherparent.vx = -4;
				temp.vy = 4;
				otherparent.awake = true;
			}
		}
	}

	public final void tab(){
		if(game.selectedCreature != null){
			Creature temp = null;
			boolean found = false;
			for(Creature c : game.creatures){
				if(c == game.selectedCreature){
					found = true;
				}
				else if((temp == null && c != null) || (c != null && found)){
					temp = c;
					if(found){
						break;
					}
				}
			}
			if(temp != null)
				game.setCreature(temp);
		}
		else{
			for(Creature c : game.creatures){
				if(c != null){
					game.setCreature(c);
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
		else if(game.selectedCreature != null){
			int x = 0;
			int y = 0;
			if(Gdx.input.isKeyPressed(Keys.A)){
				--x;
			}
			if(Gdx.input.isKeyPressed(Keys.D)){
				++x;
			}
			if(Gdx.input.isKeyPressed(Keys.S)){
				--y;
			}
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
			case Keys.TAB:
				tab();
				break;
			case Keys.DEL:
				kill();
				break;
			case Keys.P:
				if(game.selectedCreature != null)
					System.out.println(String.format("color: (%f, %f, %f) eyeColor: (%f, %f, %f)",
							game.selectedCreature.color.r,
							game.selectedCreature.color.g,
							game.selectedCreature.color.b,
							game.selectedCreature.eyeColor.r,
							game.selectedCreature.eyeColor.g,
							game.selectedCreature.eyeColor.b
							));
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

	public void clickCreature(int x, int y){
		for(int i = game.creatures.length - 1; i >= 0; --i){
			Creature c = game.creatures[i];
			if(c != null){
				Vector3 pos = new Vector3(x, y, 0);
				game.viewport.unproject(pos);
				if(c.contains(pos.x, pos.y)){
					game.setCreature(c);
					break;
				}
			}
		}
	}

	public boolean touchDown (int x, int y, int pointer, int button) {
		if(game.selectedCreature == null){
			clickCreature(x, y);	
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
		if(sel.getDuration() < 400 && Math.abs(sel.x - sel.sx) < touchpadminradius && Math.abs(sel.y - sel.sy) < touchpadminradius){
			clickCreature(x, y);
		}

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

