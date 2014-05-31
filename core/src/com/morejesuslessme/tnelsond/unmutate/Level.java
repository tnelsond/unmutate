package com.morejesuslessme.tnelsond.unmutate;

import java.io.Reader;
import java.util.Scanner;

import java.util.Queue;
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

public class Level {
	public static enum blocktype{
		DIRT, NONE, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT, GRASS
	}
	public AtlasRegion dirt, slant;
	public int w = 12;
	public int h = 16;
	public int tile = 32;
	public blocktype blocks[][];
	public float GRAVITY = 0.4f;
	public Index[] spawns = new Index[4];

	LinkedList grassGrow = new LinkedList();

	public void consume(int r, int c){
		if(blocks[r][c] == Level.blocktype.GRASS){
			blocks[r][c] = Level.blocktype.DIRT;
			int offset = 0;
			if(grassGrow.size() > 0){
				TileAction last = (TileAction) grassGrow.getLast();
				offset = last.delay;
			}
			grassGrow.add(new TileAction(r, c, 1000 - offset));
		}
	}
	
	public void update(){
		if(grassGrow.size() > 0){
			TileAction t = (TileAction) grassGrow.element();
			if(t != null && t.tick()){
				blocks[t.r][t.c] = Level.blocktype.GRASS;
				grassGrow.remove(t);
			}
		}
	}
	
	public Level(TextureAtlas atlas, String filename){
		dirt = atlas.findRegion("dirt");
		slant = atlas.findRegion("dirtslant");
		Reader reader = Gdx.files.internal(filename).reader();
		Scanner scan = new Scanner(reader);
		w = scan.nextInt();
		h = scan.nextInt();
		blocks = new blocktype[h][w];
		int row = h;
		int spawn = 0;
		while(scan.hasNextLine()) {
			int col = 0;
			String str = scan.nextLine();
			for(int i=0; i<str.length(); ++i){
				char ch = str.charAt(i);
				if(ch == '#'){
					blocks[row][col] = Level.blocktype.DIRT;
				}
				else if(ch == 'o'){
					blocks[row][col] = Level.blocktype.GRASS;
				}
				else if(ch == '$'){
					spawns[spawn++] = new Index(row, col);
					blocks[row][col] = Level.blocktype.NONE;
				}
				else if(ch == '/'){
					if(row < h && blocks[row + 1][col] == Level.blocktype.DIRT){
						blocks[row][col] = Level.blocktype.UPLEFT;
					}
					else{
						blocks[row][col] = Level.blocktype.DOWNRIGHT;
					}
				}
				else if(ch == '\\'){
					if(row < h && blocks[row + 1][col] == Level.blocktype.DIRT){
						blocks[row][col] = Level.blocktype.UPRIGHT;
					}
					else{
						blocks[row][col] = Level.blocktype.DOWNLEFT;
					}
				}
				else
					blocks[row][col] = Level.blocktype.NONE;
				++col;
			}
			--row;
		}
		scan.close();
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
				if(blocks[r][c] != Level.blocktype.NONE){
					if(blocks[r][c] == Level.blocktype.GRASS){
						if((r + c) % 2 == 1)
							batch.setColor(.1f, .7f, .0f, 1);
						else
							batch.setColor(.1f, .6f, .0f, 1);
					}
					else{
						if((r + c) % 2 == 1)
							batch.setColor(.4f, .3f, 0, 1);
						else
							batch.setColor(.3f, .2f, 0, 1);
						if(blocks[r][c] != Level.blocktype.DIRT){
							batch.draw(slant, c*tile, r*tile, tile/2, tile/2, tile, tile, (blocks[r][c] == Level.blocktype.UPLEFT || blocks[r][c] == Level.blocktype.DOWNLEFT) ? -1 : 1, (blocks[r][c] == Level.blocktype.UPRIGHT || blocks[r][c] == Level.blocktype.UPLEFT) ? -1 : 1, 0);
							continue;
						}
					}

					//batch.draw(dirt, c*tile - 0.5f, r*tile - 0.5f, tile + 1, tile + 1); // Compensating for gaps.
					batch.draw(dirt, c*tile, r*tile, tile, tile); // Compensating for gaps.
				}
			}
		}
	}
}
