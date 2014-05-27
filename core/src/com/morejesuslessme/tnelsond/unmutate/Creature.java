package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.morejesuslessme.tnelsond.unmutate.Level.blocktype;


public class Creature extends Rectangle {
	/**
	 * 
	 */
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
	public static Trect TSECONDARY = new Trect(0, 0, 24, 24);

	public Trect body, leg, eye, eyewhite, secondary;
	
	float px, py;
	float vx = 0;
	float vy = 0; // Velocity
	private AtlasRegion[] regions;
	
	public Color color;
	public Color eyeColor;
	public Color secondaryColor;
	public boolean albino = false;
	public boolean legConcave = false;
	
	public float accel = 0.3f;
	public float speed = 1;
	public float legThick = 0;
	public float legLength = TLEG.w;
	public float jump = 2;
	
	public Genome.Sex sex;
	public Genome g;
	
	// Movement flags
	public boolean onGround = false;
	public boolean movingUp = false;
	public boolean movingDown = false;
	public boolean movingRight = false;
	public boolean movingLeft = false;
	
	public float pwalkStep = 0;
	public float walkStep = 0;
	public int tick = 0;
		
	public Creature(float x, float y, Genome g, TextureAtlas atlas) {
		super(x, y, 63, 63);
		px = x;
		py = y;
		regions = new AtlasRegion[Creature.STATUS + 1];
		color = new Color(0, 0, 0, 1);
		secondaryColor = new Color(0, 0, 0, 1);
		eyeColor = new Color(0, 0, 0, 1);
		this.g = g;
		this.express();
		
		legThick *= width*7/200;
		legLength = legLength * width * 2.0f / TLEG.w;
		speed *= Math.sqrt(legLength);
		height = legLength + width * .8f - legThick;

		body = (Trect) Creature.TBODY.clone();
		leg = (Trect) Creature.TLEG.clone();
		eye = (Trect) Creature.TEYE.clone();
		eyewhite = (Trect) Creature.TEYEWHITE.clone();
		secondary = (Trect) Creature.TSECONDARY.clone();
		

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
		regions[Creature.LEG] = atlas.findRegion((legConcave) ? "bonefoot" : "roundrectfoot");
		regions[Creature.SECONDARY] = (sex == Genome.Sex.STERILE) ? null : atlas.findRegion((sex == Genome.Sex.MALE) ? "horn" : "bow");
	}
	
