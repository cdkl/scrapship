package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.ShipControlVisitor;
import com.zanateh.scrapship.engine.components.PlayerControlComponent;
import com.zanateh.scrapship.engine.components.ShipComponent;
import com.zanateh.scrapship.ship.IShipControl;

public class PlayerControlSystem extends IteratingSystem {

	private Array<Entity> playerControlledEntities = new Array<Entity>();
	private ShipControlVisitor shipControlVisitor = new ShipControlVisitor(); 
	
	private ComponentMapper<ShipComponent> shipMapper = ComponentMapper.getFor(ShipComponent.class);
	
	public PlayerControlSystem() {
		super( Family.all(PlayerControlComponent.class).get());		
	}
	
	public IShipControl getShipControl() {
		return shipControlVisitor;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		for(Entity entity : playerControlledEntities) {
			shipControlVisitor.visit(entity);
		}
		
		playerControlledEntities.clear();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		playerControlledEntities.add(entity);

	}

}
