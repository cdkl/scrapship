package com.zanateh.scrapship.engine.message;

import com.badlogic.ashley.core.Entity;

public class AttackMessage extends Message {

	public Entity attacker = null;
	
	public AttackMessage(long sender, long receiver, float delay, Entity attacker) {
		super(sender, receiver, delay);
		this.attacker = attacker;
	}
}
