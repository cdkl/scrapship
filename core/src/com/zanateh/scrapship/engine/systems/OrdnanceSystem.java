package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.effects.ScrapshipParticleEffect;
import com.zanateh.scrapship.engine.components.BeamComponent;
import com.zanateh.scrapship.engine.components.IntersectComponent;
import com.zanateh.scrapship.engine.components.OrdnanceComponent;
import com.zanateh.scrapship.engine.components.RenderComponent;
import com.zanateh.scrapship.engine.components.ParticleEffectComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.entity.ScrapEntity;
import com.zanateh.scrapship.engine.helpers.DamageHelper;
import com.zanateh.scrapship.engine.helpers.IntersectHelper;
import com.zanateh.scrapship.engine.message.AttackMessage;

public class OrdnanceSystem extends IteratingSystem {

	Array<Entity> ordnanceQueue = new Array<Entity>();
	private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	private ComponentMapper<BeamComponent> beamMapper = ComponentMapper.getFor(BeamComponent.class);
	private ComponentMapper<OrdnanceComponent> ordnanceMapper = ComponentMapper.getFor(OrdnanceComponent.class);

	Engine engine;
	
	public OrdnanceSystem(Engine engine) {
		super(Family.all(OrdnanceComponent.class, TransformComponent.class).get());
		
		this.engine = engine;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		for(Entity entity : ordnanceQueue) {
			TransformComponent tc = transformMapper.get(entity);
			BeamComponent bc =  beamMapper.get(entity);
			if( bc != null ) {
				ImmutableArray<Entity> hittables = engine.getEntitiesFor(Family.all(IntersectComponent.class, TransformComponent.class).get());
				Vector2 closestHit = null;
				Entity closestEntity = null;
				for(Entity hittableEntity : hittables) {
					Vector2 hit = IntersectHelper.intersectBeamHitbox(entity, hittableEntity);
					if(hit != null) {
						if( closestHit == null ) {
							closestHit = hit;
							closestEntity = hittableEntity;
						}
						else {
							if(IntersectHelper.getShortestVector(tc.position, closestHit, hit) == hit) {
								closestHit = hit;
								closestEntity = hittableEntity;
							}
						}
					}
				}
				
				if(closestHit != null) {
					Gdx.app.log("Ordnance", "Hit entity " + closestEntity.toString() + " at " + closestHit.toString());
					bc.strike = closestHit;
					
					OrdnanceComponent ordnanceComponent = ordnanceMapper.get(entity);
					new AttackMessage(((ScrapEntity)entity).ID(), ((ScrapEntity)closestEntity).ID(), 0f, ordnanceComponent.firingEntity).send();
					
					// Create the drawable effect for a strike
					ScrapshipParticleEffect.createStrikeEffect(engine, closestHit);
					
					// Evaluate the strike
					DamageHelper.applyDamage(engine, closestEntity, entity, closestHit, deltaTime);
					
				}
			}
		}
		
		ordnanceQueue.clear();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ordnanceQueue.add(entity);
	}

}
