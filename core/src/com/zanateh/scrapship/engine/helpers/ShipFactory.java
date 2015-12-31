package com.zanateh.scrapship.engine.helpers;

import java.util.Random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.zanateh.scrapship.engine.components.HardpointComponent;
import com.zanateh.scrapship.engine.components.PlayerCommandPodComponent;
import com.zanateh.scrapship.engine.components.ThrusterComponent;
import com.zanateh.scrapship.engine.components.WeaponMountComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Hardpoint;
import com.zanateh.scrapship.engine.components.subcomponents.LaserWeapon;
import com.zanateh.scrapship.engine.components.subcomponents.Thruster;
import com.zanateh.scrapship.engine.components.subcomponents.WeaponMount;

public class ShipFactory {
	Engine engine;
	World world;
	Random rand = new Random();
	
	private static ComponentMapper<HardpointComponent> hardpointMapper = ComponentMapper.getFor(HardpointComponent.class);
	private static ComponentMapper<ThrusterComponent> thrusterMapper = ComponentMapper.getFor(ThrusterComponent.class);
	
	public ShipFactory(Engine engine, World world) {
		this.engine = engine;
		this.world = world;
		rand.setSeed(1234567);
	}
	
	public enum ShipType {
		RandomShip,
		DebugShip,
		PlayerShip,
		EmptyShip
	};

	public Entity createShip(ShipType shipType) {
		Entity ship = null;
		
		switch(shipType) {
		
		case RandomShip:
			{
				Gdx.app.log("ShipFactory", "Creating random ship");
				ship = ShipHelper.createShipEntity(engine, world);

				Entity comp1 = generateRandomPodComponent();
				ShipHelper.addPodToShip(comp1, ship, new Vector2(0,0), 0);
			}
			break;
		
		case DebugShip:
			{
				ship = ShipHelper.createShipEntity(engine, world);
			
				Entity comp1 = ShipHelper.createPodEntity(engine,  world);
				HardpointComponent comp1hp = hardpointMapper.get(comp1);
				comp1hp.hardpoints.add(new Hardpoint(new Vector2(0.5f,0)));
				comp1hp.hardpoints.add(new Hardpoint(new Vector2(0,0.5f)));
				ShipHelper.addPodToShip(comp1, ship, new Vector2(), 0);

				Entity comp2 = ShipHelper.createPodEntity(engine,  world);
				HardpointComponent comp2hp = hardpointMapper.get(comp2);
				comp2hp.hardpoints.add(new Hardpoint(new Vector2(0.5f,0)));
				ShipHelper.attachPodToShipPod(comp2, comp2hp.hardpoints.get(0), comp1, comp1hp.hardpoints.get(0));

				Entity comp3 = ShipHelper.createPodEntity(engine,  world);
				HardpointComponent comp3hp = hardpointMapper.get(comp3);
				comp3hp.hardpoints.add(new Hardpoint(new Vector2(0.5f,0)));
				comp3hp.hardpoints.add(new Hardpoint(new Vector2(-0.5f,0)));
				ShipHelper.attachPodToShipPod(comp3, comp3hp.hardpoints.get(0), comp1, comp1hp.hardpoints.get(1));

				Entity comp4 = ShipHelper.createPodEntity(engine,  world);
				HardpointComponent comp4hp = hardpointMapper.get(comp4);
				comp4hp.hardpoints.add(new Hardpoint(new Vector2(0.5f,0)));
				ShipHelper.attachPodToShipPod(comp4, comp4hp.hardpoints.get(0), comp3, comp3hp.hardpoints.get(1));
				
			}
			break;
		case PlayerShip:
			{
				ship = ShipHelper.createPlayerShipEntity(engine, world);

				Entity comp1 = ShipHelper.createPodEntity(engine,  world);
				comp1.add(new PlayerCommandPodComponent());
				HardpointComponent comp1hc = hardpointMapper.get(comp1);
				comp1hc.hardpoints.add(new Hardpoint(new Vector2(0,0.5f)));
				comp1hc.hardpoints.add(new Hardpoint(new Vector2(0.5f,0)));
				comp1hc.hardpoints.add(new Hardpoint(new Vector2(0,-0.5f)));
				comp1hc.hardpoints.add(new Hardpoint(new Vector2(-0.5f,0)));
				ThrusterComponent comp1tc = thrusterMapper.get(comp1);
				float enginePower = 2;
				comp1tc.thrusters.add(new Thruster(new Vector2(-0.5f,0), new Vector2(1,0), enginePower * 1f));
				comp1tc.thrusters.add(new Thruster(new Vector2(0.5f,0), new Vector2(-1,0), enginePower * 0.4f));
				comp1tc.thrusters.add(new Thruster(new Vector2(0.5f,0), new Vector2(0,1), enginePower * 0.1f));
				comp1tc.thrusters.add(new Thruster(new Vector2(0.5f,0), new Vector2(0,-1), enginePower * 0.1f));
				comp1tc.thrusters.add(new Thruster(new Vector2(-0.5f,0), new Vector2(0,1), enginePower * 0.1f));
				comp1tc.thrusters.add(new Thruster(new Vector2(-0.5f,0), new Vector2(0,-1), enginePower * 0.1f));
				WeaponMountComponent wmc = new WeaponMountComponent();
				comp1.add(wmc);
				WeaponMount mount = new WeaponMount(new Vector2(0.4f,0.4f), new Vector2(1,0));
				mount.setWeapon(new LaserWeapon());
				wmc.weaponMounts.add(mount);
				
				ShipHelper.addPodToShip(comp1, ship, new Vector2(), 0);
			}
			break;
			
		case EmptyShip:
			{
//				ship = new ComponentShip(worldSource.getWorld(), stageSource.getStage());
			}
			break;
		}
		
		return ship;
		
	}

