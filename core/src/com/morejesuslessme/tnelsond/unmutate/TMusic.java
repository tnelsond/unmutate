package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.Input.Peripheral;
	
import java.util.Random;

// 0.5*sqrt(2)

public class TMusic{
	int samplerate = 44100;
	float notes[] = {
		261.63f, // C4
		293.66f, // D4
		329.63f, // E4
		349.23f, // F4
		392.00f, // G4
		440.00f, // A4
		493.88f, // B4
		523.25f, // C5
		587.33f, // D5
		369.99f, // F#4 
	};
	TNote sheet[] = {
		makeFluteNote(2),
		makeFluteNote(4),
		makeFluteNote(6),
		makeFluteNote(6),
		makeFluteNote(6),
		makeFluteNote(5),
		makeFluteNote(7),
		makeFluteNote(6),
		makeFluteNote(5),
		makeFluteNote(4, 1),

		makeFluteNote(5),
		makeFluteNote(6),
		makeFluteNote(4),
		makeFluteNote(2),
		makeFluteNote(4),
		makeFluteNote(5),
		makeFluteNote(9),
		makeFluteNote(2),
		makeFluteNote(1),
		makeFluteNote(2, 2),
	};

	TNote makeFluteNote(int note){
		return new TNote(samplerate, notes[note]);
	}

	TNote makeFluteNote(int note, float duration){
		return new TNote(samplerate, notes[note], duration);
	}

	TNote makeFluteNote(int note, float duration, float attack, float decay){
		return new TNote(samplerate, notes[note], duration, attack, decay);
	}

	public TMusic(){
		AudioDevice playbackDevice = Gdx.audio.newAudioDevice(samplerate, true);
		int totalduration = 0;
		for(TNote t : sheet){
			totalduration += t.duration;
		}
		float[] samples = new float[totalduration]; // 20 seconds mono audio
		int j = 0;
		float start = 0;
		for(int i = 0; i < samples.length; ++i){
			float pos = i - start;
			if(sheet[j].isDone(pos)){
				start = i;
				j = (j + 1) % sheet.length;
			}
			samples[i] = sheet[j].getValue(pos) * .3f;
			//samples[i] = a + b - a * b;
		}

		boolean quit = false;
		while(!quit){
			playbackDevice.writeSamples(samples, 0, samples.length);
		}
		playbackDevice.dispose();
	}
}
