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

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;


public class Unmutate extends Game {
	
	public SpriteBatch batch;
	public BitmapFont font;
	public TextureAtlas atlas;
	public TextButtonStyle bs;
	public Json json = new Json();

	@Override
	public void create() {
		Level.json = json;
		batch = new SpriteBatch(200);
		atlas = new TextureAtlas(Gdx.files.internal("gamegdx.atlas"));
		font = new BitmapFont(Gdx.files.internal("sans.fnt"), atlas.findRegion("sans"));
		Drawable button_normal = (Drawable) new NinePatchDrawable(atlas.createPatch("button_normal"));
		Drawable button_pressed = (Drawable) new NinePatchDrawable(atlas.createPatch("button_pressed"));
		bs = new TextButtonStyle(button_normal, button_pressed, button_normal, font);

		this.setScreen(new MainMenuScreen(this));
		//this.setScreen(new GameScreen(this));
	}
	
	public void render() {
		super.render();
	}
	
	public void dispose() {
		super.dispose();
		batch.dispose();
		font.dispose();
	}
}
