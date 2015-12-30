package com.zanateh.scrapship.engine.components.subcomponents;

import com.badlogic.gdx.math.Vector2;

public class Thruster {
	public Vector2 position = new Vector2();
	public Vector2 direction = new Vector2();
	public float strength = 0f;
	public float power = 0f;
	
	public Thruster() {
	}
	
	public Thruster(Vector2 position, Vector2 direction, float strength) {
		this.position.set(position);
		this.direction.set(direction);
		this.strength = strength;
	}
}
