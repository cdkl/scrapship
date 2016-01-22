package com.zanateh.scrapship.engine.helpers;

import java.util.HashSet;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.ai.ShipStateMachine;
import com.zanateh.scrapship.engine.ai.WanderState;
import com.zanateh.scrapship.engine.components.BodyComponent;
import com.zanateh.scrapship.engine.components.CameraTargetComponent;
import com.zanateh.scrapship.engine.components.FixtureComponent;
import com.zanateh.scrapship.engine.components.HardpointComponent;
import com.zanateh.scrapship.engine.components.HitpointComponent;
import com.zanateh.scrapship.engine.components.IntersectComponent;
import com.zanateh.scrapship.engine.components.PickableComponent;
import com.zanateh.scrapship.engine.components.PlayerCommandPodComponent;
import com.zanateh.scrapship.engine.components.PodComponent;
import com.zanateh.scrapship.engine.components.ShipComponent;
import com.zanateh.scrapship.engine.components.PlayerControlComponent;
import com.zanateh.scrapship.engine.components.RenderComponent;
import com.zanateh.scrapship.engine.components.ShipAIComponent;
import com.zanateh.scrapship.engine.components.ThrusterComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.components.WeaponMountComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Hardpoint;
import com.zanateh.scrapship.engine.components.subcomponents.Hitbox;
import com.zanateh.scrapship.engine.components.subcomponents.WeaponMount;
import com.zanateh.scrapship.engine.entity.ScrapEntity;

public class ShipHelper {
	
	final static float podRadius = 0.5f;
	
	private static ComponentMapper<PodComponent> podMapper = ComponentMapper.getFor(PodComponent.class);
	private static ComponentMapper<FixtureComponent> fixtureMapper = ComponentMapper.getFor(FixtureComponent.class);
	private static ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
	private static ComponentMapper<ShipComponent> shipMapper = ComponentMapper.getFor(ShipComponent.class);
	private static ComponentMapper<HardpointComponent> hardpointMapper = ComponentMapper.getFor(HardpointComponent.class);
	private static ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	private static ComponentMapper<WeaponMountComponent> weaponMountMapper = ComponentMapper.getFor(WeaponMountComponent.class);
	private static ComponentMapper<PlayerCommandPodComponent> playerCommandPodMapper = ComponentMapper.getFor(PlayerCommandPodComponent.class);

	public static Entity createPlayerShipEntity(Engine engine, World world) {
		Entity shipEntity = createShipEntity(engine, world, false);
		shipEntity.add(new CameraTargetComponent());
		shipEntity.add(new PlayerControlComponent());
		return shipEntity;
	}
	
	public static Entity createShipEntity(Engine engine, World world, boolean addAI) {
		Entity e = new ScrapEntity();

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
		
		if(addAI) {
			e.add(new PlayerControlComponent());
			e.add(new ShipAIComponent());
			ShipStateMachine.setCurrentState(e, new WanderState(e));
		}
		
		engine.addEntity(e);
		
		return e;
	}

	public static Entity createPodEntity(Engine engine, World world) {
		Entity e = new ScrapEntity();

		PodComponent pc = new PodComponent();
		pc.radius = podRadius;
		e.add(pc);
		e.add(new TransformComponent());
		e.add(new FixtureComponent());
		e.add(new HardpointComponent());
		e.add(new ThrusterComponent());
		e.add(new RenderComponent());
		e.add(new PickableComponent());
		e.add(new HitpointComponent());
		IntersectComponent ic = new IntersectComponent();
		ic.hitboxes.add(new Hitbox(ic, new Vector2(0,0), pc.radius));
		e.add(ic);
		
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
		shape.setRadius(pc.radius);
		//setRotation(deg); // can't rotate a sphere
		shape.setPosition(pos);
		
		fixDef.shape = shape;
		fixDef.density = 1.0f;
		fixDef.restitution = 0.5f;
		fixDef.friction = 0.6f;
		fc.fixture = shipBC.body.createFixture(fixDef);
		fc.localPosition.set(pos);
		fc.localRotation = deg;
		
		TransformComponent tc = transformMapper.get(podEntity);
		ShipHelper.updateTransformFromFixture(tc, fc);
	}
	
	/** Properly remove a pod from a ship-- disconnection and all. Returns the resultant ship(s) (if any)
	 * 
	 * @param engine
	 * @param podEntity
	 */
	public static Array<Entity> removePodFromShip(Engine engine, Entity podEntity) {
		HardpointComponent hc = hardpointMapper.get(podEntity);

		if(hc != null ) {
			HardpointHelper.detachAll(hc.hardpoints);
		}

		PodComponent pc = podMapper.get(podEntity);
		Entity previousShipEntity = pc.ship;
		
		depopulatePodFromShip(podEntity);
		
		if( ! ShipHelper.destroyIfNoComponentsForShip(previousShipEntity, engine) ) {	
			return ShipDivider.divideShipIfBroken(engine, previousShipEntity);
		}
		else {
			return new Array<Entity>();
		}
	}
	
	/** This function removes a pod from a ship, without changing its hardpoint state or deleting the parent ship.
	 * 
	 * @param podEntity
	 */
	public static void depopulatePodFromShip(Entity podEntity) {
		PodComponent pc = podMapper.get(podEntity);
		FixtureComponent fc = fixtureMapper.get(podEntity);

		Entity shipEntity = pc.ship;
		BodyComponent shipBC = bodyMapper.get(shipEntity);
		ShipComponent shipSC = shipMapper.get(shipEntity);
		
		shipSC.pods.removeValue(podEntity, true);
		shipBC.body.destroyFixture(fc.fixture);
		fc.fixture = null;
		pc.ship = null;
	}
	