	public void express(){
		/*
		 * RED		legThick
		 * GREEN	width		legLength
		 */
		int i = 0;
		int j = 0;
		
		// ---- Chromosome 1
		color.r = ((g.chromosomes[i].a[j] == Allele.DOM) ? .5f : ((g.chromosomes[i].a[j] == Allele.REC) ? .25f : 0)) + ((g.chromosomes[i].b[j] == Allele.DOM) ? .5f : ((g.chromosomes[i].b[j] == Allele.REC) ? .25f : 0));
		j = 1;
		legThick = ((g.chromosomes[i].a[j] == Allele.DOM) ? 5 : ((g.chromosomes[i].a[j] == Allele.REC) ? 4 : 2)) + ((g.chromosomes[i].b[j] == Allele.DOM) ? 5 : ((g.chromosomes[i].b[j] == Allele.REC) ? 4 : 2));
		j = 2;
		legConcave = g.chromosomes[i].a[j] != Allele.DOM && g.chromosomes[i].b[j] != Allele.DOM;
		j = 3;
		speed *= ((g.chromosomes[i].a[j] == Allele.DOM) ? 3 : ((g.chromosomes[i].a[j] == Allele.REC) ? 1 : .1)) + ((g.chromosomes[i].b[j] == Allele.DOM) ? 3 : ((g.chromosomes[i].b[j] == Allele.REC) ? 1 : .1));
		j = 4;
		eyeColor.b = Math.max((g.chromosomes[i].a[j] == Allele.DOM) ? .9f : ((g.chromosomes[i].a[j] == Allele.REC) ? .4f : 0), ((g.chromosomes[i].b[j] == Allele.DOM) ? .9f : ((g.chromosomes[i].b[j] == Allele.REC) ? .4f : 0)));
		
		// ---- Chromosome 2
		++i; j = 0;
		color.g = ((g.chromosomes[i].a[j] == Allele.DOM) ? .4f : ((g.chromosomes[i].a[j] == Allele.REC) ? .1f : 0)) + ((g.chromosomes[i].b[j] == Allele.DOM) ? .4f : ((g.chromosomes[i].b[j] == Allele.REC) ? .1f : 0));
		j = 1;
		width *= ((g.chromosomes[i].a[j] == Allele.DOM) ? .5 : ((g.chromosomes[i].a[j] == Allele.REC) ? .2 : .1)) + 
				((g.chromosomes[i].b[j] == Allele.DOM) ? .5 : ((g.chromosomes[i].b[j] == Allele.REC) ? .2 : .1));
		j = 2;
		legLength *= ((g.chromosomes[i].a[j] == Allele.DOM) ? .5 : ((g.chromosomes[i].a[j] == Allele.REC) ? .2 : .1)) +
				((g.chromosomes[i].b[j] == Allele.DOM) ? .5 : ((g.chromosomes[i].b[j] == Allele.REC) ? .2 : .1));
		
		// ---- Chromosome 3
		++i; j = 0;
		eyeColor.g = ((g.chromosomes[i].a[j]== Allele.DOM) ? .4f : ((g.chromosomes[i].a[j] == Allele.REC) ? .25f : 0)) + ((g.chromosomes[i].b[j] == Allele.DOM) ? .4f : ((g.chromosomes[i].b[j] == Allele.REC) ? .25f : 0));
		j = 1;
		jump += ((g.chromosomes[i].a[j]== Allele.DOM) ? 6 : ((g.chromosomes[i].a[j] == Allele.REC) ? 2 : 0))
				+ ((g.chromosomes[i].b[j] == Allele.DOM) ? 6 : ((g.chromosomes[i].b[j] == Allele.REC) ? 2 : 0));
		j = 2;
		albino = (g.chromosomes[i].a[j] == Allele.MUT && g.chromosomes[i].b[j] == Allele.MUT);
		
			// Make colors subtractive
		Color tempcolor = new Color(.9f, .9f, .9f, 1);
		if(!albino) {
			color = tempcolor.cpy().sub(color.b + color.g, color.r + color.b, color.g + color.r, 0);
			eyeColor = tempcolor.cpy().sub(eyeColor.b + eyeColor.g, eyeColor.r + eyeColor.b, eyeColor.g + eyeColor.r, 0);
		}
		else {
			color = tempcolor.cpy();
			eyeColor = new Color(1, 0, 0, 1);
		}

		// ---- Chromosome 4 (SEX)
		++i; j = 0;
		Allele ca = g.chromosomes[i].a[j];
		Allele cb = g.chromosomes[i].b[j];
		if(ca == cb && ca == Allele.FEMALE)
			sex = Genome.Sex.FEMALE;
		else if((ca == Allele.MALE && cb == Allele.FEMALE) || (ca == Allele.FEMALE && cb == Allele.MALE))
			sex = Genome.Sex.MALE;
		else
			sex = Genome.Sex.STERILE;

		j = 1;
		Allele[] femalea = null, femaleb = null, male = null;
		if(sex == Genome.Sex.MALE){
			femalea = (g.chromosomes[i].a[j] == Allele.FEMALE) ? g.chromosomes[i].a : g.chromosomes[i].b;
			femaleb = femalea;
			male = (g.chromosomes[i].a[j] == Allele.MALE) ? g.chromosomes[i].a : g.chromosomes[i].b;
			// Male inheritance here
			secondaryColor.g = ((male[j] == Allele.DOM) ? .9f : (male[j] == Allele.REC) ? .4f : .1f);
		}
		else if(sex == Genome.Sex.FEMALE){
			femalea = g.chromosomes[i].a;
			femaleb = g.chromosomes[i].b;
			secondaryColor.r = ((femalea[j] == Allele.DOM) ? .45f : (femalea[j] == Allele.REC) ? .2f : .05f) + ((femaleb[j] == Allele.DOM) ? .45f : (femaleb[j] == Allele.REC) ? .2f : .05f);
		}

			tempcolor = new Color(1f, .9f, .9f, 1);
			secondaryColor = tempcolor.cpy().sub(secondaryColor.b + secondaryColor.g, secondaryColor.r + secondaryColor.b, secondaryColor.g + secondaryColor.r, 0);
				

	}

	public void moveToward(float cx, float cy) {
		cx = (cx > 1) ? 1 : (cx < -1) ? -1 : cx;
		cy = (cy < -1) ? -1 : cy;
		vx += cx * accel * (onGround ? 1 : .5);
		
		if(cy > 0 && onGround) // Jump
			vy = jump;
		else if(cy < 0)
			vy += cy;
		
		if(vx > speed)
			vx = speed;
		else if(vx < -speed)
			vx = -speed;
		tick = 0;
	}
	
