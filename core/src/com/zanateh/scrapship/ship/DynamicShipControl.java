package com.zanateh.scrapship.ship;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.zanateh.scrapship.ship.component.ComponentThruster;
import com.zanateh.scrapship.ship.component.ThrustAction;

public class DynamicShipControl implements IShipControl {

	private ArrayList<ComponentThruster> thrusters = new ArrayList<ComponentThruster>();
	private ComponentShip ship;
	
	public DynamicShipControl( ComponentShip ship ) {
		this.ship = ship;
	}
	
	@Override
	public void remove() {
		this.ship = null;
	}
	
	@Override
	public void setForwardThrust(float thrust) {
		_setDirectionalThrustForCompatibleThrusters(thrust, new Vector2(1,0));
	}

	@Override
	public void setReverseThrust(float thrust) {
		_setDirectionalThrustForCompatibleThrusters(thrust, new Vector2(-1,0));	}

	@Override
	public void setLeftThrust(float thrust) {
		_setDirectionalThrustForCompatibleThrusters(thrust, new Vector2(0,1));
		
	}

	@Override
	public void setRightThrust(float thrust) {
		_setDirectionalThrustForCompatibleThrusters(thrust, new Vector2(0,-1));
	}

	@Override
	public void setCCWThrust(float thrust) {
		_setRotationalThrustForCompatibleThrusters(thrust, 1);
		
	}

	@Override
	public void setCWThrust(float thrust) {
		_setRotationalThrustForCompatibleThrusters(thrust, -1);
		
	}
	
	private void _setDirectionalThrustForCompatibleThrusters(float thrust, Vector2 desiredDirection) {		

		if( ship != null ) {
			for( ComponentThruster thruster : ship.getThrusters() ) {
				
				Vector2 thrustVector = thruster.getThrustVector().cpy();
				thrustVector.scl(thruster.getStrength());
				thruster.getComponent().transformVectorToParent(thrustVector);
				
				if( desiredDirection.dot(thrustVector) > 0 ) {
					Gdx.app.log("Thruster", "Firing " + thruster.toString() + " to " + thrust);
					thruster.addAction(new ThrustAction(thrust));
				}	
			}		
		}
	}
	
	private void _setRotationalThrustForCompatibleThrusters(float thrust, float desiredDirection) {
		if( ship != null ) {
			Vector2 centreOfMass = this.ship.getCenter();
			
			for( ComponentThruster thruster : ship.getThrusters() ) {
				Vector2 momentArm = thruster.getPosition().cpy();
				thruster.getComponent().transformPositionToParent(momentArm);
				
				momentArm.sub(centreOfMass);
				
				Vector2 thrustVector = thruster.getThrustVector().cpy();
				thrustVector.scl(thruster.getStrength());
				thruster.getComponent().transformVectorToParent(thrustVector);
				
	//			Vector2 parallelComponent = momentArm.cpy();
	//			parallelComponent.scl(thrustVector.dot(momentArm) / momentArm.dot(momentArm) );
	//			Vector2 angularForce = thrustVector.cpy();
	//			angularForce.sub(parallelComponent);
				
				float torque = momentArm.crs(thrustVector);
				if(torque * desiredDirection > 0) {
					Gdx.app.log("Thruster", "Firing " + thruster.toString() + " to " + thrust);
					thruster.addAction(new ThrustAction(thrust));
				}
				
			}
		}
	}

}
