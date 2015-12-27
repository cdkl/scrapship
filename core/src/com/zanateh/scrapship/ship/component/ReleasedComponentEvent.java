package com.zanateh.scrapship.ship.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;

public class ReleasedComponentEvent extends Event {
	Vector2 position = new Vector2();
	float rotation;
	
	public ReleasedComponentEvent(Vector2 position, float rotation)
	{
		this.position.set(position);
		this.rotation = rotation;
	}
	
	public Vector2 getPosition()
	{
		return this.position;
	}
	
	public float getRotation()
	{ 
		return this.rotation;
	}
}
