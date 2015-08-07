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

public class ChapterSelectScreen implements Screen {
	
	final Unmutate game;
	
	public int vieww = 500;
	public int viewh = 400;
	public Table table;
	private Stage stage;
	
	public ChapterSelectScreen(final Unmutate game) {
		this.game = game;
		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.requestRendering();
		stage = new Stage(new ExtendViewport(vieww, viewh), game.batch);
		Gdx.input.setInputProcessor(stage);
		Level.initPrefLatest();

		table = new Table();
		table.debug();
		table.debugTable();
		table.setFillParent(true);
		Label.LabelStyle labelstyle = new Label.LabelStyle(game.font, new Color(1, 1, 1, 1));
		Label label = new Label("UNMUTATE 0.2", game.skin);
		table.add(label).colspan(6);//.fill().expand();
		table.row();
		for(int chapter = 0; chapter < Level.levels.length; ++chapter){
			final TextButton chapterb = new TextButton("" + chapter, chapter > Level.newchapter ? game.bs_locked : game.bs);
			if(chapter == Level.latestChapter){
				chapterb.setColor(1, 0, 0, 1);
			}
			else if(chapter > Level.latestChapter){
				chapterb.setDisabled(true);
				chapterb.setColor(.2f, .2f, .2f, 1);
			}
			else{
				chapterb.setColor(0, 1, 0, 1);
			}
			chapterb.addListener(new ChangeListener()
			{
				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor a){
					int chapter = Integer.parseInt(chapterb.getText().toString());
					game.setScreen(new PartSelectScreen(game, chapter));
					dispose();
				}
			});
			table.add(chapterb).uniform();
			if(chapter % 6 == 0 && chapter != 0)
				table.row();
		}
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
