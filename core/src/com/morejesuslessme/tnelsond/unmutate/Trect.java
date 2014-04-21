package com.morejesuslessme.tnelsond.unmutate;

public class Trect implements Cloneable {
	public float x;
	public float y;
	public float w;
	public float h;
	
	public Trect() {
		x = y = w = h = 0;
	}
	
	public Trect(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public void grow(float h, float v) {
		this.x -= h;
		this.w += 2*h;
		this.y -= v;
		this.h += 2*v;
	}

	public void scale(float sw, float sh){
		this.w *= sw;
		this.h *= sh;
	}
	
	public void growTo(float h, float w){
		float growx = w - this.w;
		float growy = h - this.h;
		this.x -= growx/2;
		this.w += growx;
		this.y -= growy/2;
		this.h += growy;
	}

	public void setCenter(float x, float y){
		this.x = x - this.w/2;
		this.y = y - this.h/2;
	}
	
	public float getCenterX() {
		return x + w/2;
	}
	
	public float getCenterY() {
		return y + h/2;
	}
	
	public Object clone() {
		return new Trect(x, y, w, h);
	}
}