	public static void attachPodToShipPod(Engine engine, Entity podEntity, Hardpoint hp1, Entity shipPodEntity, Hardpoint hp2) {
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
			removePodFromShip(engine, podEntity);
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
		TransformComponent tc = transformMapper.get(podEntity);
		
		// Secondary attachments
		ImmutableArray<Hardpoint> hps = ShipHelper.getHardpointsForPod(podEntity);
		
		for( Entity otherShipPodEntity : ShipHelper.getPodsForShip(shipPodComponent.ship)) {
			if(otherShipPodEntity == podEntity) {
				continue;
			}
			TransformComponent otherShipPodTransform = transformMapper.get(otherShipPodEntity);
			ImmutableArray<Hardpoint> otherhps = ShipHelper.getHardpointsForPod(otherShipPodEntity);
			
			if( otherhps != null ) {
				for(Hardpoint otherhp : otherhps) {
					if(otherhp.attached != null) {
						continue;
					}
					
					for(Hardpoint hp : hps) {
						if(hp.attached != null) {
							continue;
						}
					
						if(HardpointHelper.intersect(tc, hp, otherShipPodTransform, otherhp)) {
							HardpointHelper.attach(hp, otherhp);
						}
					}
				}
			}
		}
		
	}
	
	
	
	public static boolean destroyIfNoComponentsForShip(Entity shipEntity, Engine engine) {
		ShipComponent shipSC = shipMapper.get(shipEntity);

		if( shipSC.pods.size == 0 ) {
			destroyShipInternal(shipEntity, engine);
			return true;
		}
		return false;
	}
	
	public static void destroyShip(Entity shipEntity, Engine engine) {
		ShipComponent shipSC = shipMapper.get(shipEntity);

		Array<Entity> podArray = new Array<Entity>(shipSC.pods);
		
		for(Entity podEntity : podArray ) {
			PodComponent pc = podMapper.get(podEntity);
			if(pc.ship == shipEntity) {
				depopulatePodFromShip(podEntity);
				engine.removeEntity(podEntity);
			}
		}
		destroyShipInternal(shipEntity, engine);	
	}
	
	private static void destroyShipInternal(Entity shipEntity, Engine engine) {
		BodyComponent bc = bodyMapper.get(shipEntity);
		bc.body.getWorld().destroyBody(bc.body);
		engine.removeEntity(shipEntity);
	}

	public static void setShipTransform(Entity shipEntity, Vector2 position, float angle) {
		BodyComponent bc = bodyMapper.get(shipEntity);
		bc.body.setTransform(position, angle * MathUtils.degreesToRadians);	
		TransformComponent tc = transformMapper.get(shipEntity);
		ShipHelper.updateTransformFromBody(tc, bc);
	}

	public static ImmutableArray<Entity> getPodsForShip(Entity shipEntity) {
		ShipComponent sc = shipMapper.get(shipEntity);
		if( sc == null ) {
			return null;
		}
		return new ImmutableArray<Entity>(sc.pods);
	}
	
	public static ImmutableArray<Hardpoint> getHardpointsForPod(Entity podEntity) {
		HardpointComponent hc = hardpointMapper.get(podEntity);
		if(hc == null) {
			return null;
		}
		return new ImmutableArray<Hardpoint>(hc.hardpoints);
	}

	public static ImmutableArray<WeaponMount> getWeaponMountsForPod(Entity podEntity) {
		WeaponMountComponent wmc = weaponMountMapper.get(podEntity);
		if(wmc == null) {
			return null;
		}
		// TODO Auto-generated method stub
		return new ImmutableArray<WeaponMount>(wmc.weaponMounts);
	}
	
	public static ImmutableArray<Hardpoint> getFreeHardpointsForShip(Entity shipEntity) {
		Array<Hardpoint> returnArray = new Array<Hardpoint>();
				
		for( Entity podEntity : ShipHelper.getPodsForShip(shipEntity)) {
			ImmutableArray<Hardpoint> hps = ShipHelper.getHardpointsForPod(podEntity);
			if( hps != null ) {
				for(Hardpoint hardpoint : hps) {
					if(hardpoint.attached == null) returnArray.add(hardpoint);
				}
			}
		}
		
		return new ImmutableArray<Hardpoint>(returnArray);
	}

	public static void updateTransformFromFixture(TransformComponent transformComponent,
			FixtureComponent fixtureComponent) {
		Body body = fixtureComponent.fixture.getBody();
		Vector2 position = new Vector2(fixtureComponent.localPosition);
		body.getTransform().mul(position);
		transformComponent.position.set(position);
		transformComponent.rotation = fixtureComponent.localRotation + (body.getAngle() * MathUtils.radiansToDegrees) % 360f;
	}

	public static void updateTransformFromBody(TransformComponent transformComponent, BodyComponent bodyComponent) {
		Vector2 position = bodyComponent.body.getPosition();
		transformComponent.rotation = bodyComponent.body.getAngle() * MathUtils.radiansToDegrees;
		transformComponent.position.set(position);
	}
	
	public static Entity getShipForPodEntity(Entity podEntity) {
		PodComponent pc = podMapper.get(podEntity);
		if( pc == null ) {
			throw new RuntimeException("Cannot get ship: entity provided is not a pod.");
		}
		return pc.ship;
	}
	
}



