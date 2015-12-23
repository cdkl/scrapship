package com.zanateh.scrapship.ship.component;

import com.badlogic.gdx.scenes.scene2d.Action;

public class ThrustAction extends Action {

	float strength;
	
	public ThrustAction(float strength) {
		this.strength = strength;
	}
	
	@Override
	public boolean act(float delta) {		
		ComponentThruster thruster = (ComponentThruster) this.getActor();
		thruster.setThrust(strength);
		
		return true;
	}

}
