package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.OneTickComponent;

public class CleanupSystem extends IteratingSystem {

	Array<Entity> cleanupQueue = new Array<Entity>();
	
	Engine engine;
	
	public CleanupSystem(Engine engine) {
		super(Family.all(OneTickComponent.class).get());
		this.engine = engine;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		for(Entity entity : cleanupQueue) {
			engine.removeEntity(entity);
		}
		
		cleanupQueue.clear();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		cleanupQueue.add(entity);
	}

}
