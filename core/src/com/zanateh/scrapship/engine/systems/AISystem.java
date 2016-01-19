package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.IShipControl;
import com.zanateh.scrapship.engine.ShipControlVisitor;
import com.zanateh.scrapship.engine.components.PlayerControlComponent;
import com.zanateh.scrapship.engine.components.ShipAIComponent;
import com.zanateh.scrapship.engine.components.ShipComponent;

public class AISystem extends IteratingSystem {

	private Array<Entity> AIEntities = new Array<Entity>();
	
	private ComponentMapper<ShipAIComponent> shipAIMapper = ComponentMapper.getFor(ShipAIComponent.class);
	
	public AISystem() {
		super( Family.all(ShipAIComponent.class).get());		
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		for(Entity entity : AIEntities) {
			ShipAIComponent aic = shipAIMapper.get(entity);
			aic.shipStateMachine.update(entity, delta);
		}
		
		AIEntities.clear();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		AIEntities.add(entity);

	}

}
