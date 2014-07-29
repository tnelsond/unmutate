package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.math.MathUtils;

import java.lang.Math;

public class TNote{
	public float freq;
	public int duration;
	public float attack;
	public float decay;
	public float attackrate;
	public float decayrate;
	public int samplerate;
	public float tempo;
	//public float vibratofreq;
	//public float vibratospeed;
	//public float vibratoair;

	public TNote(int samplerate, int tempo, float freq, float duration, float attack, float decay){
		this.tempo = tempo;
		this.samplerate = samplerate;
		set(freq, duration, attack, decay);
		this.decayrate = decay;
		this.attackrate = attack;
		//this.vibratospeed = 80f/samplerate;
		//this.vibratofreq = 0.000001f;
		//this.vibratoair = 0.05f;
	}

	public void set(float freq, float duration, float attackrate, float decayrate){
		this.freq = freq / samplerate;
		this.duration = (int)(duration * samplerate * 4 * 60 / tempo);
		this.attack = this.duration*attackrate;
		this.decay = this.duration*decayrate;
	}

	public void set(float freq, float duration){
		this.freq = freq / samplerate;
		this.duration = (int)(duration * samplerate * 4 * 60 / tempo);
		this.attack = this.duration * attackrate;
		this.decay = this.duration * decayrate;
	}

	public void set(float freq){
		this.freq = freq / samplerate;
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