	public void update(Level level, SpriteBatch batch) {
		px = x;
		py = y;
		if(!onGround)
			tick = 0;
		onGround = false;
		x += vx;
		checkX(vx, level, batch);
		vy -= level.GRAVITY;
		y += vy;
		checkY(vy, level, batch);
		
		/*
		if(y <= FLOOR) {
			y = FLOOR;
			vy = 0;
		}
		*/
		
		
		vx *= .95;
		vy *= .98; // Air Friction
		
		pwalkStep = walkStep;
		walkStep += (vx) / (legLength/2.0f);
		++tick;
	}
	
	public void checkX(float v, Level level, SpriteBatch batch) {
		if(v == 0){
			return;
		}
		float cor = 0;
		int c1 = (int) Math.floor((x) / level.tile);
		int c2 = (int) Math.floor((x + width + 1) / level.tile);
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
		
		int r2 = (int) Math.floor((y + height - 1) / level.tile);
		int r1 = (int) Math.floor(y / level.tile);
		//if(r1 == r2)
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
				//batch.draw(level.yellowRegion, c*level.tile, r*level.tile);
				blocktype b = level.blocks[r][c];
				if(b != blocktype.NONE){
					x = c*level.tile + cor;
					vx = 0;
					done = true;
					break;
				}
			}
		}
	}
	
	public void checkY(float v, Level level, SpriteBatch batch) {
		if(v == 0){
			return;
		}
		float cor = 0;
		int r2 = (int) Math.floor((y + height) / level.tile);
		int r1 = (int) Math.floor(y / level.tile);
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
		
		int c1 = (int) Math.floor((x) / level.tile) - 1;
		int c2 = (int) Math.floor((x + width ) / level.tile);
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
				//batch.draw(level.yellowRegion, c*level.tile, r*level.tile);
				Level.blocktype b = level.blocks[r][c];
				if(b != blocktype.NONE){
					if(v < 0)
						onGround = true;
					y = r*level.tile + cor;
					vy = 0;
					done = true;
					if(sex == Genome.Sex.MALE && b == blocktype.BREED1MALE)
						level.breeder1.male = this;
					else if(sex == Genome.Sex.FEMALE && b == blocktype.BREED1FEMALE)
						level.breeder1.female = this;
					break;
				}
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
		float yleg = width * .3f + tempy;
		
		batch.setColor(color);
		batch.draw(regions[Creature.LEG], dx + width/4 - legPivot, yleg - legThick, // Left
				legPivot, legPivot, // originX, originY
				legLength, legThick, // width, height
				1, 1, // scaleX, scaleY
				lefta/2 + 135); // rotation
		//batch.draw(regions[Creature.LEG], x + width/4 - legPivot, yleg - legThick); // Left debug
		batch.draw(regions[Creature.LEG], dx + width - legThick - width/4 + legPivot, yleg - legThick, // Right
				legPivot, legPivot, // originX, originY
				legLength, legThick, // width, height
				1, 1, // scaleX, scaleY
				righta/2 - 45); // rotation
		batch.draw(regions[Creature.BODY], dx, tempy, body.w, body.w);
		batch.setColor(eyeColor);
		float ex = ((int)vx) == 0 ? 0 : (vx > 0 ? width/9 : -width/9);
		batch.draw(regions[Creature.EYE], dx + eye.x + ex, tempy + eye.y, eye.w/2, eye.h/2, eye.w, eye.h, 1, 1, 0);
		batch.setColor(Color.WHITE);
		batch.draw(regions[Creature.EYEWHITE], dx + eyewhite.x + ex, tempy + eyewhite.y, eyewhite.w/2, eyewhite.h/2, eyewhite.w, eyewhite.h, 1, 1, 0);
		if(regions[Creature.SECONDARY] != null){
			batch.setColor(secondaryColor);
			batch.draw(regions[Creature.SECONDARY], secondary.x + dx, tempy + secondary.y, secondary.w/2, secondary.h/2, secondary.w, secondary.h, 1, 1, 0);
		}
	}
	
	public Creature breed(Creature other, TextureAtlas atlas) {
		return new Creature((x + other.x)/2, (y + other.y)/2, g.breed(other.g), atlas);
	}
}
