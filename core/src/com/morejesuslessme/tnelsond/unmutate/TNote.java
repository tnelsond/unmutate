package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.math.MathUtils;

import java.lang.Math;
import java.util.Scanner;

public class TNote{
	public float freq;
	public float freq2;
	public int duration;
	public float dur;
	public float attack;
	public float decay;
	public float attackrate;
	public float decayrate;
	public int samplerate;
	public float durationfactor;
	public int index = 0;
	public int start = 0;
	public int oct = 4;
	public Scanner sc;
	//public float vibratofreq;
	//public float vibratospeed;
	//public float vibratoair;

	public TNote(int samplerate, Scanner sc, float freq, float duration, float attack, float decay){
		this.samplerate = samplerate;
		set(freq, duration, attack, decay);
		this.decayrate = decay;
		this.attackrate = attack;
		this.sc = sc;
		setTempo(sc.nextInt());
		//this.vibratospeed = 80f/samplerate;
		//this.vibratofreq = 0.000001f;
		//this.vibratoair = 0.05f;
	}

	public float[] write(){
		int totalduration = (int)(sc.nextInt() * durationfactor) + 1;
		float samples[] = new float[totalduration];
		return write(samples);
	}

	public float[] write(float samples[]){
		set();
		for(int i = 0; i < samples.length; ++i){
			float f = getValue(i);
			samples[i] =  f / 2;
		}
		return samples;
	}

	public void setTempo(int tempo){
		durationfactor = samplerate * 4 * 60 / tempo; 
	}

	public void set(){
		if(!sc.hasNext()){
			freq = 0;
			return;
		}
		String s = sc.next();
		float dur = 0;
		boolean half = false;
		int oct = this.oct;
		for(int j = 0; j < s.length(); ++j){
			char c = s.charAt(j);
			if(Character.isDigit(c)){
				dur *= 10;
				dur += c - '0';
			}
			else if(c == '\''){
				++oct;
			}
			else if(c == ','){
				--oct;	
			}
			else if(c == '.'){
				half = true;
			}
		}
		if(dur == 0){
			dur = this.dur;
		}
		else{
			dur = 1f/dur;
			if(half)
				dur += dur/2;
		}
		//if(s.startsWith("(")){
		//	s = s.substring(1);
		//}
		if(s.startsWith("a#") || s.startsWith("bb"))
			freq = TMusic.AS[oct];
		else if(s.startsWith("c#") || s.startsWith("db"))
			freq = TMusic.CS[oct];
		else if(s.startsWith("d#") || s.startsWith("eb"))
			freq = TMusic.DS[oct];
		else if(s.startsWith("f#") || s.startsWith("gb"))
			freq = TMusic.FS[oct];
		else if(s.startsWith("g#") || s.startsWith("ab"))
			freq = TMusic.GS[oct];
		else if(s.startsWith("a"))
			freq = TMusic.A[oct];
		else if(s.startsWith("b"))
			freq = TMusic.B[oct];
		else if(s.startsWith("c"))
			freq = TMusic.C[oct];
		else if(s.startsWith("d"))
			freq = TMusic.D[oct];
		else if(s.startsWith("e"))
			freq = TMusic.E[oct];
		else if(s.startsWith("f"))
			freq = TMusic.F[oct];
		else if(s.startsWith("g"))
			freq = TMusic.G[oct];

		set(freq, dur);
		
		System.out.println(s + " " + dur);
	}

	public void set(float freq, float dur, float attackrate, float decayrate){
		this.freq = freq / samplerate;
		this.dur = dur;
		this.duration = (int)(duration * durationfactor);
		this.attack = this.duration*attackrate;
		this.decay = this.duration*decayrate;
	}

	public void set(float freq, float dur){
		this.freq = freq / samplerate;
		this.dur = dur;
		this.duration = (int)(dur * durationfactor);
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
			set();
		}
		float vol = getVolume(pos);// + getVibrato(pos) * vibratoair;
		//if(vol < 0)
			//vol = 0;
		return wave(pos) * vol;
	}

	public float wave(int pos){
		return MathUtils.sin(pos*MathUtils.PI2*(freq));// + getVibrato(pos) * vibratofreq)) * vol;
		//return (pos%freq)/(freq2) - 1;// + getVibrato(pos) * vibratofreq)) * vol;
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
