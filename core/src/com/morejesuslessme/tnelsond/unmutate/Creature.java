package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.badlogic.gdx.math.MathUtils;

import com.morejesuslessme.tnelsond.unmutate.genome.*;

public class Creature extends Rectangle{
	private static final long serialVersionUID = 7611724726502808151L;
	
	public int FLOOR = 100;
	
	public static int BODY = 0;
	public static int LEG = 1;
	public static int EYE = 2;
	public static int EYEWHITE = 3;
	public static int SECONDARY = 4;
	public static int STATUS = 5;
	
	public static Trect TBODY = new Trect(0, 0, 64, 64);
	public static Trect TLEG = new Trect(0, 0, 16, 128);
	public static Trect TEYEWHITE = new Trect(0, 0, 44, 44);
	public static Trect TEYE = new Trect(0, 0, 36, 36);
	public static Trect TSECONDARY = new Trect(0, 0, 14, 24);

	public Trect body, leg, eye, eyewhite, secondary;
	
	float ax = 0, ay = 0;
	float px, py;
	float vx = 0, vy = 0; // Velocity
	private AtlasRegion[] regions;
	
	public Color color;
	public Color eyeColor;
	public Color secondaryColor;
	public boolean albino = false;
	public boolean legConcave = false;
	public boolean legPoint = true;
	
	public float accel = 1f;
	public float speed = 1;
	public float legThick = 1;
	public float legLength = 1;

	public float jump = 1;
	
	public Genome.Sex sex;
	public Genome g;
	
	// Movement flags
	public boolean onGround = false;
	public boolean breedable = false;

	public boolean awake = true;
	public boolean ascend = false;
	public float pwalkStep = 0;
	public float walkStep = 0;
	public int tick = 0;
	public float stretchfactor = 0;

	public int grassc = -1;
	public int grassr = -1;

	public static Color pigmentize(Color base, Color offset){
		return base.cpy().sub(offset.b + offset.g, offset.b + offset.r, offset.r + offset.g, 0).clamp();
	}

	public float calculateX(float alpha){
		return px + vx*alpha + ax*alpha*alpha/2f;
	}
	public float calculateY(float alpha){
		return py + vy*alpha + ay*alpha*alpha/2f;
	}
		
	public Creature(float x, float y, Genome g, TextureAtlas atlas) {
		super(x, y, 1, 1);
		px = x;
		py = y;
		regions = new AtlasRegion[Creature.STATUS + 1];
		color = new Color(0, 0, 0, 1);
		secondaryColor = new Color(0, 0, 0, 1);
		eyeColor = new Color(0, 0, 0, 1);
		this.g = g;
		this.g.express(this);

		// Make colors subtractive
		Color tempcolor = new Color(.95f, .9f, .9f, 1);
		if(!albino) {
			color = Creature.pigmentize(tempcolor, color);
			eyeColor = Creature.pigmentize(tempcolor, eyeColor);
		}
		else {
			color = tempcolor.cpy();
			eyeColor = new Color(1, 0, 0, 1);
		}
		secondaryColor = pigmentize(tempcolor, secondaryColor);
		
		accel *= .08;
		jump *= 4;
		width *= 64;
		legThick *= width*7/20;
		legLength *= Creature.TLEG.w * width * 2.0f / TLEG.w;
		speed *= Math.sqrt(legLength)/2;
		height = legLength + width * .8f - legThick;

		body = (Trect) Creature.TBODY.clone();
		leg = (Trect) Creature.TLEG.clone();
		eye = (Trect) Creature.TEYE.clone();
		eyewhite = (Trect) Creature.TEYEWHITE.clone();
		secondary = (Trect) Creature.TSECONDARY.clone();
		if(sex == Genome.Sex.FEMALE){
			secondary.scale(2.5f, 1);
		}
		

		float factor = width / Creature.TBODY.w;
		body.scale(factor, factor);
		eye.scale(factor, factor);
		eyewhite.scale(factor, factor);
		secondary.scale(factor, factor);
		leg.growTo(legLength, legThick);

		float cx = body.getCenterX();
		float cy = body.getCenterY();

		eye.setCenter(cx, cy);
		eyewhite.setCenter(cx, cy);
		float tempy = secondary.y;
		secondary.setCenter(cx, cy);
		secondary.y += (sex == Genome.Sex.FEMALE) ? body.h/2 : body.h/2 + body.h/8;

		regions[Creature.BODY] = atlas.findRegion("circlebody");
		regions[Creature.EYE] = atlas.findRegion("eye");
		regions[Creature.EYEWHITE] = atlas.findRegion("eyewhite");
		regions[Creature.LEG] = atlas.findRegion(legPoint ? "pointfoot" : ((legConcave) ? "bonefoot" : "roundrectfoot"));
		regions[Creature.SECONDARY] = (sex == Genome.Sex.STERILE) ? null : atlas.findRegion((sex == Genome.Sex.MALE) ? "horn" : "bow");
	}

