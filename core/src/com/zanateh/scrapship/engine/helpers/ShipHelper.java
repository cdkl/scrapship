package com.zanateh.scrapship.engine.helpers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.BodyComponent;
import com.zanateh.scrapship.engine.components.FixtureComponent;
import com.zanateh.scrapship.engine.components.HardpointComponent;
import com.zanateh.scrapship.engine.components.PodComponent;
import com.zanateh.scrapship.engine.components.ShipComponent;
import com.zanateh.scrapship.engine.components.PlayerControlComponent;
import com.zanateh.scrapship.engine.components.RenderComponent;
import com.zanateh.scrapship.engine.components.ThrusterComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;

public class ShipHelper {
	
	final static float podRadius = 0.5f;
	
	private static ComponentMapper<PodComponent> podMapper = ComponentMapper.getFor(PodComponent.class);
	private static ComponentMapper<FixtureComponent> fixtureMapper = ComponentMapper.getFor(FixtureComponent.class);
	private static ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
	private static ComponentMapper<ShipComponent> shipMapper = ComponentMapper.getFor(ShipComponent.class);

	
	
	public static Entity createShipEntity(Engine engine, World world) {
		Entity e = new Entity();

		e.add(new ShipComponent());
		e.add(new PlayerControlComponent());
		e.add(new TransformComponent());
		
		BodyDef def = new BodyDef();
		def.position.set(0,0);
		def.type = BodyDef.BodyType.DynamicBody;
		
		def.angularDamping = 0.8f;
		def.linearDamping = 0.2f;

		BodyComponent bc = new BodyComponent();
		bc.body = world.createBody(def);
		e.add(bc);
		
		engine.addEntity(e);;
		
		return e;
	}

	public static Entity createPodEntity(Engine engine, World world) {
		Entity e = new Entity();

		e.add(new PodComponent());
		e.add(new TransformComponent());
		e.add(new FixtureComponent());
		e.add(new HardpointComponent());
		e.add(new ThrusterComponent());
		e.add(new RenderComponent());
		engine.addEntity(e);
		
		return e;
	}

	public static void addPodToShip(Entity podEntity, Entity shipEntity, Vector2 pos, float deg) throws RuntimeException {
		PodComponent pc = podMapper.get(podEntity);
		FixtureComponent fc = fixtureMapper.get(podEntity);
		
		if(pc.ship != null) {
			throw new RuntimeException("Cannot add pod " + podEntity.toString() + " to ship, already has ship.");
		}
		if(fc.fixture != null ) {
			throw new RuntimeException("Cannot create fixture for pod " + podEntity.toString() + " to ship, already has fixture.");
		}
		
		pc.ship = shipEntity;
		ShipComponent shipSC = shipMapper.get(shipEntity);
		if(shipSC.pods.contains(podEntity, true)) {
			throw new RuntimeException("Cannot add pod " + podEntity.toString() + " to ship, ship already references it.");
		}
		shipSC.pods.add(podEntity);
		
		BodyComponent shipBC = bodyMapper.get(shipEntity);
		
		FixtureDef fixDef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(podRadius);
		//setRotation(deg); // can't rotate a sphere
		shape.setPosition(pos);
		
		fixDef.shape = shape;
		fixDef.density = 1.0f;
		fixDef.restitution = 0.5f;
		fixDef.friction = 0.6f;
		fc.fixture = shipBC.body.createFixture(fixDef);
		fc.localPosition.set(pos);
		fc.localRotation = deg;
	}
	
	public static void removePodFromShip(Entity podEntity) {
		PodComponent pc = podMapper.get(podEntity);
		FixtureComponent fc = fixtureMapper.get(podEntity);

		Entity shipEntity = pc.ship;
		BodyComponent shipBC = bodyMapper.get(shipEntity);
		ShipComponent shipSC = shipMapper.get(shipEntity);
		
		shipSC.pods.removeValue(podEntity, true);
		shipBC.body.destroyFixture(fc.fixture);
		fc.fixture = null;
	}
	
	public static void destroyIfNoComponentsForShip(Entity shipEntity, Engine engine, World world) {
		ShipComponent shipSC = shipMapper.get(shipEntity);

		if( shipSC.pods.size == 0 ) {
			destroyShipInternal(shipEntity, engine, world);
		}
	}
	
	public static void destroyShip(Entity shipEntity, Engine engine, World world) {
		ShipComponent shipSC = shipMapper.get(shipEntity);

		Array<Entity> podArray = new Array<Entity>(shipSC.pods);
		
		for(Entity podEntity : podArray ) {
			PodComponent pc = podMapper.get(podEntity);
			if(pc.ship == shipEntity) {
				removePodFromShip(podEntity);
				engine.removeEntity(podEntity);
			}
		}
		destroyShipInternal(shipEntity, engine, world);	
	}
	
	private static void destroyShipInternal(Entity shipEntity, Engine engine, World world) {
		BodyComponent bc = bodyMapper.get(shipEntity);
		world.destroyBody(bc.body);
		engine.removeEntity(shipEntity);
	}

	public static void setShipTransform(Entity shipEntity, Vector2 position, float angle) {
		BodyComponent bc = bodyMapper.get(shipEntity);
		bc.body.setTransform(position, angle * MathUtils.degreesToRadians);	
	}
	
}
