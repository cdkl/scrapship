package com.zanateh.scrapship.engine.components.subcomponents;

public class LaserWeapon implements Weapon {
	public float range = 50f;
	public float strength = 1f;

	public boolean firing = false;
	
	@Override
	public void fire() {
		firing = true;
	}
	
	
}
