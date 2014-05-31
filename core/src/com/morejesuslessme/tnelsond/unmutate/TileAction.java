package com.morejesuslessme.tnelsond.unmutate;

public class TileAction extends Index{
	public int delay;

	public TileAction(int r, int c, int delay){
		super(r, c);
		this.delay = delay;
	}

	public boolean tick(){
		return --delay <= 0;
	}
}
