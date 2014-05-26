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

import java.lang.Math;

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
	Creature selectedCreature;
	Creature needUpdates[];
	AtlasRegion halo;

	public GameScreen(final Unmutate game) {
		this.game = game;
	
		backgroundColor = new Color(0.4f, 0.8f, 1f, 1);
		halo = game.atlas.findRegion("halo");
		currentlevel = new Level(game.atlas, "levels/1.txt");
		shapeRenderer = new ShapeRenderer();
		creatures = new Creature[800];
		needUpdates = new Creature[10];

		Genome g1 = new Genome(
				new Allele[][][] {
						{ { Allele.DOM, Allele.DOM },
							{ Allele.DOM, Allele.REC },
							{ Allele.DOM, Allele.DOM },
							{ Allele.REC, Allele.DOM },
							{ Allele.MUT, Allele.DOM }, },
						{ { Allele.REC, Allele.DOM },
							{ Allele.REC, Allele.DOM },
							{ Allele.DOM, Allele.REC }, },
						{ { Allele.DOM, Allele.DOM },
							{ Allele.DOM, Allele.MUT },
							{ Allele.DOM, Allele.MUT }, },
						{ { Allele.FEMALE, Allele.FEMALE },
							{ Allele.DOM, Allele.REC }}});

		Genome g2 = new Genome(
				new Allele[][][] {
						{ { Allele.MUT, Allele.REC },
							{ Allele.MUT, Allele.REC },
							{ Allele.REC, Allele.MUT },
							{ Allele.MUT, Allele.REC },
							{ Allele.REC, Allele.MUT }, },
						{ { Allele.REC, Allele.DOM },
							{ Allele.REC, Allele.REC },
							{ Allele.REC, Allele.REC }, },
						{ { Allele.REC, Allele.REC },
							{ Allele.REC, Allele.REC },
							{ Allele.REC, Allele.MUT }, },
						{ { Allele.MALE, Allele.FEMALE },
							{ Allele.REC, Allele.REC }}});

		creatures[0] = new Creature(290, 100, g1, game.atlas);
		creatures[1] = new Creature(700, 100, g2, game.atlas);
		creatures[2] = creatures[0].breed(creatures[1], game.atlas);
		selectedCreature = null;// creatures[0];

		int i = 0;
		for(Creature c : creatures){
			if(c != null){
				needUpdates[i++] = c;
				System.out.println(c.g);
			}
		}

		// shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		viewport = new ExtendViewport(vieww, viewh, camera);
		
		control = new TInput(this);
		InputMultiplexer im = new InputMultiplexer((InputProcessor)control.stage);
		im.addProcessor(control);
		Gdx.input.setInputProcessor(im);
	}

	public void setCreature(Creature c){
		selectedCreature = c;
		addToUpdates(c);	
	}

	public void addToUpdates(Creature c){
		int temp = -1;
		for(int i = 0; i < needUpdates.length; ++i){
			if(needUpdates[i] == null){
				temp = i;
			}
			if(needUpdates[i] == c){
				return;
			}
		}
		if(temp != -1){
			needUpdates[temp] = c;
		}
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

		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();

		if(selectedCreature != null){
			game.batch.setColor(1, 0.5f, 0, 1);
			game.batch.draw(halo, selectedCreature.x + selectedCreature.width/2 - 16, selectedCreature.y + selectedCreature.height, 32, 32);
		}

		for (Creature c : creatures) {
			if (c != null) {
				//shapeRenderer.rect(c.x, c.y, c.width, c.height);
				c.draw(game.batch, alpha);
			}
		}

		currentlevel.draw(game.batch, camera.position, viewport.getWorldWidth(), viewport.getWorldHeight());

		
	/*
		shapeRenderer.begin(ShapeType.Filled);

		shapeRenderer.setProjectionMatrix(camera.combined);

	
		shapeRenderer.setColor(1, 1, 1, 1);
		if (selectedCreature != null)
			shapeRenderer.triangle(selectedCreature.x + selectedCreature.width
					/ 2.0f, selectedCreature.y + selectedCreature.height + 10,
					selectedCreature.x + selectedCreature.width / 2.0f - 20,
					selectedCreature.y + selectedCreature.height + 34,
					selectedCreature.x + selectedCreature.width / 2.0f + 20,
					selectedCreature.y + selectedCreature.height + 34);
		// shapeRenderer.ellipse(selectedCreature.x, selectedCreature.y,
		// selectedCreature.width, selectedCreature.height, 9);
		
		shapeRenderer.end();
	*/
		control.render();
	}

	@Override
	public void render(float delta) {
		double time = Math.min(delta, 0.25);

		accumulator += time;
		while(accumulator >= physicsStep){ // Physics loop
			currentlevel.update();
			control.update();
			//control.stage.act(); //maybe don't need
			for(int i = 0; i < needUpdates.length; ++i) {
				Creature c = needUpdates[i];
				if (c != null) {
					if(c != selectedCreature && c.onGround && Math.abs(c.vx) < .0001 && c.vy > -0.0001 && c.tick > 10){
						needUpdates[i] = null;
						c.vy = 0;
						c.tick = 0;
					}
					else{
						c.update(currentlevel, game.batch);
					}
				}
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
