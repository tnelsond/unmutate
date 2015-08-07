package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class PartSelectScreen implements Screen {
	
	final Unmutate game;
	
	public int vieww = 500;
	public int viewh = 400;
	public Table table;
	private Stage stage;
	
	public PartSelectScreen(final Unmutate game, final int chapter) {
		this.game = game;
		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.requestRendering();
		stage = new Stage(new ExtendViewport(vieww, viewh), game.batch);
		Gdx.input.setInputProcessor(stage);


		table = new Table();
		table.debug();
		table.debugTable();
		table.setFillParent(true);
		boolean completed = false;
		boolean prevcompleted = true;
		for(int part = 0; part <= Level.levels[chapter]; ++part){
			completed = Level.latestChapter < chapter || Level.latestPart > part;
			final TextButton partb = new TextButton("" + part, prevcompleted ? game.bs : game.bs_locked);
			if(!prevcompleted){
				partb.setDisabled(true);
				partb.setColor(.2f, .2f, .2f, 1);
			}
			else if(completed){
				partb.setColor(0, 1, 0, 1);
			}
			else{
				partb.setColor(1, 0, 0, 1);
			}
			partb.addListener(new ChangeListener()
			{
				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor a){
					int part = Integer.parseInt(partb.getText().toString());
					game.setScreen(new GameScreen(game, chapter, part));
					dispose();
				}
			});
			table.add(partb).uniform();
			if(part % 9 == 0 && part > 0)
				table.row();
			prevcompleted = completed;
		}
		table.row();
		TextButton backb = new TextButton("BACK", game.bs);
		backb.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor a){
				game.setScreen(new ChapterSelectScreen(game));
				dispose();
			}
		});
		table.add(backb);
		table.layout();
		stage.addActor(table);

		Gdx.gl.glClearColor(0.0f, 0.1f, 0.4f, 1);
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
