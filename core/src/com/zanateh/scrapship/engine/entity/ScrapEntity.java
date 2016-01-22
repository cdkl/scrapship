package com.zanateh.scrapship.engine.entity;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.zanateh.scrapship.engine.components.ShipAIComponent;
import com.zanateh.scrapship.engine.message.Message;

public class ScrapEntity extends Entity {
	static long nextID = 1;
	static ComponentMapper<ShipAIComponent> AIMapper = ComponentMapper.getFor(ShipAIComponent.class);
	
	
	long ID = nextID++;
	public long ID() {
		return ID;
	}
	
}
