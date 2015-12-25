package com.zanateh.scrapship.ship.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.zanateh.scrapship.ship.ComponentShip;
import com.zanateh.scrapship.ship.ComponentShipFactory;
import com.zanateh.scrapship.state.PlayState;

public class ComponentJoiner extends Actor {

	PlayState playState;
	Stage stage;
	
	
	public ComponentJoiner(PlayState playState, Stage stage) {
		this.playState = playState;
		this.stage = stage;
		this.stage.addActor(this);
		
		final ComponentJoiner target = this;
		
		stage.addListener(new ReleasedComponentEventListener() {
			public boolean componentReleased(ReleasedComponentEvent event) {
				target.handleComponentReleased(event);
				return true;
			}
		});
	}
	
	protected void handleComponentReleased(ReleasedComponentEvent event) {
		PodComponent releasedComponent = (PodComponent)event.getTarget();
		
		releasedComponent.setPosition(event.getPosition());
		Hardpoint intersect = null;
		for( Hardpoint hardpoint : releasedComponent.getHardpoints() ) 
		{
			intersect = intersectHardpoints(hardpoint, stage.getRoot());
			if( intersect != null ) {
				intersect.attach(hardpoint);
				for( Hardpoint otherHardpoint : releasedComponent.getHardpoints() ) {
					if( otherHardpoint != hardpoint ) {
						otherHardpoint.secondaryAttach(intersect.component);
					}
				}
				break;
			}
		}
		
		if( intersect == null ) {
			
			// Time to drop the component. Make a new "ship" for it.
			ComponentShip ship = 
					playState.getShipFactory().createShip(
							ComponentShipFactory.ShipType.EmptyShip);
			
			ship.setPosition(event.getPosition());
			ship.setRotation(event.getRotation());
			ship.attachComponent((PodComponent)event.getTarget(), 0, 0, 0);
		}				

	}

	private Hardpoint intersectHardpoints(Hardpoint hardpoint, Actor actor) {
		if(actor instanceof Hardpoint) {
			Hardpoint hardpointActor = (Hardpoint)actor;

			if( hardpointActor.attached == null && hardpointActor.intersect(hardpoint)) {
				return hardpointActor;
			}
		}
		else if(actor instanceof Group) {
			Group group = (Group)actor;
			for(Actor subActor : group.getChildren()) {
				Hardpoint returnedHardpoint = null;
				if(( returnedHardpoint = intersectHardpoints(hardpoint, subActor)) != null ) {
					return returnedHardpoint;
				}
			}
			
		}
		return null;
	}
	
}
