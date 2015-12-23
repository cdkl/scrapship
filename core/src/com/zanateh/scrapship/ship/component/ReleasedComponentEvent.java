package com.zanateh.scrapship.ship.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;

public class ReleasedComponentEvent extends Event {
	Vector2 position = new Vector2();
	
	public ReleasedComponentEvent(Vector2 position)
	{
		this.position.set(position);
	}
	
	public Vector2 getPosition()
	{
		return this.position;
	}
}
