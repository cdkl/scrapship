package com.zanateh.scrapship.engine.ai;

import java.util.PrimitiveIterator.OfDouble;
import java.util.Random;
import java.util.stream.DoubleStream;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.zanateh.scrapship.engine.components.PlayerControlComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.message.AttackMessage;
import com.zanateh.scrapship.engine.message.Message;

public class WanderState extends State {

	Vector2 wanderGoal = new Vector2();
	final static float WANDER_EXTENT = 20f;
	final static float SEEK_TOLERANCE = 2f;
	final static float ARC_HALF_WIDTH = 45f;
	final static float COURSE_TOLERANCE = 5f;
	static Random rand = new Random(12345);
	
	private static ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	private static ComponentMapper<PlayerControlComponent> controlMapper = ComponentMapper.getFor(PlayerControlComponent.class);
	
	public WanderState(Entity entity) {
		enter(entity);
	}

	@Override
	public void enter(Entity entity) {
		// Pick a point to wander to in the world.
		OfDouble posStreamIter = rand.doubles(2, -WANDER_EXTENT, WANDER_EXTENT).iterator();
		wanderGoal.set(posStreamIter.next().floatValue(),(float) posStreamIter.next().floatValue() );
		Gdx.app.log("AI", entity.toString() + " heading to position " + this.wanderGoal.toString());
	}

	@Override
	public void execute(Entity entity, float deltaTime) {
		// Are we at the goal?
		TransformComponent tc = transformMapper.get(entity);
		if(tc == null) {
			throw new RuntimeException("State applied to entity without position.");
		}
		
		Vector2 diff = new Vector2(wanderGoal);
		diff.sub(tc.position);
		if( diff.len2() <= SEEK_TOLERANCE*SEEK_TOLERANCE) {
			// If close enough, move into a new wander state.
			ShipStateMachine.changeState(entity, new WanderState(entity));
		}
		else {
			PlayerControlComponent pcc = controlMapper.get(entity);
			pcc.shipControl.resetThrust();
			
			// Is the target in our front arc?
			float bearing = (diff.angle() - tc.rotation) % 360f;
			if( Math.abs(bearing) <= ARC_HALF_WIDTH) {
				// fire forward
				pcc.shipControl.setForwardThrust(1f);
			}
			if( bearing < -COURSE_TOLERANCE ) {
				pcc.shipControl.setCWThrust(1f);
				// turn left
			}
			else if( bearing > COURSE_TOLERANCE) {
				pcc.shipControl.setCCWThrust(1f);
			}
		}
		
		
	}

	@Override
	public void exit(Entity entity) {
	}
	
	@Override
	public boolean processMessage(Entity entity, Message message) {
		if(message instanceof AttackMessage) {
			// react!
			Gdx.app.log("AI", "I'm being attacked!");
			return true;
		}
		return false;
	}
	
}
