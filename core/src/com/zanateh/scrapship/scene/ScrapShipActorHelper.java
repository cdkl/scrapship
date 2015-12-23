package com.zanateh.scrapship.scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class ScrapShipActorHelper {

	public static Actor ScrapShipActorHit(
			Actor actor, float x, float y,
			boolean touchable) {
			if (touchable && actor.getTouchable() != Touchable.enabled) return null;
			return x >= -(actor.getWidth()) && x < actor.getWidth() && y >= -actor.getHeight() && y < actor.getHeight() ? actor : null;
	}

}
