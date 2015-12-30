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
import com.zanateh.scrapship.engine.helpers.ShipHelper;

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
			if(bodyComponent != null ) {
				ShipHelper.updateTransformFromBody(transformComponent, bodyComponent);
			}
			else {
				FixtureComponent fixtureComponent = fixtureMapper.get(entity);
				ShipHelper.updateTransformFromFixture(transformComponent, fixtureComponent);
			}
		}
		
		bodiesQueue.clear();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		bodiesQueue.add(entity);
	}

}
