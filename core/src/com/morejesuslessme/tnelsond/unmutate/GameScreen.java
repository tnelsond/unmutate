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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
	private OrthographicCamera camera;
	public Viewport viewport;
	
	final Unmutate game;

	public ShapeRenderer shapeRenderer;

	public Level currentlevel;
	Color backgroundColor;
	
	public int vieww = 800;
	public int viewh = 480;

	private TInput control;
	
	Creature creatures[];
	Creature selectedCreature;

	// private ShaderProgram shader;
	// String vertexShader;
	// String fragmentShader;

	public GameScreen(final Unmutate game) {
		this.game = game;
	
		backgroundColor = new Color(0.2f, 0.1f, .0f, 1);
		currentlevel = new Level(game.atlas, "levels/1.txt");
		shapeRenderer = new ShapeRenderer();
		creatures = new Creature[8];
		// vertexShader = Gdx.files.internal("vertexShader.txt").readString();
		// fragmentShader =
		// Gdx.files.internal("fragmentShader.txt").readString();
		// shader = new ShaderProgram(vertexShader, fragmentShader);
		// System.out.println(shader.isCompiled());

		Genome g1 = new Genome(
				new Allele[][][] {
						{ { Allele.DOM, Allele.DOM },
								{ Allele.MUT, Allele.REC },
								{ Allele.DOM, Allele.REC },
								{ Allele.DOM, Allele.REC }, },
						{ { Allele.DOM, Allele.REC },
								{ Allele.DOM, Allele.DOM },
								{ Allele.MUT, Allele.DOM }, },
						{ { Allele.MUT, Allele.DOM },
								{ Allele.DOM, Allele.REC },
								{ Allele.MUT, Allele.MUT }, } });

		Genome g2 = new Genome(
				new Allele[][][] {
						{ { Allele.REC, Allele.REC },
								{ Allele.REC, Allele.MUT },
								{ Allele.DOM, Allele.REC },
								{ Allele.DOM, Allele.MUT }, },
						{ { Allele.REC, Allele.DOM },
								{ Allele.REC, Allele.MUT },
								{ Allele.REC, Allele.DOM }, },
						{ { Allele.REC, Allele.REC },
								{ Allele.DOM, Allele.REC },
								{ Allele.MUT, Allele.DOM }, } });

		creatures[0] = new Creature(290, 100, g1, game.atlas);
		creatures[1] = new Creature(250, 100, g2, game.atlas);
		creatures[2] = creatures[0].breed(creatures[1], game.atlas);
		selectedCreature = null;// creatures[0];

		// shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		viewport = new ExtendViewport(800, 480, camera);
		
		control = new TInput(this);
		InputMultiplexer im = new InputMultiplexer((InputProcessor)control.stage);
		im.addProcessor(control);
		Gdx.input.setInputProcessor(im);
	}


	public void draw() {
		if (selectedCreature != null) {
			Vector3 pos = new Vector3(selectedCreature.x, selectedCreature.y, 0);
			camera.position.set(pos);
		}
		camera.update();
		//System.out.println(w + ", " + h + ", " + camera.viewportWidth + ", " + camera.viewportHeight);

		// Matrix3 matrix = new Matrix3().setToRotation(30);
		// shader.begin();
		// shader.setUniformMatrix("u_worldView", matrix);
		// shader.setUniformi("u_texture", 0);

		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		// game.batch.setShader(shader); // Shader stuff

		for (Creature c : creatures) {
			if (c != null) {
				//shapeRenderer.rect(c.x, c.y, c.width, c.height);
				c.draw(game.batch);
			}
		}

		currentlevel.draw(game.batch, camera.position, viewport.getWorldWidth(), viewport.getWorldHeight());
		game.batch.end();
		
	/*
		shapeRenderer.begin(ShapeType.Filled);

		shapeRenderer.setProjectionMatrix(camera.combined);

		//control.render();
	
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
	}

	@Override
	public void render(float delta) {
		control.update();
		if (delta < 1000.0 / 45)
			draw();
		for (Creature c : creatures) {
			if (c != null) {
				c.update(currentlevel, game.batch);
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		control.stage.getViewport().update(width, height);
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
