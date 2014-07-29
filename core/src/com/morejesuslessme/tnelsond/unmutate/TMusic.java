package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.Input.Peripheral;
	
import java.util.Random;

// 0.5*sqrt(2)

public class TMusic implements Runnable{
	private int samplerate = 10000;
	private volatile boolean running = true;
	private float samples[];

	public static final float C[] = {
		16.35f, 32.70f, 65.41f, 130.81f, 261.63f, 523.25f, 1046.50f, 2093f, 4186.01f
	};
	public static final float CS[] = {
		17.32f, 34.65f, 69.30f, 138.59f, 277.18f, 554.37f, 1108.73f
	};
	public static final float D[] = {
		18.35f, 36.71f, 73.42f, 146.83f, 293.66f, 587.33f, 1174.66f
	};
	public static final float DS[] = {
		19.45f, 38.89f, 77.78f, 155.56f, 311.13f, 622.25f, 1244.51f
	};
	public static final float E[] = {
		20.60f, 41.20f, 82.41f, 164.81f, 329.63f, 659.25f, 1318.51f
	};
	public static final float F[] = {
		21.83f, 43.65f, 87.31f, 174.61f, 349.23f, 698.46f, 1396.91f
	};
	public static final float FS[] = {
		23.12f, 46.25f, 92.50f, 185.00f, 369.99f, 739.99f, 1479.98f
	};
	public static final float G[] = {
		24.50f, 49.00f, 98.00f, 196.00f, 392.00f, 783.99f, 1567.98f
	};
	public static final float GS[] = {
		25.96f, 51.91f, 103.83f, 207.65f, 415.30f, 830.61f, 1661.22f
	};
	public static final float A[] = {
		27.50f, 55.00f, 110.00f, 220.00f, 440.00f, 880.00f, 1760.00f
	};
	public static final float AS[] = {
		29.14f, 58.27f, 116.54f, 223.08f, 466.16f, 932.33f, 1864.66f
	};
	public static final float B[] = {
		30.87f, 61.74f, 123.47f, 246.94f, 493.88f, 987.77f, 1975.53f, 
	};

	public void terminate(){
		running = false;
	}

	public float beat[] = {
		C[4], 1,
		F[3], 3/8f,
		G[3], 1/8f,
		A[3], 1/4f,
		A[3], 1/4f,
		G[3], 3/8f,
		F[3], 1/16f,
		G[3], 3/8f,
		A[3], 1/16f,
		F[3], 1/4f,
		C[3], 1/4f,

		F[3], 3/8f,
		G[3], 1/8f,
		A[3], 1/4f,
		A[3], 1/4f,
		G[3], 3/8f,
		F[3], 1/16f,
		G[3], 3/8f,
		A[3], 1/16f,
		F[3], 1/2f,
	};

	public float notes[] = {
		C[5], 1,
		F[4], 3/8f,
		G[4], 1/8f,
		A[4], 1/4f,
		A[4], 1/4f,
		G[4], 3/8f,
		F[4], 1/16f,
		G[4], 3/8f,
		A[4], 1/16f,
		F[4], 1/4f,
		C[4], 1/4f,

		F[4], 3/8f,
		G[4], 1/8f,
		A[4], 1/4f,
		A[4], 1/4f,
		G[4], 3/8f,
		F[4], 1/16f,
		G[4], 3/8f,
		A[4], 1/16f,
		F[4], 1/2f,
	};

	public TMusic(){
		int tempo = 120;
		float bars = 0;
		for(int b = 1; b < notes.length; b += 2){
			bars += notes[b];
		}
		int totalduration = (int)(bars * samplerate * 4 / tempo * 60) + 1;
		samples = new float[totalduration]; // 10 seconds mono audio

		int j = 0;
		int b = 0;
		float start = 0;
		float bstart = 0;
		TNote flute = new TNote(samplerate, tempo, 0, 1/4f, 1/8f, 1/4f);
		TNote drum = new TNote(samplerate, tempo, 0, 1/4f, 0f, 1f);
		flute.set(notes[j], notes[j+1]);
		drum.set(beat[b], beat[b+1]);
		for(int i = 0; i < samples.length; ++i){
			float pos = i - start;
			float bpos = i - bstart;
			float f = 0;
			if(flute.freq != 0)
				f = flute.getValue(pos);
				//f = (flute.getValue(pos) + 1)/2;
			float d = 0;
			if(drum.freq != 0)
				d = drum.getValue(bpos);
				//d = (drum.getValue(bpos) + 1)/2;
			samples[i] =  (f + d) / 2;
			//samples[i] -= (samples[i] > 0 ? 1 : -1) * f * d;
			if(flute.isDone(pos)){
				start = i;
				j = (j + 2) % notes.length;
				flute.set(notes[j], notes[j+1]);
			}
			if(drum.isDone(bpos)){
				bstart = i;
				b = (b + 2) % beat.length;
				drum.set(beat[b], beat[b+1]);
			}
			//samples[i] = a + b - a * b;
		}
	}

	public void run(){
		AudioDevice playbackDevice = Gdx.audio.newAudioDevice(samplerate, true);
		int samplesperplay = samplerate/2;
		int s = 0;
		while(running){
			int dur = samplesperplay;
			if(s + samplesperplay > samples.length){
				dur = samples.length - s;
			}
			playbackDevice.writeSamples(samples, s, dur);
			s += dur;
			if(s >= samples.length){
				s = 0;
			}
		}
		playbackDevice.dispose();
	}
}
