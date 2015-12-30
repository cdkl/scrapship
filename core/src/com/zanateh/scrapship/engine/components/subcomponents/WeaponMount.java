package com.zanateh.scrapship.engine.components.subcomponents;

import com.badlogic.gdx.math.Vector2;

public class WeaponMount {
	public Vector2 position = new Vector2();
	public Vector2 direction = new Vector2(1,0);
	public float radius = 0.2f;
	public Vector2 barrelTip = new Vector2(0.2f, 0);
	private Weapon weapon = null;
	
	public WeaponMount() {
	}
	
	public WeaponMount(Vector2 position, Vector2 direction) {
		this.position.set(position);
		if(direction.len2() == 0) {
			throw new RuntimeException("WeaponMount direction needs a direction.");
		}
		this.direction.set(direction);
	}
	
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}
	
	public Weapon getWeapon() {
		return this.weapon;
	}
}
