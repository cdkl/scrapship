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
import com.zanateh.scrapship.engine.components.PickableComponent;
import com.zanateh.scrapship.engine.components.PodComponent;
import com.zanateh.scrapship.engine.components.ShipComponent;
import com.zanateh.scrapship.engine.components.PlayerControlComponent;
import com.zanateh.scrapship.engine.components.RenderComponent;
import com.zanateh.scrapship.engine.components.ThrusterComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Hardpoint;

public class ShipHelper {
	
	final static float podRadius = 0.5f;
	
	private static ComponentMapper<PodComponent> podMapper = ComponentMapper.getFor(PodComponent.class);
	private static ComponentMapper<FixtureComponent> fixtureMapper = ComponentMapper.getFor(FixtureComponent.class);
	private static ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
	private static ComponentMapper<ShipComponent> shipMapper = ComponentMapper.getFor(ShipComponent.class);
	private static ComponentMapper<HardpointComponent> hardpointMapper = ComponentMapper.getFor(HardpointComponent.class);

	
	
	public static Entity createShipEntity(Engine engine, World world) {
		Entity e = new Entity();

		e.add(new ShipComponent());
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
		e.add(new PickableComponent());
		
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
		HardpointComponent hc = hardpointMapper.get(podEntity);

		Entity shipEntity = pc.ship;
		BodyComponent shipBC = bodyMapper.get(shipEntity);
		ShipComponent shipSC = shipMapper.get(shipEntity);
		
		if(hc != null ) {
			HardpointHelper.detachAll(hc.hardpoints);
		}
		
		shipSC.pods.removeValue(podEntity, true);
		shipBC.body.destroyFixture(fc.fixture);
		fc.fixture = null;
		pc.ship = null;
	}
	
	public static void attachPodToShipPod(Entity podEntity, Hardpoint hp1, Entity shipPodEntity, Hardpoint hp2) {
		if( hp1.attached != null ) {
			throw new RuntimeException("Cannot attach hardpoints: hardpoint " + hp1.toString() + " already attached to " + hp1.attached.toString());
		}
		if( hp2.attached != null ) {
			throw new RuntimeException("Cannot attach hardpoints: hardpoint " + hp2.toString() + " already attached to " + hp2.attached.toString());
		}

		// Remove pod from previous ship
		PodComponent podEntityComponent = podMapper.get(podEntity);
		PodComponent shipPodComponent = podMapper.get(shipPodEntity);
		Entity previousShipEntity = podEntityComponent.ship;
		if( previousShipEntity != null ) {
			removePodFromShip(podEntity);
		}
		
		// Attach hardpoints
		HardpointHelper.attach(hp1, hp2);
		
		// Transform newly attached pod
		FixtureComponent shipPodFixture = fixtureMapper.get(shipPodEntity);
		FixtureComponent podFixture = fixtureMapper.get(podEntity);
		Vector2 hp2Position = 
				new Vector2(hp2.position).rotate(shipPodFixture.localRotation);
		float hp2Rotation = hp2Position.angle();
		hp2Position.add(shipPodFixture.localPosition);
		
		// hp2Position is now the position of hp2 locally for the ship. 
		// This will be where hp1 has to end up when podEntity is transformed correctly.

		// podRotation is how far the pod has to be rotated so that its hardpoint is diametrically opposed from the other
		float podRotation = (hp2Rotation + 180f - hp1.position.angle()) % 360f;
		
		// Now we work out the pod's position by rotating hp1's position, and subbing that rotated position from hp2Position.
		Vector2 hp1Vector = new Vector2(hp1.position);
		hp1Vector.rotate(podRotation);
		Vector2 podPosition = new Vector2(hp2Position);
		podPosition.sub(hp1Vector);
	
		addPodToShip(podEntity, shipPodComponent.ship, podPosition, podRotation);
		// Secondary attachments
		
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
