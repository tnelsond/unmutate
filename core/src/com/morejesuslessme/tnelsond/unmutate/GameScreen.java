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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class GameScreen implements Screen {
	private OrthographicCamera camera;
	public Viewport viewport;
	
	final Unmutate game;

	public ShapeRenderer shapeRenderer;

	public Level currentlevel;
	Color backgroundColor;
	
	public int vieww = 800;
	public int viewh = 480;

	//private TInput control;
	
	Creature creatures[];
	Creature selectedCreature;
	private TextureAtlas atlas;
	private AtlasRegion eye, eyewhite;

	// private ShaderProgram shader;
	// String vertexShader;
	// String fragmentShader;

	public GameScreen(final Unmutate game) {
		this.game = game;
	
		backgroundColor = new Color(0.05f, 0f, .1f, 1);
		currentlevel = new Level("levels/1.txt");
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

		creatures[0] = new Creature(290, 100, g1);
		creatures[1] = new Creature(250, 100, g2);
		creatures[2] = creatures[0].breed(creatures[1]);
		selectedCreature = null;// creatures[0];

		// shapeRenderer = new ShapeRenderer();
		setupTextures();

		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		viewport = new ExtendViewport(800, 480, camera);
		
		//control = new TInput(this);
		//InputMultiplexer im = new InputMultiplexer((InputProcessor)control.stage);
		//im.addProcessor(control);
		//Gdx.input.setInputProcessor(im);
		atlas = new TextureAtlas(Gdx.files.internal("gamegdx.atlas"));
		eye = atlas.findRegion("eye");
		eyewhite = atlas.findRegion("eyewhite");
		System.out.println(eye);
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

		/*
		for (Creature c : creatures) {
			if (c != null) {
				// shapeRenderer.rect(c.x, c.y, c.width, c.height);
				c.draw(game.batch);
			}
		}
		*/

		//currentlevel.draw(game.batch, camera.position, viewport.getWorldWidth(), viewport.getWorldHeight());
		game.batch.setColor(0, .3f, 1, 1);
		game.batch.draw(eye, 32.5f, 32.5f, 55, 55);
		game.batch.setColor(1, 1, 1, 1);
		game.batch.draw(eyewhite, 20, 20, 80, 80);
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
		//control.update();
		if (delta < 1000.0 / 45)
			draw();
		for (Creature c : creatures) {
			if (c != null) {
				c.update(currentlevel, game.batch);
			}
		}
	}

	public void setupTextures() {
		/*
		batchtexture = new Texture(256, 512, Pixmap.Format.RGBA4444);
		currentlevel.setupTexture(batchtexture, 0, 0);
		int i = 0;
		for (Creature c : creatures) {
			if (c != null) {
				c.setupTexture(batchtexture, 0, 35 + 65 * i);
				++i; // The texture will be offset by the index of the creature
			}
		}
		*/
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		//control.stage.getViewport().update(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
		//batchtexture.dispose();
		//setupTextures();
	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		//batchtexture.dispose();
	}
}