	public void moveToward(float cx, float cy) {
		cx = MathUtils.clamp(cx, -1, 1);
		cy = MathUtils.clamp(cy, -1, 1);

		if((cx > 0 && vx < 0) || (cx < 0 && vx > 0)){
			ax += -vx *.06f;
		}
		ax += cx * accel * (onGround ? 1 : .5);
		
		if(cy > 0){
			if(onGround) // Jump
				ay = jump;
		}
		else {
			ay += cy;
			if(vy > 0){
				ay = -vy/2;
			}
		}
		
		tick = 0;
	}
	
	public void update(){
		grassc = -1;
		grassr = -1;
		breedable = false;
		px = x;
		py = y;
		if(vx + ax > speed)
			ax = speed - vx;
		else if(vx + ax < -speed)
			ax = -speed - vx;
		if(ax == 0){
			ax = -vx *.03f;
		}
		vx += ax;
		vy += ay;

		if(!onGround)
			tick = 0;

		onGround = false;
		x = calculateX(1);
		checkX(vx, Level.currentlevel);
		y = calculateY(1);
		checkY(vy, Level.currentlevel);
		
		pwalkStep = walkStep;
		walkStep += (vx) / (legLength/2.0f);
		++tick;
	}

	public void updateVelocity(){
		vx += ax;
		vy += ay;
		ax = 0;
		ay = -Level.currentlevel.GRAVITY;
	}

	public void checkX(float v, Level level) {
		if(v == 0){
			return;
		}
		float cor = 0;
		int c1 = (int) ((x) / level.tile);
		int c2 = (int) ((x + width + 1) / level.tile);
		if(c1 < 0)
			c1 = 0;
		if(c2 < 0)
			return;
		if(c1 >= level.w)
			return;
		if(c2 >= level.w)
			c2 = level.w - 1;
		
		if(v < 0) {
			int temp = c1;
			c1 = c2;
			c2 = temp;
			cor = level.tile;
		}
		else {
			cor = -width - 1;
		}
		
		int r2 = (int) ((y + height - 1) / level.tile);
		int r1 = (int) (y / level.tile);
		--r1;
		if(r1 < 0)
			r1 = 0;
		if(r1 >= level.h)
			return;
		if(r2 >= level.h)
			r2 = level.h - 1;
		if(r2 < 0)
			return;
		
		boolean done = false;
		Titer col = new Titer(c1, c2);
		int c;
		while(col.hasNext()) {
			c = (Integer) col.next();
			if(done)
				break;
			int r;
			Titer row = new Titer(r1, r2);
			while(row.hasNext()) {
				r = (Integer) row.next();
				int b = level.blocks[r][c];
				if(b == Level.END){
					ascend = true;
				}
				if(b != Level.NONE && level.isSolid(b, this)){
					x = c*level.tile + cor;
					vx = 0;
					done = true;
					break;
				}
			}
		}
	}
	
	public void checkY(float v, Level level) {
		if(v == 0){
			return;
		}
		float cor = 0;
		int r2 = (int) ((y + height) / level.tile);
		int r1 = (int) (y / level.tile);
		if(r1 < 0)
			r1 = 0;
		if(r1 >= level.h)
			return;
		if(r2 >= level.h)
			r2 = level.h - 1;
		if(r2 < 0)
			return;
		
		if(v < 0) {
			int temp = r1;
			r1 = r2;
			r2 = temp;
			cor = level.tile;
		}
		else {
			cor = -height - 1;
		}
		
		int c1 = (int) ((x) / level.tile) - 1;
		int c2 = (int) ((x + width ) / level.tile);
		if(c2 == c1)
			--c1;
		if(c1 < 0)
			c1 = 0;
		if(c2 < 0)
			return;
		if(c1 >= level.w)
			return;
		if(c2 >= level.w)
			c2 = level.w - 1;
		
		boolean done = false;
		Titer row = new Titer(r1, r2);
		int r;
		while(row.hasNext()) {
			r = (Integer) row.next();
			if(done)
				break;
			int c;
			Titer col = new Titer(c1, c2);
			while(col.hasNext()) {
				c = (Integer) col.next();
				int b = level.blocks[r][c];
				if(b == Level.END){
					ascend = true;
				}
				if(b != Level.NONE && level.isSolid(b, this)){
					if(v < 0)
						onGround = true;
					y = r*level.tile + cor;
					vy = 0;
					done = true;
					if(b == Level.GRASS){
						grassc = c;
						grassr = r;
						breedable = true;
					}
					break;
				}
			}
		}
	}

