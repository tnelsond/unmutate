package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class LevelSelectScreen implements Screen {
	
	final Unmutate game;
	
	public int vieww = 500;
	public int viewh = 300;
	public Table table;
	private Stage stage;
	
	public LevelSelectScreen(final Unmutate game) {
		this.game = game;
		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.requestRendering();
		stage = new Stage(new ExtendViewport(vieww, viewh), game.batch);
		Gdx.input.setInputProcessor(stage);


		table = new Table();
		table.debug();
		table.debugTable();
		table.setFillParent(true);
		Table con = new Table();
		Label.LabelStyle labelstyle = new Label.LabelStyle(game.font, new Color(1, 1, 1, 1));
		Label label = new Label("UNMUTATE 0.1", game.skin);
		con.add(label).fill().expand();
		con.row();
		TextButton chapter1 = new TextButton("1", game.bs);
		chapter1.setColor(1, 1, 0, 1);
		chapter1.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y){
				game.setScreen(new MainMenuScreen(game));
				dispose();
			}
		});
		con.add(chapter1);
		ScrollPane scroll = new ScrollPane(con);
		table.add(scroll).fill().expand();

		//scroll.setFillParent(true);
		table.layout();
		stage.addActor(table);

		Gdx.gl.glClearColor(0.1f, 0.3f, 0.0f, 1);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
		//table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
