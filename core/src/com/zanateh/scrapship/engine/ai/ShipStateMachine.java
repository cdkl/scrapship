package com.zanateh.scrapship.engine.ai;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.zanateh.scrapship.engine.components.ShipAIComponent;
import com.zanateh.scrapship.engine.entity.ScrapEntity;
import com.zanateh.scrapship.engine.message.Message;

public class ShipStateMachine {
	private static ComponentMapper<ShipAIComponent> shipAIMapper = ComponentMapper.getFor(ShipAIComponent.class);
	
	public static void setCurrentState(Entity entity, State state) {
		ShipAIComponent aic = shipAIMapper.get(entity);
		aic.currentState = state;
	}
	
	public static void setPreviousState(Entity entity, State state) {
		ShipAIComponent aic = shipAIMapper.get(entity);
		aic.previousState = state;
	}
	
	public static void update(Entity entity, float deltaTime) {
		ShipAIComponent aic = shipAIMapper.get(entity);
		if(aic.currentState != null ) {
			aic.currentState.execute(entity, deltaTime);
		}
	}
	
	public static void changeState(Entity entity, State state) {
		ShipAIComponent aic = shipAIMapper.get(entity);
		aic.previousState = aic.currentState;
		aic.previousState.exit(entity);
		aic.currentState = state;
		aic.currentState.enter(entity);
	}
	
	public static void revertToPreviousState(Entity entity) {
		ShipAIComponent aic = shipAIMapper.get(entity);
		State previous = aic.previousState;
		aic.previousState = aic.currentState;
		aic.previousState.exit(entity);
		aic.currentState = previous;
		aic.currentState.enter(entity);
	}
	
	public static boolean isState(Entity entity, State state) {
		ShipAIComponent aic = shipAIMapper.get(entity);
		return state == aic.currentState;
	}

	public static boolean processMessage(Entity entity,Message message) {
		ShipAIComponent aic = shipAIMapper.get(entity);
		return aic.currentState.processMessage(entity, message);
	}
	
}
