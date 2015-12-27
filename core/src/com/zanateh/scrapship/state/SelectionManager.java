package com.zanateh.scrapship.state;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.zanateh.scrapship.ship.ComponentShip;
import com.zanateh.scrapship.ship.ComponentShipFactory;
import com.zanateh.scrapship.ship.component.PodComponent;

public class SelectionManager {

	PlayStateInputProcessor proc;
	
	ISelectable selectedActor = null;
	Vector2 selectionPosition = new Vector2(0,0);
	
	public SelectionManager(PlayStateInputProcessor proc) {
		this.proc = proc;
	}
	
	public void setSelected(ISelectable actor)
	{
		selectedActor = actor;
		selectedActor.select();
	}
	
	public ISelectable releaseSelected()
	{
		ISelectable actor = selectedActor;
		selectedActor = null;
		actor.release();
		return actor;
	}
	
	public ISelectable getSelected() {
		return selectedActor;
	}
	
	public boolean isSelected(ISelectable actor)
	{
		return actor == selectedActor;
	}

	public void setSelectionPosition(Vector2 stageCoordinates) {
		this.selectionPosition.set(stageCoordinates);
		if( this.selectedActor != null ) {
			this.selectedActor.setPosition(selectionPosition);
		}
	}

	public void rotateSelected(float degrees) {
		if(getSelected() != null) {
			selectedActor.incrementSelectionRotation(degrees);
		}
		
	}
}
