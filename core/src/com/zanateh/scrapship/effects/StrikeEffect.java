package com.zanateh.scrapship.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;

public class StrikeEffect {
	public final Vector2 position = new Vector2();
	public float rotation = 0f;
	
	public PooledEffect effect = null;
	
	private static ParticleEffect strikeEffectTemplate = null;
	private static ParticleEffectPool particleEffectPool = null; 
	public static ParticleEffectPool getEffectPool() {
		if(particleEffectPool == null) {
			strikeEffectTemplate = new ParticleEffect();
			strikeEffectTemplate.load(Gdx.files.internal("effects/strike.p"), Gdx.files.internal("img"));
			strikeEffectTemplate.scaleEffect(0.01f);
			particleEffectPool = new ParticleEffectPool(strikeEffectTemplate, 1, 64);
		}
		return particleEffectPool;
	}

}
