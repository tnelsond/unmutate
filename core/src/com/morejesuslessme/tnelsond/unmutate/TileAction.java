package com.morejesuslessme.tnelsond.unmutate;

public class TileAction{
	public int r;
	public int c;
	public int delay = 300;

	public TileAction(int r, int c, int delay){
		this.r = r;
		this.c = c;
	}

	public boolean tick(){
		return --delay <= 0;
	}
}
