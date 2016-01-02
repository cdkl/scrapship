package com.zanateh.scrapship.engine.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class Hardpoint {
	// Need to maintain a reference back to entity for connectivity checks via hardpoint.
	// This means no swapping HardpointComponents!
	public Entity entity = null;
	public Hardpoint attached = null;
	public Vector2 position = new Vector2();
	
	public Hardpoint(Entity entity, Vector2 position) {
		this.entity = entity;
		this.position.set(position);
	}
	
	public float hardpointRadius = 0.1f;
}
