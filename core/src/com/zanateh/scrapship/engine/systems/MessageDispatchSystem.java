package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.NullComponent;
import com.zanateh.scrapship.engine.components.PlayerControlComponent;
import com.zanateh.scrapship.engine.components.PodComponent;
import com.zanateh.scrapship.engine.components.ShipAIComponent;
import com.zanateh.scrapship.engine.entity.EntityRegistry;
import com.zanateh.scrapship.engine.entity.ScrapEntity;
import com.zanateh.scrapship.engine.message.Message;

public class MessageDispatchSystem extends IteratingSystem {

	EntityRegistry entityRegistry;
	
	static MessageDispatchSystem instance = null;
	
	Array<Message> messages = new Array<Message>(false, 1024);
	
	private ComponentMapper<ShipAIComponent> shipAIMapper = ComponentMapper.getFor(ShipAIComponent.class);
	private ComponentMapper<PodComponent> podMapper = ComponentMapper.getFor(PodComponent.class);
	
	public MessageDispatchSystem(EntityRegistry entityRegistry) {
		super(Family.all(NullComponent.class).get());
		if(instance != null) {
			throw new RuntimeException("Cannot create more than one MessageDispatchSystem");
		}
		instance = this;
		this.entityRegistry = entityRegistry;
	}
	
	public static MessageDispatchSystem instance() {
		return instance;
	}
	
	public void dispatchMessage(Message message) {
		if(message.delay <= 0f) {
			processMessage(message);
			// do immediately.
		}
		else {
			messages.add(message);
		}
	}
	
	@Override
	public void update(float delta) {
		Array<Message> messagesToDeliver = new Array<Message>();
		for(Message message : messages) {
			message.delay -= delta;
			if( message.delay <= 0.0) {
				messagesToDeliver.add(message);
			}
		}
		
		for(Message message : messagesToDeliver) {
			messages.removeValue(message, true);
			processMessage(message);
		}
	}
	
	boolean processMessage(Message message) {
		ScrapEntity se = entityRegistry.getEntity(message.receiver);
		if( se != null ) {
			// If entity recipient is a pod, forward the message to the ship.
			PodComponent pod = podMapper.get(se);
			if(pod != null) {
				return doProcessMessage(pod.ship, message);
			}

			return doProcessMessage(se, message);
		}
		return false;
	}
	
	boolean doProcessMessage(Entity se, Message message) {
		
		ShipAIComponent ai = shipAIMapper.get(se);
		if( ai != null && ai.shipStateMachine.processMessage(se, message)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// TODO Auto-generated method stub

	}

	
}
