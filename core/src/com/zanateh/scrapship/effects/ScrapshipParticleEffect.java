package com.zanateh.scrapship.effects;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.zanateh.scrapship.engine.components.ParticleEffectComponent;
import com.zanateh.scrapship.engine.components.RenderComponent;
import com.zanateh.scrapship.engine.entity.ScrapEntity;

public class ScrapshipParticleEffect {
	
	public static void createExplosionEffect(Engine engine, Vector2 location) {
		Entity peEntity = getParticleEffectEntity(engine);
		ParticleEffectComponent sec = peEntity.getComponent(ParticleEffectComponent.class);
		PooledEffect pe = ScrapshipParticleEffect.getExplosionEffectPool().obtain();
		pe.setPosition(location.x, location.y);
		//pe.getEmitters().first().getVelocity().
		pe.reset();
		sec.particleEffects.add(pe);
	}
	
	private static ParticleEffect explosionEffectTemplate = null;
	private static ParticleEffectPool explosionEffectPool = null; 
	public static ParticleEffectPool getExplosionEffectPool() {
		if(explosionEffectPool == null) {
			explosionEffectTemplate = new ParticleEffect();
			explosionEffectTemplate.load(Gdx.files.internal("effects/explosion.p"), Gdx.files.internal("img"));
			explosionEffectTemplate.scaleEffect(0.01f);
			explosionEffectPool = new ParticleEffectPool(explosionEffectTemplate, 1, 64);
		}
		return explosionEffectPool;
	}
	
	public static void createStrikeEffect(Engine engine, Vector2 location) {
		Entity peEntity = getParticleEffectEntity(engine);
		ParticleEffectComponent sec = peEntity.getComponent(ParticleEffectComponent.class);
		PooledEffect pe = ScrapshipParticleEffect.getStrikeEffectPool().obtain();
		pe.setPosition(location.x, location.y);
		//pe.getEmitters().first().getVelocity().
		pe.reset();
		sec.particleEffects.add(pe);
	}
	
	private static ParticleEffect strikeEffectTemplate = null;
	private static ParticleEffectPool strikeEffectPool = null; 
	private static ParticleEffectPool getStrikeEffectPool() {
		if(strikeEffectPool == null) {
			strikeEffectTemplate = new ParticleEffect();
			strikeEffectTemplate.load(Gdx.files.internal("effects/strike.p"), Gdx.files.internal("img"));
			strikeEffectTemplate.scaleEffect(0.01f);
			strikeEffectPool = new ParticleEffectPool(strikeEffectTemplate, 1, 64);
		}
		return strikeEffectPool;
	}
	
	private static Entity getParticleEffectEntity(Engine engine) {
		ImmutableArray<Entity> strikeEntities = engine.getEntitiesFor(Family.all(ParticleEffectComponent.class).get());
		Entity strikeEntity;
		if(strikeEntities.size() == 0 ){
			strikeEntity = new ScrapEntity();
			strikeEntity.add(new ParticleEffectComponent());
			strikeEntity.add(new RenderComponent());
			engine.addEntity(strikeEntity);
		}
		else {
			strikeEntity = strikeEntities.first();
		}
	
		return strikeEntity;
	}

}
