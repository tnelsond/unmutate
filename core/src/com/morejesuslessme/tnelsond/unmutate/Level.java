package com.morejesuslessme.tnelsond.unmutate;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.Preferences;

import java.lang.Character;

import com.morejesuslessme.tnelsond.unmutate.genome.*;

public class Level implements Json.Serializable{
	public AtlasRegion dirt, slant, grass, end, block, hint, eye;
	public int w = 12;
	public int h = 16;
	public int tile = 32;
	public int blocks[][];
	public boolean carryover = false;
	public Index[] spawns = new Index[4];
	public boolean male = false;
	public boolean female = false;

	public int disphint = -1;
	public String[] hints;

	public float GRAVITY = 0.1f;

	public Special[] special = new Special[26];

	public Color dirtColor = new Color(.4f, 0.2f, 0, 1);
	public Color dirtColor2 = new Color(.3f, 0.16f, 0, 1);
	public Color grassColor = new Color(.2f, .5f, 0, 1);
	public Color skyColor = new Color(.4f, .8f, 1, 1);
	public Color hintColor = new Color(0f, .3f, 1, 1);

	public static Json json;
	public static Preferences prefLatest;
	public static Preferences pref;
	public static Preferences prefNext;

	// START Static Stuff
	public final static int NONE = 1;
	public final static int DIRT = 2;
	public final static int GRASS = 3;
	public final static int DEATH = 4;
	public final static int END = 5;
	public final static int HINT_FIRST = 6;
	public final static int HINT_LAST = HINT_FIRST + 26;
	public int chapter = 0;
	public int part = 0;
	public static int newchapter = 0;
	public static int currentgenome = 0;
	public static String prefix = "unmutatelevel";
	public static int[] levels = {3, 0};
	public static Level currentlevel = null;

	public static void initPrefLatest(){
		prefLatest = Gdx.app.getPreferences("main.pref");
		Level.newchapter = prefLatest.getInteger("chapter", 0);
	}

	public Genome getGenome(ChromosomePair[] c, Json j, String s, boolean f){
		switch(chapter){
			case 0:
				if(c != null)
					return new Genome00(c);
				else if(j != null)
					return j.fromJson(Genome00.class, s);
				return new Genome00(f);
			case 1:
				if(c != null)
					return new Genome01(c);
				else if(j != null)
					return j.fromJson(Genome01.class, s);
				return new Genome01(f);
			default:
				if(c != null)
					return new Genome(c);
				else if(j != null)
					return j.fromJson(Genome.class, s);
				return new Genome(f);
		}
	}

	public void ascend(Creature c){
		for(int i = 0; i < 2; ++i){
			String value;
			String key;
			if(c.sex == Genome.Sex.MALE){
				key = "male" + i;
				value = prefNext.getString(key, "null");
			}
			else if(c.sex == Genome.Sex.FEMALE){
				key = "female" + i;
				value = prefNext.getString(key, "null");
			}
			else{
				return;
			}
			if(value.equals("null")){
				String str = json.toJson(c.g);
				prefNext.putString(key, str);
				if(c.sex == Genome.Sex.MALE){
					male = true;
				}
				else if(c.sex == Genome.Sex.FEMALE){
					female = true;
				}

				break;
			}
		}
		prefNext.flush();
	}

	public static String getLevelName(String extension, int chapter, int part){
		return String.format("%s-%d-%d%s", Level.prefix, chapter, part, extension);
	}	

	public static Level makeLevel(Unmutate game, int chapter, int part){
		Level.currentlevel = json.fromJson(Level.class, Gdx.files.internal(Level.getLevelName(".json", chapter, part)));
		Level.currentlevel.chapter = chapter;
		Level.currentlevel.part = part;
		Level.currentlevel.setupTextures(game.atlas);
		Level.currentlevel.setupPrefs();
		return currentlevel;
	}

	public void write(Json json){
		// Levels shouldn't be saved in this way
	}

	public void read(Json json, JsonValue jsonMap){
		JsonValue js = jsonMap.child();
		do{
			if(js.name().equals("dirt")){
				float[] colors = js.asFloatArray();
				dirtColor.r = colors[0];
				dirtColor.g = colors[1];
				dirtColor.b = colors[2];
			}
			else if(js.name().equals("carryover")){
				carryover = js.asBoolean();	
			}
			else if(js.name().equals("dirt2")){
				float[] colors = js.asFloatArray();
				dirtColor2.r = colors[0];
				dirtColor2.g = colors[1];
				dirtColor2.b = colors[2];
			}
			else if(js.name().equals("grass")){
				float[] colors = js.asFloatArray();
				grassColor.r = colors[0];
				grassColor.g = colors[1];
				grassColor.b = colors[2];
			}
			else if(js.name().equals("sky")){
				float[] colors = js.asFloatArray();
				skyColor.r = colors[0];
				skyColor.g = colors[1];
				skyColor.b = colors[2];
			}
			else if(js.name().equals("hint")){
				float[] colors = js.asFloatArray();
				hintColor.r = colors[0];
				hintColor.g = colors[1];
				hintColor.b = colors[2];
			}
			else if(js.name().equals("special")){
				JsonValue js2 = js.child();
				int i = 0;
				do{
					float[] colors = js2.asFloatArray();
					special[i] = new Special(new Color(colors[0], colors[1], colors[2], 1), colors[3]);
					++i;
				}while((js2 = js2.next) != null);
			}
			else if(js.name().equals("hints")){
				hints = js.asStringArray();
			}
			else if(js.name().equals("map")){
				String[] btext = js.asStringArray();

				w = btext[0].length();
				h = btext.length;
				blocks = new int[h][w];
				int row = h - 1;
				int spawn = 0;
				for(int line = 0; line < h; ++line){
					int col = 0;
					String str = btext[line];
					for(int i=0; i<str.length(); ++i){
						char ch = str.charAt(i);
						if(ch == '#'){
							blocks[row][col] = Level.DIRT;
						}
						else if(ch == '^'){
							blocks[row][col] = Level.GRASS;
						}
						else if(ch == '*'){
							blocks[row][col] = Level.END;
						}
						else if(ch == '^'){
							blocks[row][col] = Level.DEATH;
						}
						else if(ch == '$'){
							spawns[spawn++] = new Index(row, col);
							blocks[row][col] = Level.NONE;
						}
						else if(Character.isDigit(ch)){
							blocks[row][col] = -ch + '0';
						}
						else if(ch >= 'a' && ch <= 'z'){
							blocks[row][col] = ch - 'a' + Level.HINT_FIRST;
						}
						else
							blocks[row][col] = Level.NONE;
						++col;
					}
					--row;
				}
			}
		}while((js = js.next()) != null);
	}
	

