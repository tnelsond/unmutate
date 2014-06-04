package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.TimeUtils;

import com.badlogic.gdx.Preferences;

import java.lang.Math;

import com.morejesuslessme.tnelsond.unmutate.genome.Genome;

public class GameScreen implements Screen {
	public OrthographicCamera camera;
	public Viewport viewport;

	final Unmutate game;

	public ShapeRenderer shapeRenderer;

	public Level currentlevel;
	Color backgroundColor;

	public int vieww = 500;
	public int viewh = 300;

	private double accumulator = 0.0;
	private float physicsStep = 1.0f / 60.0f;

	private TInput control;

	Creature creatures[];
	Creature selectedCreature = null;
	AtlasRegion halo;

	public final void kill(Creature c){
		if(c != null){
			for(int i = 0; i < creatures.length; ++i){
				if(creatures[i] == c){
					creatures[i] = null;
					if(selectedCreature == c)
						selectedCreature = null;
					break;
				}
			}
		}
	}

	public GameScreen(final Unmutate game) {
		this.game = game;

		Gdx.graphics.setContinuousRendering(true);

		backgroundColor = new Color(0.4f, 0.8f, 1f, 1);
		halo = game.atlas.findRegion("halo");

		currentlevel = Level.makeLevel(game);

		shapeRenderer = new ShapeRenderer();
		creatures = new Creature[40];

		loadCreatures();

		// shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		viewport = new ExtendViewport(vieww, viewh, camera);

		control = new TInput(this);
		InputMultiplexer im = new InputMultiplexer((InputProcessor)control.stage);
		im.addProcessor(control);
		Gdx.input.setInputProcessor(im);

		Gdx.gl.glClearColor(currentlevel.skyColor.r, currentlevel.skyColor.g, currentlevel.skyColor.b, 1);
	}

	public void loadCreatures(){
		for(int i = 0; i < currentlevel.spawns.length; ++i){
			String key = ((i % 2 == 0) ? "male" : "female") + i/2;
			String str = currentlevel.pref.getString(key, "null");
			boolean load = true;
			Genome g = null;
			if(str.equals("null") && !currentlevel.carryover){
				if(i <= 1){
					creatures[i] = new Creature(currentlevel.spawns[i].c * currentlevel.tile, currentlevel.spawns[i].r * currentlevel.tile, Level.getGenome(null, null, null, (i % 2 == 0) ? false : true), game.atlas);
					//str = game.json.toJson(g);
					//pref.putString(key, str);
				}
			}
			else{
				creatures[i] = new Creature(currentlevel.spawns[i].c * currentlevel.tile, currentlevel.spawns[i].r * currentlevel.tile, (g == null) ? Level.getGenome(null, game.json, str, false) : g, game.atlas);
			}
		}
	}

	public void setCreature(Creature c){
		selectedCreature = c;
		c.awake = true;
	}

	public void draw(final float alpha) {
		Vector3 pos = new Vector3(camera.position.x, camera.position.y, 0);
		if (selectedCreature != null) {
			pos.x = selectedCreature.px + alpha * (selectedCreature.x - selectedCreature.px);
			pos.y = selectedCreature.py + alpha * (selectedCreature.y - selectedCreature.py);
		}
		if(pos.x - camera.viewportWidth/2 < 0)
			pos.x = camera.viewportWidth/2;
		if(currentlevel.w * currentlevel.tile > camera.viewportWidth){
			if(pos.x + camera.viewportWidth/2 > currentlevel.w * currentlevel.tile)
				pos.x = currentlevel.w * currentlevel.tile - camera.viewportWidth/2;
		}
		if(pos.y - camera.viewportHeight/2 < 0)
			pos.y = camera.viewportHeight/2;
		if(currentlevel.h * currentlevel.tile > camera.viewportHeight){
			if(pos.y + camera.viewportHeight/2 > currentlevel.h * currentlevel.tile)
				pos.y = currentlevel.h * currentlevel.tile - camera.viewportHeight/2;
		}

		camera.position.set(pos);
		camera.update();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();

		game.batch.disableBlending();

		currentlevel.draw(game.batch, camera.position, viewport.getWorldWidth(), viewport.getWorldHeight());

		game.batch.enableBlending();

		for (Creature c : creatures) {
			if (c != null) {
				c.draw(game.batch, alpha);
			}
		}

		if(selectedCreature != null){
			game.batch.setColor(1, 0.5f, 0, 1);
			game.batch.draw(halo, selectedCreature.x + selectedCreature.width/2 - 16, selectedCreature.y + selectedCreature.height, 32, 32);
		}

		control.render();
	}

	@Override
	public void render(float delta) {
		double time = Math.min(delta, 0.25);

		accumulator += time;
		while(accumulator >= physicsStep){ // Physics loop
			control.update();
			currentlevel.update();
			//control.stage.act(); //maybe don't need
			boolean any = false;
			for(int i = creatures.length - 1; i >= 0; --i){
				Creature c = creatures[i];
				if(c != null && c.awake) {
					if(c != selectedCreature && c.onGround && Math.abs(c.vx) < .0001 && c.vy > -0.0001 && c.tick > 3){
						c.awake = false;
						c.vy = 0;
						c.tick = 0;
					}
					else{
						c.update();
					}
				}
				if(c != null){
					any = true;
					if(c.dead){
						kill(c);	
					}
					else if(c.ascend){
						currentlevel.ascend(c);	
					}
				}
			}
			if(!any){
				currentlevel.nextLevel(this);
			}
			accumulator -= physicsStep;
		}
		final float alpha = (float)(accumulator / physicsStep);
		draw(alpha);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		control.stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
	}
}