	public void checkForGrass(Level level){
		int r = (int) Math.floor((y - 1) / level.tile);
		int c1 = (int) (x / level.tile) - 1;
		int c2 = (int) ((x + width) / level.tile);
		if(c2 == c1)
			--c1;
		int c;
		Titer col = new Titer(c1, c2);
		while(col.hasNext()){
			c = (Integer) col.next();
			int b = level.blocks[r][c];
			if(b == Level.GRASS){
				grassc = c;
				grassr = r;
				breedable = true;
				break;
			}
		}
	}
	
	public void draw(SpriteBatch batch, final float alpha) {
		float dx = px + (x - px) * alpha;
		float dy = py + (y - py) * alpha;
		float dwalkStep = pwalkStep + (walkStep - pwalkStep) * alpha;

		float legPivot = legThick/2 + 1;
		float angle = Math.abs(dwalkStep*70) % (360);
		float righta = angle > 180 ? 180 - (angle) : angle - 180;
		//float tempy = (float) (y + Math.sin(righta/180*Math.PI + Math.PI)*legLength/4.25 + legLength/4.25);//width/2;
		//float tempy = (float) (y + Math.sin(righta/180*Math.PI + Math.PI)*legLength/4.25) + legLength - width/3 - legThick;//height - width;
		float tempy = (float) (dy + (Math.sin(righta/180*Math.PI + Math.PI)*(legLength - legPivot)/3.6f) + legLength - legLength/3.2f - legThick) - 3;
		float lefta = 180 - righta;
		stretchfactor += .001;
		if(stretchfactor > width/300)
			stretchfactor = 0;
		float stretch = width * (stretchfactor < width/600 ? stretchfactor : width/600 - (stretchfactor % (width/600)));
		float twidth = width - stretch;
		dx += stretch / 2;
		float yleg = width * .3f + tempy;
		
		batch.setColor(color);
		batch.draw(regions[Creature.LEG], dx + twidth/4 - legPivot, yleg - legThick, // Left
				legPivot, legPivot, // originX, originY
				legLength, legThick, // width, height
				1, 1, // scaleX, scaleY
				lefta/2 + 135); // rotation
		//batch.draw(regions[Creature.LEG], x + width/4 - legPivot, yleg - legThick); // Left debug
		batch.draw(regions[Creature.LEG], dx + twidth - legThick - twidth/4 + legPivot, yleg - legThick, // Right
				legPivot, legPivot, // originX, originY
				legLength, legThick, // width, height
				1, 1, // scaleX, scaleY
				righta/2 - 45); // rotation
		batch.draw(regions[Creature.BODY], dx, tempy, twidth, body.w + stretch);
		batch.setColor(eyeColor);
		float ex = ((int)vx) == 0 ? 0 : (vx > 0 ? twidth/9 : -twidth/9);
		batch.draw(regions[Creature.EYE], dx + twidth/2 - eye.w/2 + ex, tempy + eye.y, eye.w/2, eye.h/2, eye.w, eye.h, 1, 1, 0);
		batch.setColor(Color.WHITE);
		batch.draw(regions[Creature.EYEWHITE], dx + twidth/2 - eyewhite.w/2 + ex, tempy + eyewhite.y, eyewhite.w/2, eyewhite.h/2, eyewhite.w, eyewhite.h, 1, 1, 0);
		if(regions[Creature.SECONDARY] != null){
			batch.setColor(secondaryColor);
			batch.draw(regions[Creature.SECONDARY], dx + twidth/2 - secondary.w/2, tempy + secondary.y + width - twidth, secondary.w/2, secondary.h/2, secondary.w, secondary.h, 1, 1, 0);
		}
	}
	
	public Creature breed(Creature other, GameScreen game) {
		game.currentlevel.consume(grassr, grassc);
		return new Creature((x + other.x)/2, (y + other.y)/2, g.breed(other.g), game.game.atlas);
	}
}
