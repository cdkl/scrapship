package com.zanateh.scrapship.scene;

import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class ScrapShipActor extends Actor {

	public void postUpdate()
	{
		
	}
	
	@Override public Actor hit(float x, float y, boolean touchable) {
		return ScrapShipActorHelper.ScrapShipActorHit(this, x, y, touchable);
	}
}
