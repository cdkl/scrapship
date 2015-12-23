package com.zanateh.scrapship.ship;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public class DestroyShipEventListener implements EventListener {

	@Override
	public boolean handle(Event event) {
		if( event instanceof DestroyShipEvent ) {
			return handleDestroyShip((ComponentShip)event.getTarget()); 
		}
		return false;
	}

	public boolean handleDestroyShip(ComponentShip target) {
		return false;
	}

}
