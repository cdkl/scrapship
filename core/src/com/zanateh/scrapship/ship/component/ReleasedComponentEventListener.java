package com.zanateh.scrapship.ship.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public class ReleasedComponentEventListener implements EventListener {

	@Override
	public boolean handle(Event event) {
		// TODO Auto-generated method stub
		if( ! (event instanceof ReleasedComponentEvent) ) return false;
		Gdx.app.log("Selection", "Event Handler for ReleasedComponent");
		ReleasedComponentEvent rcEvent = (ReleasedComponentEvent)event;
		
		return componentReleased(rcEvent);
	}

	public boolean componentReleased(ReleasedComponentEvent event) {
		return false;
	}

}
