package com.zanateh.scrapship.scene;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;

public class ScrapShipActorGroup extends Group {

	// seems important not to have hit() create vector2s, fuckkk
	private Vector2 myPoint = new Vector2();
	
	public void postUpdate()
	{
		for( Actor actor : getChildren() ) {
			if( actor instanceof ScrapShipActor ) {
				((ScrapShipActor) actor).postUpdate();
			}
			else if( actor instanceof ScrapShipActorGroup) {
				((ScrapShipActorGroup) actor).postUpdate();
			}
			
		}
	}
	
	@Override
	public Actor hit(float x, float y, boolean touchable) {
		if (touchable && getTouchable() == Touchable.disabled) return null;
		Array<Actor> children = this.getChildren();
		for (int i = children.size - 1; i >= 0; i--) {
			Actor child = children.get(i);
			if (!child.isVisible()) continue;
			child.parentToLocalCoordinates(myPoint.set(x, y));
			Actor hit = child.hit(myPoint.x, myPoint.y, touchable);
			if (hit != null) return hit;
		}
		return ScrapShipActorHelper.ScrapShipActorHit(this, x, y, touchable);
	}

}
