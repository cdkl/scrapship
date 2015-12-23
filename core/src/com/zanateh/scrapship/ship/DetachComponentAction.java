package com.zanateh.scrapship.ship;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.zanateh.scrapship.ship.component.PodComponent;

public class DetachComponentAction extends Action {

	PodComponent detachee;
	Fixture fixture;
	
	public DetachComponentAction(PodComponent detachee, Fixture fixture) {
		this.detachee = detachee;
		this.fixture = fixture;
	}
	
	@Override
	public boolean act(float delta) {
		ComponentShip ship = (ComponentShip)this.actor;
		
		ship.reactToDetach(detachee, fixture);
		
		return true;
	}

	
}
