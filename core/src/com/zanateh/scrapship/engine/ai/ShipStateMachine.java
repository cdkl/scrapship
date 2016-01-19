package com.zanateh.scrapship.engine.ai;

import com.badlogic.ashley.core.Entity;

public class ShipStateMachine {
	State currentState = null;
	
	State previousState = null;
	
	public ShipStateMachine()
	{
	}
	
	public void setCurrentState(State state) {
		currentState = state;
	}
	
	public void setPreviousState(State state) {
		previousState = state;
	}
	
	public void update(Entity entity, float deltaTime) {
		if(currentState != null ) {
			currentState.execute(entity, deltaTime);
		}
	}
	
	public void changeState(Entity entity, State state) {
		previousState = currentState;
		previousState.exit(entity);
		currentState = state;
		currentState.enter(entity);
	}
	
	public boolean isState(State state) {
		return state == currentState;
	}
	
}
