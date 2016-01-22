package com.zanateh.scrapship.engine.message;

import com.zanateh.scrapship.engine.systems.MessageDispatchSystem;

public abstract class Message {
	public long sender;
	public long receiver;
	public float delay;
	
	public Message(long sender, long receiver, float delay) {
		this.sender = sender;
		this.receiver = receiver;
		this.delay = delay;
	}
	
	public void send() {
		MessageDispatchSystem.instance().dispatchMessage(this);
	}

}
