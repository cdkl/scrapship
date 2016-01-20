package com.zanateh.scrapship.engine.ai;

import com.badlogic.ashley.core.Entity;
import com.zanateh.scrapship.engine.message.Message;

public abstract class State {

	protected ShipStateMachine stateMachine;
	public State(ShipStateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}
	
	public abstract void enter(Entity entity);

	public abstract void execute(Entity entity, float deltaTime);

	public abstract void exit(Entity entity);

	public boolean processMessage(Entity entity, Message message) {
		return false;
	}
}
