package com.morejesuslessme.tnelsond.unmutate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;


public class TParticleEffect{
	private float delta = 1/60f;
	private ParticleEffect prototype;
	private ParticleEffectPool pool;
	private Array<PooledEffect> effects;

	public TParticleEffect(String filename, TextureAtlas atlas){
		prototype = new ParticleEffect();
		prototype.load(Gdx.files.internal(filename), atlas, "");
		//prototype.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		prototype.start();
	
		pool = new ParticleEffectPool(prototype, 0, 70);
		effects = new Array<PooledEffect>();
	}

	public void render(SpriteBatch batch){
		for(PooledEffect effect : effects){
			effect.draw(batch, delta);
			if(effect.isComplete()){
				effects.removeValue(effect, true);
				effect.free();
			}
		}
	}

	public void addEffect(float x, float y){
		PooledEffect effect = pool.obtain();
		effect.setPosition(x, y);
		effects.add(effect);
	}

	public void dispose(){
		prototype.dispose();
	}
}

