package com.morejesuslessme.tnelsond.unmutate;

import java.io.Reader;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class Level {
	public static enum blocktype{
		GRASS, NONE
	}
	TextureRegion grassRegion;
	TextureRegion yellowRegion;
	public int w = 12;
	public int h = 16;
	public int tile = 32;
	public blocktype blocks[][];
	public float GRAVITY = 0.4f;
	private TextureRegion grassRegion2;
	
	public Level(String filename){
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
					blocks[row][col] = Level.blocktype.GRASS;
				}
				else
					blocks[row][col] = Level.blocktype.NONE;
				++col;
			}
		}
		scan.close();
		
	}
	
	public void setupTexture(Texture tex, int x, int y){
		Pixmap pix = new Pixmap(128, 33, Pixmap.Format.RGBA4444);
		Color dirtcolor = new Color(0.1f, 0.5f, 0, 1);
		pix.setColor(dirtcolor);
		pix.fillRectangle(0, 0, tile, tile + 1);
		
		TRandom.r.setSeed(1212);
		for(int i=0; i<10; ++i) {
			float light = (float) TRandom.r.nextGaussian()/2 + .8f;
			Color temp = dirtcolor.cpy().mul(light, light, light, .1f);
			pix.setColor(temp);
			int rx, ry, rwidth, rheight;
			rx = TRandom.r.nextInt(tile);
			ry = TRandom.r.nextInt(tile);
			rwidth = TRandom.r.nextInt(tile/4) + 1;
			rheight = rwidth;
			pix.fillRectangle(rx, ry, rwidth, rheight);
		}
		
		pix.setColor(dirtcolor.mul(1.1f));
		pix.fillRectangle(tile, 0, tile, tile + 1);
		TRandom.r.setSeed(3020);
		for(int i=0; i<30; ++i) {
			float light = (float) TRandom.r.nextGaussian()/2 + .8f;
			Color temp = dirtcolor.cpy().mul(light, light, light, .1f);
			pix.setColor(temp);
			int rx, ry, rwidth, rheight;
			rx = TRandom.r.nextInt(tile);
			ry = TRandom.r.nextInt(tile);
			rwidth = TRandom.r.nextInt(tile/4) + 1;
			rheight = rwidth;
			pix.fillRectangle(rx + tile, ry, rwidth, rheight);
		}
		
		pix.setColor(1, 1, 0, .6f);
		pix.fillRectangle(tile*2, 0, tile, tile + 1);
		tex.draw(pix, x, y);
		grassRegion = new TextureRegion(tex, x, y, tile, tile + 1);
		grassRegion2 = new TextureRegion(tex, x + tile, y, tile, tile + 1);
		yellowRegion = new TextureRegion(tex, x + tile*2, y, tile, tile + 1);
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
				if(blocks[r][c] == Level.blocktype.GRASS){
					batch.draw((r + c) % 2 == 0 ? grassRegion : grassRegion2, c*tile, r*tile);
				}
			}
		}
	}
}
