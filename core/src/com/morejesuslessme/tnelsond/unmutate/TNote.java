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
	public float durationfactor;
	public float notes[];
	public int index = 0;
	public int start = 0;
	//public float vibratofreq;
	//public float vibratospeed;
	//public float vibratoair;

	public TNote(int samplerate, float notes[], int tempo, float freq, float duration, float attack, float decay){
		this.notes = notes;
		this.samplerate = samplerate;
		setTempo(tempo);
		set(freq, duration, attack, decay);
		this.decayrate = decay;
		this.attackrate = attack;
		set(notes[0], notes[1]);
		//this.vibratospeed = 80f/samplerate;
		//this.vibratofreq = 0.000001f;
		//this.vibratoair = 0.05f;
	}

	public void setTempo(int tempo){
		durationfactor = samplerate * 4 * 60 / tempo; 
	}

	public void set(float freq, float duration, float attackrate, float decayrate){
		this.freq = freq / samplerate;
		this.duration = (int)(duration * durationfactor);
		this.attack = this.duration*attackrate;
		this.decay = this.duration*decayrate;
	}

	public void set(float freq, float duration){
		this.freq = freq / samplerate;
		this.duration = (int)(duration * durationfactor);
		this.attack = this.duration * attackrate;
		this.decay = this.duration * decayrate;
	}

	public void set(float freq){
		this.freq = freq / samplerate;
	}

	//public float getVibrato(float pos){
		//return MathUtils.sin(pos*vibratospeed);
	//}

	public float getValue(int i){
		if(freq == 0){
			return 0;
		}
		int pos = i - start;
		if(pos > duration){
			start = i;
			pos = 0;
			index = (index + 2) % notes.length;	
			set(notes[index], notes[index+1]);
		}
		float vol = getVolume(pos);// + getVibrato(pos) * vibratoair;
		//if(vol < 0)
			//vol = 0;
		return wave(pos) * vol;
	}

	public float wave(int pos){
		return MathUtils.sin(pos*MathUtils.PI2*(freq));// + getVibrato(pos) * vibratofreq)) * vol;
	}

	public float getVolume(int pos){
		if(pos < attack){
			return (float)Math.sqrt(pos / attack);
		}
		if(pos < duration - decay){
			return 1f;
		}
		return (float)Math.sqrt((duration - pos) / decay);
	}
}
