package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class OrdnanceComponent implements Component {
	public Entity firingEntity = null;
	
	public OrdnanceComponent(Entity firingEntity) {
		this.firingEntity = firingEntity;
	}
}
