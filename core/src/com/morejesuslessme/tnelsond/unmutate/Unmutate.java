package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Unmutate extends Game {
	
	public SpriteBatch batch;
	public BitmapFont font;
	public TextureAtlas atlas;

	@Override
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();
		atlas = new TextureAtlas(Gdx.files.internal("gamegdx.atlas"));
		//this.setScreen(new MainMenuScreen(this));
		this.setScreen(new GameScreen(this));
	}
	
	public void render() {
		super.render();
	}
	
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}
