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

import java.lang.Character;

public class Level implements Json.Serializable{
	public AtlasRegion dirt, slant, grass;
	public int w = 12;
	public int h = 16;
	public int tile = 32;
	public int blocks[][];
	public Index[] spawns = new Index[4];

	public float GRAVITY = 0.4f;

	public final static int NONE = 1;
	public final static int DIRT = 2;
	public final static int GRASS = 3;
	public Special[] special = new Special[10];

	public Color dirtColor = new Color(.4f, 0.2f, 0, 1);
	public Color dirtColor2 = new Color(.3f, 0.16f, 0, 1);
	public Color grassColor = new Color(.2f, .5f, 0, 1);
	public Color skyColor = new Color(.4f, .8f, 1, 1);

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
			else if(js.name().equals("special")){
				JsonValue js2 = js.child();
				int i = 0;
				do{
					float[] colors = js2.asFloatArray();
					special[i] = new Special(new Color(colors[0], colors[1], colors[2], 1), null);
					++i;
				}while((js2 = js2.next) != null);
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
						else if(ch == 'o'){
							blocks[row][col] = Level.GRASS;
						}
						else if(ch == '$'){
							spawns[spawn++] = new Index(row, col);
							blocks[row][col] = Level.NONE;
						}
						else if(Character.isDigit(ch)){
							blocks[row][col] = -ch + '0';
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
	}

	// For reflection
	public Level(){

	}

	public void draw(SpriteBatch batch, Vector3 pos, float width, float height){
		int c1 = Math.max((int) ((pos.x - width/2)/tile), 0);
		int r1 = Math.max((int) ((pos.y - height/2)/tile), 0);
		int c2 = Math.min((int) ((pos.x + width/2)/tile) + 1, w);
		int r2 = Math.min((int) ((pos.y + height/2)/tile) + 1, h);
		if(c1 > w || r1 > h)
			return;
		for(int r=r1; r<r2; ++r){
			for(int c=c1; c<c2; ++c){
				if(blocks[r][c] != Level.NONE){
					if(isSpecial(blocks[r][c])){
						batch.setColor(getSpecial(blocks[r][c]).outside);
					}
					else
						batch.setColor(((r + c) % 2 == 1) ? dirtColor : dirtColor2);
					/*
					else{
						if((r + c) % 2 == 1)
							batch.setColor(.4f, .3f, 0, 1);
						else
							batch.setColor(.3f, .2f, 0, 1);
						if(blocks[r][c] != BlockType.DIRT){
							batch.draw(slant, c*tile, r*tile, tile/2, tile/2, tile, tile, (blocks[r][c] == BlockType.UPLEFT || blocks[r][c] == Level.blocktype.DOWNLEFT) ? -1 : 1, (blocks[r][c] == Level.blocktype.UPRIGHT || blocks[r][c] == Level.blocktype.UPLEFT) ? -1 : 1, 0);
							continue;
						}
					}
					*/

					//batch.draw(dirt, c*tile - 0.5f, r*tile - 0.5f, tile + 1, tile + 1); // Compensating for gaps.
					batch.draw(dirt, c*tile, r*tile, tile, tile); // Compensating for gaps.

					if(blocks[r][c] == Level.GRASS){
						batch.setColor(grassColor);
						batch.draw(grass, c*tile, r*tile + tile*3/4, tile, tile/3); // Compensating for gaps.
					}
				}
			}
		}
	}
}
