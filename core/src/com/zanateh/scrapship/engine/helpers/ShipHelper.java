package com.zanateh.scrapship.engine.helpers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.zanateh.scrapship.engine.components.BodyComponent;
import com.zanateh.scrapship.engine.components.FixtureComponent;
import com.zanateh.scrapship.engine.components.HardpointComponent;
import com.zanateh.scrapship.engine.components.PodComponent;
import com.zanateh.scrapship.engine.components.SpriteComponent;
import com.zanateh.scrapship.engine.components.ThrusterComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;

public class ShipHelper {
	
	final static float podRadius = 0.5f;
	
	private static ComponentMapper<PodComponent> podMapper = ComponentMapper.getFor(PodComponent.class);
	private static ComponentMapper<FixtureComponent> fixtureMapper = ComponentMapper.getFor(FixtureComponent.class);
	private static ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);

	
	
	public static Entity createShipEntity(Engine engine, World world) {
		Entity e = new Entity();
		
		BodyDef def = new BodyDef();
		def.position.set(0,0);
		def.type = BodyDef.BodyType.DynamicBody;
		
		def.angularDamping = 0.8f;
		def.linearDamping = 0.2f;

		BodyComponent bc = new BodyComponent();
		bc.body = world.createBody(def);
		e.add(bc);

		e.add(new TransformComponent());
		
		engine.addEntity(e);;
		
		return e;
	}

	public static Entity createPodEntity(Engine engine, World world) {
		Entity e = new Entity();
		
		PodComponent pc = new PodComponent();
		e.add(pc);
		TransformComponent tc = new TransformComponent();
		e.add(tc);
		FixtureComponent fc = new FixtureComponent();
		e.add(fc);
		HardpointComponent hc = new HardpointComponent();
		e.add(hc);
		ThrusterComponent thc = new ThrusterComponent();
		e.add(thc);
		SpriteComponent sc = new SpriteComponent();
		Texture image = new Texture(Gdx.files.internal("data/pod.png"));
		sc.sprite = new Sprite(image);
		sc.sprite.setSize(1,1);
		sc.sprite.setOrigin(sc.sprite.getWidth()/2, sc.sprite.getHeight()/2);
		e.add(sc);
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
	}
	
	public static void removePodFromShip(Entity podEntity) {
		PodComponent pc = podMapper.get(podEntity);
		FixtureComponent fc = fixtureMapper.get(podEntity);

		Entity shipEntity = pc.ship;
		BodyComponent shipBC = bodyMapper.get(shipEntity);
		
		shipBC.body.destroyFixture(fc.fixture);
		fc.fixture = null;
	}
	
	public static void destroyIfNoComponentsForShip(Entity shipEntity, Engine engine, World world) {
		Family pods = Family.all(PodComponent.class).get();
		boolean found = false;
		for(Entity podEntity : engine.getEntitiesFor(pods) ) {
			PodComponent pc = podMapper.get(podEntity);
			if(pc.ship == shipEntity) {
				found = true;
				break;
			}
		}
		if( !found ) {
			destroyShipInternal(shipEntity, engine, world);
		}
	}
	
	public static void destroyShip(Entity shipEntity, Engine engine, World world) {
		Family pods = Family.all(PodComponent.class).get();
		for(Entity podEntity : engine.getEntitiesFor(pods) ) {
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