	public boolean isSpecial(int n){
		return n <= 0;
	}

	public Special getSpecial(int n){
		return special[-n];
	}

	// Checks if a block is solid
	public boolean isSolid(int n, Creature c){
		if(n > 0)
			return true;
		if(n <= 0 && n >= -9 && special[-n] != null){
			return special[-n].isSolid(c);	
		}
		return false;
	}

	LinkedList grassGrow = new LinkedList();

	public void consume(int r, int c){
		if(blocks[r][c] == Level.GRASS){
			blocks[r][c] = Level.DIRT;
			int offset = 0;
			if(grassGrow.size() > 0){
				TileAction last = (TileAction) grassGrow.getLast();
				offset = last.delay;
			}
			grassGrow.add(new TileAction(r, c, 400 - offset));
		}
	}

	public void update(){
		if(grassGrow.size() > 0){
			TileAction t = (TileAction) grassGrow.getFirst();
			if(t != null && t.tick()){
				blocks[t.r][t.c] = Level.GRASS;
				grassGrow.remove(t);
			}
		}
	}
	
	public void setupTextures(TextureAtlas atlas){
		dirt = atlas.findRegion("dirt");
		slant = atlas.findRegion("dirtslant");
		grass = atlas.findRegion("grass");
		end = atlas.findRegion("end");
		block = atlas.findRegion("block");
		eye = atlas.findRegion("eye");
		hint = atlas.findRegion("hint");
	}

	// For reflection
	public Level(){
	}

	public void setupPrefs(){
		pref = Gdx.app.getPreferences(Level.getLevelName(".pref", chapter, part));
		prefNext = Gdx.app.getPreferences(Level.getLevelName(".pref", chapter, part + 1));
		prefNext.clear();
	}

	public boolean done(){
		return male && female;
	}

	public void nextLevel(GameScreen game){	
		boolean finishedchapter = false;
		if(done()){
			if(part >= levels[chapter]){
				finishedchapter = true;
			}
			prefNext.putBoolean("c", true);
			prefNext.flush();
		}
		if(finishedchapter){
			Level.newchapter = chapter + 1;
			prefLatest.putInteger("chapter", Level.newchapter);
			prefLatest.flush();
			game.game.setScreen(new ChapterSelectScreen(game.game));
		}
		else
			game.game.setScreen(new PartSelectScreen(game.game, chapter));

		game.dispose();
	}

	public void draw(SpriteBatch batch, Vector3 pos, float width, float height){
		int c1 = Math.max((int) ((pos.x - width/2)/tile), 0);
		int r1 = Math.max((int) ((pos.y - height/2)/tile), 0);
		int c2 = Math.min((int) ((pos.x + width/2)/tile) + 1, w);
		int r2 = Math.min((int) ((pos.y + height/2)/tile) + 1, h);
		AtlasRegion tex;
		if(c1 > w || r1 > h)
			return;
		for(int r=r1; r<r2; ++r){
			for(int c=c1; c<c2; ++c){
				if(blocks[r][c] != Level.NONE){
					tex = dirt;
					if(blocks[r][c] >= Level.HINT_FIRST && blocks[r][c] <= Level.HINT_LAST){
						batch.setColor(hintColor);
						tex = hint;
					}
					else if(isSpecial(blocks[r][c])){
						Special spec = getSpecial(blocks[r][c]);
						batch.setColor(spec.color);
						if(spec.eye)
							tex = eye;
						else
							tex = block;
					}
					else if(blocks[r][c] == Level.END){
						tex = end;
						if(done()){
							batch.setColor(new Color(1, 1, 0, 1));
						}
						else{
							batch.setColor(new Color(.6f, .6f, 0, 1));
						}
					}
					else
						batch.setColor(((r + c) % 2 == 1) ? dirtColor : dirtColor2);

					batch.draw(tex, c*tile, r*tile, tile, tile);

					// For grass
					if(blocks[r][c] == Level.GRASS){
						batch.setColor(grassColor);
						batch.draw(grass, c*tile, r*tile + tile*3/4, tile, tile/3);
					}
				}
			}
		}
	}
}
