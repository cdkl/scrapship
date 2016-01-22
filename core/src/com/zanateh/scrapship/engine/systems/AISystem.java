package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.IShipControl;
import com.zanateh.scrapship.engine.ShipControlVisitor;
import com.zanateh.scrapship.engine.ai.ShipStateMachine;
import com.zanateh.scrapship.engine.components.PlayerControlComponent;
import com.zanateh.scrapship.engine.components.PodComponent;
import com.zanateh.scrapship.engine.components.ShipAIComponent;
import com.zanateh.scrapship.engine.components.ShipComponent;
import com.zanateh.scrapship.engine.entity.EntityRegistry;
import com.zanateh.scrapship.engine.entity.ScrapEntity;
import com.zanateh.scrapship.engine.message.Message;
import com.zanateh.scrapship.engine.message.MessageHandler;

public class AISystem extends IteratingSystem implements MessageHandler {

	private Array<Entity> AIEntities = new Array<Entity>();
	
	private ComponentMapper<ShipAIComponent> shipAIMapper = ComponentMapper.getFor(ShipAIComponent.class);
	private ComponentMapper<PodComponent> podMapper = ComponentMapper.getFor(PodComponent.class);
	

	public AISystem() {
		super( Family.all(ShipAIComponent.class).get());		
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		for(Entity entity : AIEntities) {
			ShipStateMachine.update(entity, delta);
		}
		
		AIEntities.clear();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		AIEntities.add(entity);

	}

	@Override
	public boolean handle(Message message) {
		ScrapEntity se = EntityRegistry.instance().getEntity(message.receiver);
		if( se != null ) {
			// If entity recipient is a pod, forward the message to the ship.
			PodComponent pod = podMapper.get(se);
			if(pod != null) {
				ShipAIComponent ai = shipAIMapper.get(pod.ship);
				if( ai != null && ai.currentState.processMessage(pod.ship, message)) {
					return true;
				}
			}
		}
		return false;
	}

}
