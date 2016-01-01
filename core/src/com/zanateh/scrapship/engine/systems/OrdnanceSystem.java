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
import com.zanateh.scrapship.effects.StrikeEffect;
import com.zanateh.scrapship.engine.components.BeamComponent;
import com.zanateh.scrapship.engine.components.IntersectComponent;
import com.zanateh.scrapship.engine.components.OrdnanceComponent;
import com.zanateh.scrapship.engine.components.RenderComponent;
import com.zanateh.scrapship.engine.components.StrikeEffectComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.helpers.IntersectHelper;

public class OrdnanceSystem extends IteratingSystem {

	Array<Entity> ordnanceQueue = new Array<Entity>();
	private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	private ComponentMapper<BeamComponent> beamMapper = ComponentMapper.getFor(BeamComponent.class);
	
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
					
					ImmutableArray<Entity> strikeEntities = engine.getEntitiesFor(Family.all(StrikeEffectComponent.class).get());
					Entity strikeEntity;
					if(strikeEntities.size() == 0 ){
						strikeEntity = new Entity();
						strikeEntity.add(new StrikeEffectComponent());
						strikeEntity.add(new RenderComponent());
						engine.addEntity(strikeEntity);
					}
					else {
						strikeEntity = strikeEntities.first();
					}
					
					StrikeEffectComponent sec = strikeEntity.getComponent(StrikeEffectComponent.class);
					StrikeEffect se = new StrikeEffect();
					PooledEffect pe = StrikeEffect.getEffectPool().obtain();
					se.effect = pe;
					se.position.set(closestHit);
					pe.setPosition(closestHit.x, closestHit.y);
					//pe.getEmitters().first().getVelocity().
					pe.reset();
					sec.strikeEffects.add(se);
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
