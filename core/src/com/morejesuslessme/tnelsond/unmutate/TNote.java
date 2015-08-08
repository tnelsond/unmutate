package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.math.MathUtils;

import java.lang.Math;
import java.util.Scanner;

public class TNote{
	public int duration;
	public float dur;
	public float attack;
	public float decay;
	public float attackrate;
	public float decayrate;
	public float defaultdecay;
	public int samplerate;
	public float durationfactor;
	public int start = 0;
	public int oct = 4;
	public float step = 0;
	public float goalstep = 0;
	public float stepchange = 0;
	public float input = 0;
	public boolean blend = false;
	public Scanner sc;
	public float vibratofact;
	//public float vibratospeed;
	//public float vibratoair;

	public TNote(int samplerate, Scanner sc, float freq, float duration, float attack, float decay){
		this.samplerate = samplerate;
		set(freq, duration, attack, decay);
		this.decayrate = decay;
		this.attackrate = attack;
		this.sc = sc;
		setTempo(sc.nextInt());
		this.defaultdecay = decayrate;
		//this.vibratospeed = 80f/samplerate;
		this.vibratofact = 20f/samplerate;
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
			step = 0;
			return;
		}
		String s = sc.next();
		float dur = 0;
		boolean half = false;
		boolean tie = false;
		int oct = this.oct;
		float freq = step * samplerate / MathUtils.PI2;
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
			else if(c == '~'){
				tie = true;
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

		if(blend){
			goalstep = MathUtils.PI2 * freq / samplerate;
			stepchange = (goalstep - step) / attack;
		}
		else{
			step = MathUtils.PI2 * freq / samplerate;
			stepchange = 0;
			goalstep = step;
			//input = 0;
		}
		set(dur, tie ? 0 : defaultdecay);
		blend = tie;
	}

	public void set(float freq, float dur, float attackrate, float decayrate){
		this.step = MathUtils.PI2 * freq / samplerate;
		this.dur = dur;
		this.duration = (int)(duration * durationfactor);
		this.attack = this.duration*attackrate;
		this.decay = this.duration*decayrate;
	}
	
	public void set(float dur, float decayrate){
		this.dur = dur;
		this.duration = (int)(dur * durationfactor);
		this.attack = this.duration * attackrate;
		this.decay = this.duration * decayrate;
	}

	//public void set(float freq, float dur){
	//	set(freq, dur, decayrate);
	//}

	public void set(float freq, float dur, float decayrate){
		this.step = MathUtils.PI2 * freq / samplerate;
		this.dur = dur;
		this.duration = (int)(dur * durationfactor);
		this.attack = this.duration * attackrate;
		this.decay = this.duration * decayrate;
	}

	public void set(float freq){
		this.step = MathUtils.PI2 * freq / samplerate;
	}

	//public float getVibrato(float pos){
		//return MathUtils.sin(pos*vibratospeed);
	//}

	public float getValue(int i){
		if(step == 0){
			return 0;
		}
		int pos = i - start;
		if(pos > duration){
			start = i;
			pos = 0;
			set();
		}
		float vol = getVolume(pos);
		return wave(pos)*0.8f * vol;
	}

	public float wave(int pos){
		float vibrato = MathUtils.sin(pos*vibratofact) * 0.003f + 1f;
		step += stepchange;
		if(step != goalstep && pos >= attack){
			stepchange = 0;
			step = goalstep;
		}
		input += step*vibrato;
		return MathUtils.sin(input) + MathUtils.sin(input/2)/8f + MathUtils.sin(input/4)/32f + MathUtils.sin(input*2)/32f;// + getVibrato(pos) * vibratofreq)) * vol;
		//return (pos%freq)/(freq2) - 1;// + getVibrato(pos) * vibratofreq)) * vol;
	}

	public float getVolume(int pos){
		float tremolo = (MathUtils.sin(pos*vibratofact) + 1) / 8f + 1 - 1/4f;
		if(pos < attack && stepchange == 0){
			return (float)Math.sqrt(pos / attack)*tremolo;
		}
		if(pos <= duration - decay){
			return tremolo;
		}
		return (float)Math.sqrt((duration - pos) / decay)*tremolo;
	}
}
