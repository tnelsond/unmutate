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
		DIRT, NONE, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT, BREED1MALE, BREED1FEMALE, BREED1CHILD
	}
	private AtlasRegion dirt, slant;
	public int w = 12;
	public int h = 16;
	public int tile = 32;
	public blocktype blocks[][];
	public Breeder breeder1;
	public float GRAVITY = 0.4f;
	
	public Level(TextureAtlas atlas, String filename){
		dirt = atlas.findRegion("dirt");
		slant = atlas.findRegion("dirtslant");
		Reader reader = Gdx.files.internal(filename).reader();
		Scanner scan = new Scanner(reader);
		w = scan.nextInt();
		h = scan.nextInt();
		boolean breeder = false;
		breeder1 = new Breeder(0, 0, 0);
		blocks = new blocktype[h][w];
		int row = h;
		while(scan.hasNextLine()) {
			int col = 0;
			String str = scan.nextLine();
			for(int i=0; i<str.length(); ++i){
				char ch = str.charAt(i);
				if(ch == '#'){
					if(breeder){
						blocks[row][col] = Level.blocktype.BREED1CHILD;
						blocks[row + 1][col] = Level.blocktype.BREED1CHILD;
					}
					else{
						blocks[row][col] = Level.blocktype.DIRT;
					}
				}
				else if(ch == '1'){
					breeder = true;
					breeder1.row = row;
					breeder1.colFemale = col;
					blocks[row][col] = Level.blocktype.BREED1FEMALE;
				}
				else if(ch == '2'){
					breeder = false;
					breeder1.colMale = col;
					blocks[row][col] = Level.blocktype.BREED1MALE;
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

	public void update(){
		breeder1.update();
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
					if(blocks[r][c] == Level.blocktype.BREED1FEMALE){
						batch.setColor(breeder1.femaleColor());
					}
					else if(blocks[r][c] == Level.blocktype.BREED1MALE){
						batch.setColor(breeder1.maleColor());
					}
					else if(blocks[r][c] == Level.blocktype.BREED1CHILD){
						batch.setColor(breeder1.childColor());
					}
					else{
						if((r + c) % 2 == 1)
							batch.setColor(.1f, .6f, 0, 1);
						else
							batch.setColor(.1f, .5f, 0, 1);
						if(blocks[r][c] != Level.blocktype.DIRT){
							batch.draw(slant, c*tile - 0.5f, r*tile - 0.5f, tile/2, tile/2, tile + 1, tile + 1, (blocks[r][c] == Level.blocktype.UPLEFT || blocks[r][c] == Level.blocktype.DOWNLEFT) ? -1 : 1, (blocks[r][c] == Level.blocktype.UPRIGHT || blocks[r][c] == Level.blocktype.UPLEFT) ? -1 : 1, 0);
							continue;
						}
					}

					batch.draw(dirt, c*tile - 0.5f, r*tile - 0.5f, tile + 1, tile + 1); // Compensating for gaps.
				}
			}
		}
	}
}
