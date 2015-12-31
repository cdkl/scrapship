package com.zanateh.scrapship.engine;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.BodyComponent;
import com.zanateh.scrapship.engine.components.FixtureComponent;
import com.zanateh.scrapship.engine.components.ShipComponent;
import com.zanateh.scrapship.engine.components.ThrusterComponent;
import com.zanateh.scrapship.engine.components.WeaponMountComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Thruster;
import com.zanateh.scrapship.engine.components.subcomponents.Weapon;
import com.zanateh.scrapship.engine.components.subcomponents.WeaponMount;

public class ShipControlVisitor implements IShipControl {

	private ComponentMapper<ShipComponent> shipMapper = ComponentMapper.getFor(ShipComponent.class);
	private ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
	private ComponentMapper<ThrusterComponent> thrusterMapper = ComponentMapper.getFor(ThrusterComponent.class);
	private ComponentMapper<FixtureComponent> fixtureMapper = ComponentMapper.getFor(FixtureComponent.class);
	private ComponentMapper<WeaponMountComponent> weaponMountMapper = ComponentMapper.getFor(WeaponMountComponent.class);
	
	private float forwardThrust = 0.0f;
	private float reverseThrust = 0.0f;
	private float leftThrust = 0.0f;
	private float rightThrust = 0.0f;
	private float ccwThrust = 0.0f;
	private float cwThrust = 0.0f;
	
	boolean fireState = false;
	
	public void visit(Entity entity) {
//		Gdx.app.log("ShipControlVisitor", String.format("F: %4.2f R: %4.2f L: %4.2f R: %4.2f CCW: %4.2f CW: %4.2f", 
//				forwardThrust,
//				reverseThrust,
//				leftThrust,
//				rightThrust,
//				ccwThrust,
//				cwThrust ));
		
		BodyComponent bc = bodyMapper.get(entity);
		Body body = bc.body;
		
		Vector2 forward = new Vector2(1,0);
		Vector2 reverse = new Vector2(-1,0);
		Vector2 left = new Vector2(0,1);
		Vector2 right = new Vector2(0,-1);
		float ccw = 1;
		float cw = -1;
		
		
		ShipComponent shipComponent = shipMapper.get(entity);

		for(Entity podEntity : shipComponent.pods)
		{
			ThrusterComponent thrusterComponent = thrusterMapper.get(podEntity);
			FixtureComponent fc = fixtureMapper.get(podEntity);
			


			for( Thruster thruster : thrusterComponent.thrusters ) {
				thruster.power = 0;				
				
				if( compatibleDirection(fc, thruster, forward) ) {
					thruster.power = Math.max( thruster.power, forwardThrust );
				}
				if( compatibleDirection(fc, thruster, reverse) ) {
					thruster.power = Math.max( thruster.power, reverseThrust );
				}
				if( compatibleDirection(fc, thruster, left) ) {
					thruster.power = Math.max( thruster.power, leftThrust );
				}
				if( compatibleDirection(fc, thruster, right) ) {
					thruster.power = Math.max( thruster.power, rightThrust );
				}
				
				if( compatibleRotation(fc, thruster, ccw)) {
					thruster.power = Math.max( thruster.power, ccwThrust );
				}
				if( compatibleRotation(fc, thruster, cw)) {
					thruster.power = Math.max( thruster.power, cwThrust );
				}

				
			}
			
			WeaponMountComponent wmc = weaponMountMapper.get(podEntity);
			if(wmc != null) {
				if(fireState) {
					for(WeaponMount mount : wmc.weaponMounts) {
						mount.getWeapon().fire();
					}
				}
			}
			
		}
		
	}

	private boolean compatibleRotation(FixtureComponent fc, Thruster thruster, float rotation) {
		Vector2 momentArm = new Vector2(thruster.position);
		momentArm.rotate(fc.localRotation);
		momentArm.add(fc.localPosition);
		
		momentArm.sub(fc.fixture.getBody().getLocalCenter());
		
		Vector2 thrusterVec = new Vector2(thruster.direction);
		thrusterVec.rotate(fc.localRotation);
		
		float torque = momentArm.crs(thrusterVec);
		if(torque * rotation > 0.001f ) {
			return true;
		}
		
		return false;
	}

	private boolean compatibleDirection(FixtureComponent fc, Thruster thruster, Vector2 direction) {
//		Vector2 thrusterPos = new Vector2(thruster.position);
//		thrusterPos.rotate(fc.localRotation);
//		thrusterPos.add(fc.localPosition);
		Vector2 thrusterVec = new Vector2(thruster.direction);
		thrusterVec.rotate(fc.localRotation);				

		if( direction.dot(thrusterVec) > 0.001f ) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void setForwardThrust(float thrust) {
		forwardThrust = thrust;
	}

	@Override
	public void setReverseThrust(float thrust) {
		reverseThrust = thrust;
	}

	@Override
	public void setLeftThrust(float thrust) {
		leftThrust = thrust;
	}

	@Override
	public void setRightThrust(float thrust) {
		rightThrust = thrust;
	}

	@Override
	public void setCCWThrust(float thrust) {
		ccwThrust = thrust;
	}

	@Override
	public void setCWThrust(float thrust) {
		cwThrust = thrust;
	}

	@Override
	public void fire(boolean on) {
		fireState = on;
	}
	
	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
