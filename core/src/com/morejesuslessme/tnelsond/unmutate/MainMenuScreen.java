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

public class MainMenuScreen implements Screen {
	
	final Unmutate game;
	
	public int vieww = 500;
	public int viewh = 300;
	public Table table;
	private Stage stage;
	
	public MainMenuScreen(final Unmutate game) {
		this.game = game;
		//Gdx.graphics.setContinuousRendering(true);
		//Gdx.graphics.requestRendering();
		stage = new Stage(new ExtendViewport(vieww, viewh), game.batch);
		Gdx.input.setInputProcessor(stage);


		table = new Table();
		table.debug();
		table.debugTable();
		//table.align(Align.left | Align.top);
		table.setFillParent(true);
		Label.LabelStyle labelstyle = new Label.LabelStyle(game.font, new Color(1, 1, 1, 1));
		String levelintro = Gdx.files.internal(Level.getLevelName(".txt", false)).readString();
		Label label = new Label(levelintro, labelstyle);
		label.setWrap(true);
		Table con = new Table();
		con.add(label).fill().expand();
		con.row();
		TextButton start = new TextButton("START", game.bs);
		start.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y){
				game.setScreen(new GameScreen(game));
				dispose();
			}
		});
		con.add(start);
		ScrollPane scroll = new ScrollPane(con);
		table.add(scroll).fill().expand();

		//scroll.setFillParent(true);
		table.layout();
		stage.addActor(table);

		Gdx.gl.glClearColor(0.0f, 0.0f, 0.2f, 1);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.2f, 1);
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
