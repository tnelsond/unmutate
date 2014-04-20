package com.morejesuslessme.tnelsond.unmutate;

public class Trect implements Cloneable {
	public int x;
	public int y;
	public int w;
	public int h;
	
	public Trect() {
		x = y = w = h = 0;
	}
	
	public Trect(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public void grow(int h, int v) {
		this.x -= h;
		this.w += 2*h;
		this.y -= v;
		this.h += 2*v;
	}
	
	public void growTo(int h, int w){
		int growx = w - this.w;
		int growy = h - this.h;
		this.x -= growx/2;
		this.w += growx;
		this.y -= growy/2;
		this.h += growy;
	}
	
	public int getCenterX() {
		return x + w/2;
	}
	
	public int getCenterY() {
		return y + h/2;
	}
	
	public Object clone() {
		return new Trect(x, y, w, h);
	}
}