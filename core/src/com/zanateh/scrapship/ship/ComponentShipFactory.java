package com.zanateh.scrapship.ship;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.zanateh.scrapship.scene.ScrapShipStage;
import com.zanateh.scrapship.ship.component.ComponentThruster;
import com.zanateh.scrapship.ship.component.PodComponent;
import com.zanateh.scrapship.state.IStageSource;
import com.zanateh.scrapship.state.IWorldSource;

public class ComponentShipFactory {
	
	IWorldSource worldSource;
	IStageSource stageSource;
	
	Random rand = new Random();
	
	public ComponentShipFactory( IWorldSource worldSource, IStageSource stageSource ) {
		this.worldSource = worldSource;
		this.stageSource = stageSource;
		
		rand.setSeed(1234567);
	}
	
	public enum ShipType {
		RandomShip,
		DebugShip,
		PlayerShip,
		EmptyShip
	};
	
	public ComponentShip createShip(ShipType shipType) 
	{
		ComponentShip ship = null;
		
		switch(shipType) {
		
		case RandomShip:
			{
				Gdx.app.log("ShipFactory", "Creating random ship");
				ship = new ComponentShip(worldSource.getWorld(), stageSource.getStage());
				PodComponent comp1 = generateRandomPodComponent();
				ship.attachComponent(comp1, 0, 0, 0);
			}
			break;
		
		case DebugShip:
			{
				ship = new ComponentShip(worldSource.getWorld(), stageSource.getStage());
			
				PodComponent comp1 = new PodComponent();
				comp1.addHardpoint(new Vector2(0.5f,0));
				comp1.addHardpoint(new Vector2(0,0.5f));
				ship.attachComponent(comp1, 0, 0, 0);
				PodComponent comp2 = new PodComponent();
				comp2.addHardpoint(new Vector2(0.5f,0));
				comp1.getHardpoint(0).attach(comp2.getHardpoint(0));
				PodComponent comp3 = new PodComponent();
				comp3.addHardpoint(new Vector2(0.5f,0));
				comp3.addHardpoint(new Vector2(-0.5f,0));
				comp1.getHardpoint(1).attach(comp3.getHardpoint(1));
				PodComponent comp4 = new PodComponent();
				comp4.addHardpoint(new Vector2(0.5f,0));
				comp3.getHardpoint(0).attach(comp4.getHardpoint(0));
			}
			break;
		case PlayerShip:
			{
				ship = new ComponentShip(worldSource.getWorld(), stageSource.getStage());
				
				PodComponent comp1 = new PodComponent();
				ship.attachComponent(comp1, 0, 0, 0);
				
				
				comp1.addHardpoint(new Vector2(0,0.5f));
				comp1.addHardpoint(new Vector2(0.5f, 0));
				comp1.addHardpoint(new Vector2(0,-0.5f));
				comp1.addHardpoint(new Vector2(-0.5f, 0));
				
				float enginePower = 2;
				ComponentThruster mainEngine = comp1.addThruster(new Vector2(-0.5f,0), new Vector2(1,0), enginePower * 1f );
				ComponentThruster revEngine = comp1.addThruster(new Vector2(0.5f,0), new Vector2(-1,0), enginePower * 0.4f);
				ComponentThruster leftEngineFront = comp1.addThruster(new Vector2(0.5f,0), new Vector2(0,1), enginePower * 0.1f);
				ComponentThruster rightEngineFront = comp1.addThruster(new Vector2(0.5f,0), new Vector2(0,-1), enginePower * 0.1f);
				ComponentThruster leftEngineRear = comp1.addThruster(new Vector2(-0.5f,0), new Vector2(0,1), enginePower * 0.1f);
				ComponentThruster rightEngineRear = comp1.addThruster(new Vector2(-0.5f,0), new Vector2(0,-1), enginePower * 0.1f);

				DynamicShipControl shipControl = new DynamicShipControl(ship);
				
				ship.setShipControl(shipControl);
			
			}
			break;
			
		case EmptyShip:
			{
				ship = new ComponentShip(worldSource.getWorld(), stageSource.getStage());
			}
			break;
		}
		
		return ship;
	}
	
	private PodComponent generateRandomPodComponent() {
		String logString = "--Hardpoints: ";
		float chanceOfHardpoint = .666f;
		PodComponent component = new PodComponent();
		if( rand.nextFloat() <= chanceOfHardpoint ) {
			component.addHardpoint(new Vector2(0, 0.5f));
			logString += " +x ";
		}
		if( rand.nextFloat() <= chanceOfHardpoint ) {
			component.addHardpoint(new Vector2(0.5f, 0));
			logString += " +y ";
		}
		if( rand.nextFloat() <= chanceOfHardpoint ) {
			component.addHardpoint(new Vector2(0, -0.5f));
			logString += " -x ";
		}
		if( rand.nextFloat() <= chanceOfHardpoint ) {
			component.addHardpoint(new Vector2(-0.5f, 0));
			logString += " -y ";
		}
		Gdx.app.log("ShipFactory", logString);
		
		logString = "--Thrusters:\n";
		
		int numThrusters = (int) rand.nextGaussian() + 1;
		for( int i = 0 ; i < numThrusters ; ++i ) {
			Vector2 thrusterPosition = randomCardinalDirection().scl(0.5f);
			ComponentThruster thruster = component.addThruster(thrusterPosition, randomOutwardCardinalDirection(thrusterPosition), Math.max(1.0f, (float) rand.nextGaussian() * 2 + 2));
			logString += "----Thruster: " + thrusterPosition;
			logString += " -> " + thruster.toString() + "\n";
		}
		
		Gdx.app.log("ShipFactory", logString);
		
		return component;
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
