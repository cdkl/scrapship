package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.BodyComponent;
import com.zanateh.scrapship.engine.components.FixtureComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;

public class PhysicsSystem extends IteratingSystem {

	private World world;
	private Array<Entity> bodiesQueue;
	
	private ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
	private ComponentMapper<FixtureComponent> fixtureMapper = ComponentMapper.getFor(FixtureComponent.class);
	private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	
	public PhysicsSystem(World world) {
		super(Family.all(TransformComponent.class).one(BodyComponent.class, FixtureComponent.class).get());
		
		this.world = world;
		this.bodiesQueue = new Array<Entity>();
	}
	
	@Override
	public void update( float delta ) {
		super.update(delta);
		world.step(delta, 7, 3);
		
		for(Entity entity : bodiesQueue) {
			TransformComponent transformComponent = transformMapper.get(entity);
			BodyComponent bodyComponent = bodyMapper.get(entity);
			Vector2 position;
			if(bodyComponent != null ) {
				position = bodyComponent.body.getPosition();
				transformComponent.rotation = bodyComponent.body.getAngle() * MathUtils.radiansToDegrees;
				transformComponent.position.set(position);
			}
			else {
				FixtureComponent fixtureComponent = fixtureMapper.get(entity);
				Body body = fixtureComponent.fixture.getBody();
				
				position = new Vector2(fixtureComponent.localPosition);
	    		body.getTransform().mul(position);
				transformComponent.position.set(position);
				transformComponent.rotation = fixtureComponent.localRotation + (body.getAngle() * MathUtils.radiansToDegrees) % 360f;
			}
		}
		
		bodiesQueue.clear();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		bodiesQueue.add(entity);
	}

}
