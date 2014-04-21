package com.morejesuslessme.tnelsond.unmutate;

import java.io.Reader;
import java.util.Scanner;

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
		DIRT, NONE
	}
	private AtlasRegion dirt;
	public int w = 12;
	public int h = 16;
	public int tile = 32;
	public blocktype blocks[][];
	public float GRAVITY = 0.4f;
	
	public Level(TextureAtlas atlas, String filename){
		dirt = atlas.findRegion("dirt");
		Reader reader = Gdx.files.internal(filename).reader();
		Scanner scan = new Scanner(reader);
		w = scan.nextInt();
		h = scan.nextInt();
		blocks = new blocktype[h][w];
		int row = h;
		while(scan.hasNextLine()) {
			--row;
			int col = 0;
			String str = scan.nextLine();
			for(int i=0; i<str.length(); ++i){
				char ch = str.charAt(i);
				if(ch == '#'){
					blocks[row][col] = Level.blocktype.DIRT;
				}
				else
					blocks[row][col] = Level.blocktype.NONE;
				++col;
			}
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
				if(blocks[r][c] == Level.blocktype.DIRT){
					batch.draw(dirt, c*tile, r*tile, tile, tile);
				}
			}
		}
	}
}
