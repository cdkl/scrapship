package com.zanateh.scrapship.engine.components.subcomponents;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class Hardpoint {
	public Hardpoint attached = null;
	public Vector2 position = new Vector2();
	
	public Hardpoint(Vector2 position) {
		this.position.set(position);
	}
	
	public float hardpointRadius = 0.1f;
}
