package com.morejesuslessme.tnelsond.unmutate;

import java.util.Iterator;

public class Titer implements Iterator<Object>{
	int start;
	int end;
	int step;
	
	public Titer(int start, int end){
		this.start = start;
		this.end = end;
		this.step = 1;
		if(start > end)
			this.step = -1;
	}
	public Titer(int start, int end, int step){
		this(start, end);
		this.step *= step;
	}
	
	public boolean hasNext() {
		return (step > 0 && start < end) || (step < 0 && start > end);
	}
	@Override
	public Object next() {
		return start += step;
	}
	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}
}
