package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.FixtureComponent;
import com.zanateh.scrapship.engine.components.ThrusterComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Thruster;

public class ThrusterSystem extends IteratingSystem {
	
	private ComponentMapper<ThrusterComponent> thrusterMapper = ComponentMapper.getFor(ThrusterComponent.class);
	private ComponentMapper<FixtureComponent> fixtureMapper = ComponentMapper.getFor(FixtureComponent.class);
	
	private Array<Entity> thrusterQueue = new Array<Entity>();
	
	public ThrusterSystem() {
		super(Family.all(ThrusterComponent.class, FixtureComponent.class).get());
	}

	
	@Override
	public void update( float delta ) {
		super.update(delta);
		
		for(Entity entity : thrusterQueue) {
			FixtureComponent fc = fixtureMapper.get(entity);
			ThrusterComponent tc = thrusterMapper.get(entity);
			
			Body body = fc.fixture.getBody();
			for(Thruster thruster : tc.thrusters) {
				Vector2 thrusterPos = new Vector2(thruster.position);
				thrusterPos.add(fc.localPosition);
				Vector2 thrusterVec = new Vector2(thruster.direction);
				thrusterVec.scl(thruster.strength * thruster.power);
				
				body.applyForce(body.getWorldVector(thrusterVec), body.getWorldPoint(thrusterPos), true);
			}
		}
		
		thrusterQueue.clear();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		thrusterQueue.add(entity);
	}
	
	
	
}
