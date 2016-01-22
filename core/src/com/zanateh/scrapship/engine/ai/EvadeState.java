package com.zanateh.scrapship.engine.ai;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.zanateh.scrapship.engine.components.PlayerControlComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.entity.ScrapEntity;
import com.zanateh.scrapship.engine.helpers.NavigationHelper;

public class EvadeState extends State implements EntityListener {

	private static ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	private static ComponentMapper<PlayerControlComponent> controlMapper = ComponentMapper.getFor(PlayerControlComponent.class);

	
	Entity attacker;
	
	private static float safeDistance = 10f;
	
	public EvadeState(Entity entity, Entity attacker) {
		this.attacker = attacker;
		enter(entity);
	}

	@Override
	public void enter(Entity entity) {
	}

	@Override
	public void execute(Entity entity, float deltaTime) {
		// Do we still have an attacker?
		if(attacker == null) {
			Gdx.app.log("AI", "Whew I'm safe. My attacker is gone.");
			ShipStateMachine.revertToPreviousState(entity);
		}
		else {
			// Where's our attacker? We want to turn and thrust away from it!
			TransformComponent myTC = transformMapper.get(entity);
			TransformComponent enemyTC = transformMapper.get(attacker);
			
			Vector2 desiredVector = new Vector2(myTC.position);
			desiredVector.sub(enemyTC.position);
			
			if(desiredVector.len2() > safeDistance*safeDistance) {
				Gdx.app.log("AI", "Whew I'm safe. I'm at " + myTC.position + " and my attacker is at " + enemyTC.position);
				ShipStateMachine.revertToPreviousState(entity);
			}
			else {
				NavigationHelper.moveTowards(entity, desiredVector);
			}
		}
	}

	@Override
	public void exit(Entity entity) {
	}

	@Override
	public void entityAdded(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entityRemoved(Entity entity) {
		// THIS COMPLETELY WON'T WORK BECAUSE WE're NOT SUBSCRIBED
		if(entity == attacker) {
			// Attacker is dead.
			attacker = null;
		}
	}

}