	private Entity generateRandomPodComponent() {
		String logString = "--Hardpoints: ";
		float chanceOfHardpoint = .666f;
		Entity entity = ShipHelper.createPodEntity(engine, world);
		HardpointComponent hc = hardpointMapper.get(entity);
		ThrusterComponent tc = thrusterMapper.get(entity);
		if( rand.nextFloat() <= chanceOfHardpoint ) {
			hc.hardpoints.add(new Hardpoint(new Vector2(0, 0.5f)));
			logString += " +x ";
		}
		if( rand.nextFloat() <= chanceOfHardpoint ) {
			hc.hardpoints.add(new Hardpoint(new Vector2(0.5f, 0)));
			logString += " +y ";
		}
		if( rand.nextFloat() <= chanceOfHardpoint ) {
			hc.hardpoints.add(new Hardpoint(new Vector2(0, -0.5f)));
			logString += " -x ";
		}
		if( rand.nextFloat() <= chanceOfHardpoint ) {
			hc.hardpoints.add(new Hardpoint(new Vector2(-0.5f, 0)));
			logString += " -y ";
		}
		Gdx.app.log("ShipFactory", logString);
		
		logString = "--Thrusters:\n";
		
		int numThrusters = (int) rand.nextGaussian() + 1;
		for( int i = 0 ; i < numThrusters ; ++i ) {
			Vector2 thrusterPosition = randomCardinalDirection().scl(0.5f);
			Thruster thruster = new Thruster(thrusterPosition, randomOutwardCardinalDirection(thrusterPosition), 
					Math.max(1.0f, (float) rand.nextGaussian() * 2 + 2));
			tc.thrusters.add(thruster);
			logString += "----Thruster: " + thrusterPosition;
			logString += " -> " + thruster.toString() + "\n";
		}
		
		Gdx.app.log("ShipFactory", logString);
		
		return entity;
	}
	
	private Vector2 randomCardinalDirection() {
		int i = rand.nextInt(4);
		switch(i) {
		case 0:
			return new Vector2(0, 1);
		case 1:
			return new Vector2(1, 0);
		case 2:
			return new Vector2(0, 1);
		case 3:
		default:
			return new Vector2(1, 0);
		}
	}
	
	private Vector2 randomOutwardCardinalDirection(Vector2 offset) {
		while(true) {
			Vector2 direction = randomCardinalDirection();
			if(direction.dot(offset) <= 0) {
				return direction;
			}
		}
	}
		
	
}
