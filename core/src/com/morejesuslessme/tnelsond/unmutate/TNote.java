package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.math.MathUtils;

import java.lang.Math;

public class TNote{
	public float freq;
	public int duration;
	public float attack;
	public float decay;
	//public float vibratofreq;
	//public float vibratospeed;
	//public float vibratoair;

	public TNote(int samplerate, float freq, float duration, float attack, float decay){
		this.freq = freq / samplerate;
		this.duration = (int)(duration * samplerate);
		this.attack = this.duration*attack;
		this.decay = this.duration*decay;
		//this.vibratospeed = 80f/samplerate;
		//this.vibratofreq = 0.000001f;
		//this.vibratoair = 0.05f;
	}

	public TNote(int samplerate, float freq){
		this(samplerate, freq, 1/2f, 1/16f, 1/8f);
	}

	public TNote(int samplerate, float freq, float duration){
		this(samplerate, freq, duration, 1/8f, 1/4f);
	}

	//public float getVibrato(float pos){
		//return MathUtils.sin(pos*vibratospeed);
	//}

	public float getValue(float pos){
		float vol = getVolume(pos);// + getVibrato(pos) * vibratoair;
		//if(vol < 0)
			//vol = 0;
		return MathUtils.sin(pos*MathUtils.PI2*(freq)) * vol;// + getVibrato(pos) * vibratofreq)) * vol;
	}

	public boolean isDone(float pos){
		return pos > duration;
	}

	public float getVolume(float pos){
		if(pos < attack){
			return (float)Math.sqrt(pos / attack);
		}
		if(pos < duration - decay){
			return 1f;
		}
		return (float)Math.sqrt((duration - pos) / decay);
	}
}
