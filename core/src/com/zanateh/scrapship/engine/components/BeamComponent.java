package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class BeamComponent implements Component {
	private final Vector2 direction = new Vector2(1,0);
	public void setDirection(Vector2 direction) {
		this.direction.set(direction);
		this.direction.nor();
	}
	
	public Vector2 getDirection() {
		return this.direction;
	}
	
	public float range = 10f;
	public float strength = 1f;
	public Vector2 strike = null;
}
