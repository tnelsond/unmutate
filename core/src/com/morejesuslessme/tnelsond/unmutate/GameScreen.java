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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.Preferences;

import java.lang.Math;

import com.badlogic.gdx.graphics.FPSLogger;

import com.morejesuslessme.tnelsond.unmutate.genome.Genome;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class GameScreen implements Screen {
	public TCamera camera;
	public Viewport viewport;
	public ShapeRenderer shapeRenderer;

	TParticleEffect deathFX;
	TParticleEffect ascendFX;
	TParticleEffect birthFX;

	Sound soundJump;
	Sound soundDeath;
	Sound soundBirth;

	final Unmutate game;

	public Level currentlevel;

	public int vieww = 500;
	public int viewh = 300;

	private double accumulator = 0.0;
	private float physicsStep = 1.0f / 60.0f;

	private TInput control;

	Creature creatures[];
	Creature selectedCreature = null;
	AtlasRegion halo;

	// Debug
	FPSLogger fps = new FPSLogger();

	public final void breed(){
		int index = -1;
		Creature temp = null;
		Creature otherparent = null;
		if(selectedCreature == null)
			return;
		if(!selectedCreature.breedable)
			selectedCreature.checkForGrass(currentlevel);
		if(selectedCreature.breedable && selectedCreature.sex != Genome.Sex.STERILE){
			for(int i = 0; i < creatures.length; ++i){
				if(creatures[i] == null){
					index = i;
				}
				else if(creatures[i] != selectedCreature
						&& selectedCreature.sex != creatures[i].sex
						&& creatures[i].sex != Genome.Sex.STERILE
						&& (int)(creatures[i].y/10) == (int)(selectedCreature.y/10)
						&& creatures[i].overlaps(selectedCreature)){
					if(!creatures[i].breedable){
						creatures[i].checkForGrass(currentlevel);
					}
					if(creatures[i].breedable){
						otherparent = creatures[i];
						temp = creatures[i].breed(selectedCreature, this);
					}
				}

				if(temp != null && index != -1)
					break;
			}
			if(index != -1 && temp != null){
				creatures[index] = temp;
				selectedCreature.ax += 4;
				otherparent.ax += -4;
				temp.ay += 4;
				otherparent.awake = true;
				birthFX.addEffect(temp.x + temp.width/2, temp.y);
				soundBirth.play(.9f);
				control.updatestatus(1, 1);
			}
		}

	}

	public final void kill(Creature c){
		if(c != null){
			for(int i = 0; i < creatures.length; ++i){
				if(creatures[i] == c){
					creatures[i] = null;
					if(selectedCreature == c)
						selectedCreature = null;

					// Particle effect
					control.updatestatus(-1, 0);
					if(c.ascend){
						ascendFX.addEffect(c.x + c.width/2, c.y + c.height/2);
					}
					else{
						deathFX.addEffect(c.x + c.width/2, c.y + c.height/2);
						soundDeath.play(.9f);
					}
	
					break;
				}
			}
		}
	}

	public GameScreen(final Unmutate game, int chapter, int part) {
		this.game = game;

		Gdx.graphics.setContinuousRendering(true);

		halo = game.atlas.findRegion("eyewhite");

		currentlevel = Level.makeLevel(game, chapter, part);

		creatures = new Creature[10];


		shapeRenderer = new ShapeRenderer();
		camera = new TCamera();
		camera.setToOrtho(false);
		viewport = new ExtendViewport(vieww, viewh, (OrthographicCamera) camera);

		control = new TInput(this);
		InputMultiplexer im = new InputMultiplexer((InputProcessor)control.stage);
		im.addProcessor(control);
		Gdx.input.setInputProcessor(im);

		loadCreatures();
		selectedCreature = creatures[0];

		deathFX = new TParticleEffect("fx/death.p", game.atlas);
		ascendFX = new TParticleEffect("fx/ascend.p", game.atlas);
		birthFX = new TParticleEffect("fx/birth.p", game.atlas);

		soundJump = Gdx.audio.newSound(Gdx.files.internal("jump.ogg"));
		soundDeath = Gdx.audio.newSound(Gdx.files.internal("die.ogg"));
		soundBirth = Gdx.audio.newSound(Gdx.files.internal("born.ogg"));
	}

	public void loadCreatures(){
		for(int i = 0; i < currentlevel.chromosomes.length; ++i){
			creatures[i] = new Creature(currentlevel.spawns[i].c * currentlevel.tile, currentlevel.spawns[i].r * currentlevel.tile, currentlevel.getGenome(i, null), game.atlas);
			control.updatestatus(1, 0);
		}
	}

	public void setCreature(Creature c){
		selectedCreature = c;
		c.awake = true;
	}

	public void draw(final float alpha) {
		camera.setTarget(selectedCreature);
		camera.update(alpha);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(currentlevel.skyColor.r, currentlevel.skyColor.g, currentlevel.skyColor.b, 1);
		shapeRenderer.rect(0, 0, currentlevel.w * currentlevel.tile, currentlevel.h * currentlevel.tile);
		shapeRenderer.end();

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
			float dx = selectedCreature.px + (selectedCreature.x - selectedCreature.px) * alpha;
			float dy = selectedCreature.py + (selectedCreature.y - selectedCreature.py) * alpha;
			game.batch.setColor(0.2f, 1f, 0, 1);
			game.batch.draw(halo, dx + selectedCreature.width/2 - 16, dy + selectedCreature.height, 32, 32);
		}

		// Particle effects
		deathFX.render(game.batch);
		ascendFX.render(game.batch);
		birthFX.render(game.batch);
		
		control.render();
	}

	@Override
	public void render(float delta) {
		double time = Math.min(delta, 0.2);
		float alpha = 0;
		if(!game.paused){
			accumulator += time;
			while(accumulator >= physicsStep){ // Physics loop
				for(Creature c : creatures){
					if(c != null && c.awake){
						c.updateVelocity();
					}
				}
				control.update();
				//control.stage.act(); //maybe don't need
				boolean any = false;
				for(int i = creatures.length - 1; i >= 0; --i){
					Creature c = creatures[i];
					if(c != null && c.dead){
						kill(c);
					}
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
						if(c.ascend){
							currentlevel.ascend(c);	
							kill(c);
						}
					}
				}
				if(!any){
					currentlevel.nextLevel(this);
				}
				currentlevel.update();
				accumulator -= physicsStep;
			}
			alpha = (float)(accumulator / physicsStep);
		}
		draw(alpha);
		Gdx.graphics.requestRendering();
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
		deathFX.dispose();
		ascendFX.dispose();
		soundJump.dispose();
		soundDeath.dispose();
		soundBirth.dispose();
	}
}
