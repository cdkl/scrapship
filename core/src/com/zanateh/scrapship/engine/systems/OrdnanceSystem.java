package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.BeamComponent;
import com.zanateh.scrapship.engine.components.IntersectComponent;
import com.zanateh.scrapship.engine.components.OrdnanceComponent;
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
