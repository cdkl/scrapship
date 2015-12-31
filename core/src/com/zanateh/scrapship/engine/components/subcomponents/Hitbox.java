package com.zanateh.scrapship.engine.components.subcomponents;

import com.badlogic.gdx.math.Vector2;
import com.zanateh.scrapship.engine.components.IntersectComponent;

public class Hitbox {
	public IntersectComponent component;
	
	public final Vector2 position = new Vector2();
	public float radius = 0.5f;
	
	public Hitbox(IntersectComponent component, Vector2 position, float radius) {
		this.component = component;
		this.position.set(position);
		this.radius = radius;
	}

}
