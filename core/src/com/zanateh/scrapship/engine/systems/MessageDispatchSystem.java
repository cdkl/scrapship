package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.NullComponent;
import com.zanateh.scrapship.engine.message.Message;
import com.zanateh.scrapship.engine.message.MessageHandler;

public class MessageDispatchSystem extends IteratingSystem {

	static MessageDispatchSystem instance = null;
	
	Array<Message> messages = new Array<Message>(false, 1024);
	
	Array<MessageHandler> handlers = new Array<MessageHandler>();
	
	public MessageDispatchSystem() {
		super(Family.all(NullComponent.class).get());
		if(instance != null) {
			throw new RuntimeException("Cannot create more than one MessageDispatchSystem");
		}
		instance = this;
	}
	
	public static MessageDispatchSystem instance() {
		return instance;
	}
	
	public void addHandler(MessageHandler handler) {
		handlers.add(handler);
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
		for( MessageHandler handler : handlers ) {
			if(handler.handle(message)) {
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// TODO Auto-generated method stub

	}

	
}
