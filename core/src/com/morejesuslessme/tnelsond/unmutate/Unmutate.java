package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;


public class Unmutate extends Game {
	
	public SpriteBatch batch;
	public BitmapFont font;
	public TextureAtlas atlas;
	public TextButtonStyle bs;
	public TextButtonStyle bs_locked;
	public LabelStyle hintst;

	public Json json = new Json();
	public Skin skin;
	public Thread musicthread;
	public TMusic bgmusic;

	public boolean paused = false;

	public static String tag = "***UNMUTATE";

	@Override
	public void create() {
		Gdx.app.log(Unmutate.tag, "STARTING APP");
		Level.json = json;
		batch = new SpriteBatch(200);
		atlas = new TextureAtlas(Gdx.files.internal("gamegdx.atlas"));
		skin = new Skin(Gdx.files.internal("skin.json"), atlas);
		font = skin.getFont("regular");
		hintst = skin.get("tutorial", LabelStyle.class);
		bs = skin.get("default", TextButtonStyle.class);
		bs_locked = skin.get("locked", TextButtonStyle.class);
		bgmusic = new TMusic();
		musicthread = new Thread(bgmusic);
		
		//DISABLED FOR NOW
		//musicthread.start();

		this.setScreen(new ChapterSelectScreen(this));
		//this.setScreen(new GameScreen(this));
	}

	public void render() {
		super.render();
	}
	
	public void dispose() {
		super.dispose();
		batch.dispose();
		font.dispose();
		if(bgmusic != null){
			try{
				musicthread.interrupt();
				Gdx.app.log(Unmutate.tag, "STOPPING MUSIC THREAD...");
				bgmusic.terminate();
				musicthread.join();
			} catch(InterruptedException e){
			}
			Gdx.app.log(Unmutate.tag, "MUSIC THREAD STOPPED");
		}
	}
}
