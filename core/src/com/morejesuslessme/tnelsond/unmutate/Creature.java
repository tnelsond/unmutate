package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
	public static int STATUS = 3;
	
	public static Trect TBODY = new Trect(0, 0, 64, 64);
	public static Trect TLEG = new Trect(TBODY.w + 1, 0, TBODY.h * 2, TBODY.w * 7 / 20);
	public static Trect TEYE = new Trect(TBODY.w + 1, TLEG.h + 1, TBODY.w * 13 / 20, TBODY.w * 13 / 20);
	
	float vx = 0;
	float vy = 0; // Velocity
	private TextureRegion[] regions;
	
	public Color color;
	public Color eyeColor;
	public boolean albino;
	
	public float accel = 0.3f;
	public float speed = 1;
	public int legThick = 0;
	public int legLength = TLEG.w;
	public float jump = 2;
	
	public Genome.Sex sex;
	public Genome g;
	
	// Movement flags
	public boolean onGround = false;
	public boolean movingUp = false;
	public boolean movingDown = false;
	public boolean movingRight = false;
	public boolean movingLeft = false;
	
	public float walkStep = 0;
	public int tick = 0;
		
	public Creature(float x, float y, Genome g) {
		super(x, y, 63, 63);
		regions = new TextureRegion[Creature.STATUS + 1];
		color = new Color(0, 0, 0, 0);
		eyeColor = new Color(0, 0, 0, 1);
		this.g = g;
		this.express();
		
		legThick *= width*7/200;
		if(legThick % 2 == 0)
			++legThick;
		if(width % 2 == 0)
			++width;
		legLength = (int) (legLength * width * 2.0 / TLEG.w);
		speed *= Math.sqrt(legLength)/6;
		height = (float)(legLength + width * .8) - legThick;
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
		speed *= ((g.chromosomes[i].a[j] == Allele.DOM) ? 3 : ((g.chromosomes[i].a[j] == Allele.REC) ? 1 : .1)) + ((g.chromosomes[i].b[j] == Allele.DOM) ? 3 : ((g.chromosomes[i].b[j] == Allele.REC) ? 1 : .1));
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
		
		Color tempcolor = new Color(.9f, .9f, .9f, 1);
		if(!albino) {
			color = tempcolor.sub(color.b + color.g, color.r + color.b, color.g + color.r, 0);
		}
		else {
			color = tempcolor;
			eyeColor = new Color(1, 0, 0, 1);
		}
	}

	public void moveToward(float cx, float cy) {
		vx += cx * accel * (onGround ? 1 : .5);
		
		if(cy > 0 && onGround) // Jump
			vy = jump;
		else if(cy < 0)
			vy += cy;
		
		if(vx > speed)
			vx = speed;
		else if(vx < -speed)
			vx = -speed;
	}
	
	public void update(Level level, SpriteBatch batch) {
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
		
		walkStep += (vx) / (legLength/2.0f);
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
				Level.blocktype b = level.blocks[r][c];
				if(b == blocktype.GRASS){
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
				if(b == blocktype.GRASS){
					if(v < 0)
						onGround = true;
					y = r*level.tile + cor;
					vy = 0;
					done = true;
					break;
				}
			}
		}
	}
	
	public void setupTexture(Texture texture, int tx, int ty) {
		Pixmap pix = new Pixmap(256, 64, Pixmap.Format.RGBA4444);
		Pixmap.setBlending(Pixmap.Blending.SourceOver);
		Pixmap.setFilter(Pixmap.Filter.BiLinear);
		
		Color shadowcolor = (Color) color.cpy();
		shadowcolor.lerp(Color.BLACK, .3f);
		
		//Body
		Trect body = (Trect) TBODY.clone();
		body.h = body.w = (int)width;
		pix.setColor(shadowcolor);
		pix.fillCircle(body.getCenterX() + 1, body.getCenterY() + 1, body.w/2);
		pix.setColor(color);
		int offsetshadow = (int)Math.round(body.w/28.0);
		int shadowsize = (int)Math.round(body.w * .43f);
		pix.fillCircle(body.getCenterX() - offsetshadow + 1, body.getCenterY() - offsetshadow + 1, shadowsize);
		
		//Leg
		Trect leg = (Trect) TLEG.clone();
		leg.w = legLength;
		leg.h = legThick;
		
		pix.setColor(shadowcolor);
		int halflegthick = legThick/2;
		pix.fillRectangle(leg.x + halflegthick, leg.y, leg.w - legThick, leg.h);
		pix.fillCircle(leg.x + halflegthick - 1, leg.y + halflegthick, halflegthick);
		pix.fillCircle(leg.x + leg.w - halflegthick - 1, leg.y + halflegthick, halflegthick);
		pix.setColor(color);
		pix.fillRectangle(leg.x + halflegthick, (int) Math.round(leg.y + leg.h/3.0), leg.w - legThick, (int) Math.round(leg.h*2/3.0));
		pix.fillCircle(leg.x + leg.w - halflegthick - 1, leg.y + (int) Math.round(halflegthick*4/3.0), (int) Math.round(halflegthick*2/3.0));
		
		//Eye
		Trect eye = (Trect) TEYE.clone();
		eye.w = (int) (width * TEYE.w / TBODY.w);
		eye.h = eye.w;
		pix.setColor(Color.WHITE);
		pix.fillCircle(eye.getCenterX(), eye.getCenterY(), eye.w/2);
		pix.setColor(eyeColor);
		pix.fillCircle(eye.getCenterX(), eye.getCenterY(), eye.w*4/12);
		pix.setColor(Color.BLACK);
		pix.fillCircle(eye.getCenterX(), eye.getCenterY(), eye.w/5);
		pix.setColor(Color.WHITE);
		pix.fillCircle(eye.getCenterX() - eye.w/10, eye.getCenterY() - eye.w/10, eye.w/12);
		
		// Commit the pixmap to the texture
		texture.draw(pix, tx, ty);
		pix.dispose();
		
		regions[Creature.BODY] = new TextureRegion(texture, tx + body.x, ty + body.y, body.w + 1, body.h + 1);
		regions[Creature.LEG] = new TextureRegion(texture, tx + leg.x, ty + leg.y, leg.w, leg.h);
		regions[Creature.EYE] = new TextureRegion(texture, tx + eye.x, ty + eye.y, eye.w + 1, eye.h + 1);
		regions[Creature.STATUS] = new TextureRegion(texture, tx, ty, leg.w + body.w + 3, body.h + 20);
	}
	
	public void draw(SpriteBatch batch) {
		int legPivot = legThick/2 + 1;
		float angle = Math.abs(walkStep*70) % (360);
		float righta = angle > 180 ? 180 - (angle) : angle - 180;
		//float tempy = (float) (y + Math.sin(righta/180*Math.PI + Math.PI)*legLength/4.25 + legLength/4.25);//width/2;
		//float tempy = (float) (y + Math.sin(righta/180*Math.PI + Math.PI)*legLength/4.25) + legLength - width/3 - legThick;//height - width;
		float tempy = (float) (y + (Math.sin(righta/180*Math.PI + Math.PI)*(legLength - legPivot)/3.6f) + legLength - legLength/3.2f - legThick);
		float lefta = 180 - righta;
		int yleg = (int)(width*.3 + tempy);
		
		
		batch.draw(regions[Creature.LEG], x + width/4 - legPivot, yleg - legThick, // Left
				legPivot, legPivot, // originX, originY
				legLength, legThick, // width, height
				1, 1, // scaleX, scaleY
				lefta/2 + 135); // rotation
		//batch.draw(regions[Creature.LEG], x + width/4 - legPivot, yleg - legThick); // Left debug
		batch.draw(regions[Creature.LEG], x + width - legThick - width/4 + legPivot, yleg - legThick, // Right
				legPivot, legPivot, // originX, originY
				legLength, legThick, // width, height
				1, 1, // scaleX, scaleY
				righta/2 - 45); // rotation
		batch.draw(regions[Creature.BODY], x - 1, tempy);
		batch.draw(regions[Creature.EYE], x + width * 3/16 + (((int)vx) == 0 ? 0 : (vx > 0 ? width/9 : -width/9)), tempy + width * 3/16);
		//batch.draw(regions[Creature.STATUS], x, y - 100);
	}
	
	public Creature breed(Creature other) {
		return new Creature((x + other.x)/2, (y + other.y)/2, g.breed(other.g));
	}
}
