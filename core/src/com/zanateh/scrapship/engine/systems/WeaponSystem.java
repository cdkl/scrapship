package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.BeamComponent;
import com.zanateh.scrapship.engine.components.OneTickComponent;
import com.zanateh.scrapship.engine.components.RenderComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.components.WeaponMountComponent;
import com.zanateh.scrapship.engine.components.subcomponents.LaserWeapon;
import com.zanateh.scrapship.engine.components.subcomponents.Weapon;
import com.zanateh.scrapship.engine.components.subcomponents.WeaponMount;
import com.zanateh.scrapship.engine.helpers.ShipHelper;

public class WeaponSystem extends IteratingSystem {

	Engine engine;
	
	Array<Entity> weaponQueue = new Array<Entity>();
	private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	
	public WeaponSystem(Engine engine) {
		super(Family.all(WeaponMountComponent.class, TransformComponent.class).get());
		this.engine = engine;
	}
	
	@Override 
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		for(Entity podEntity : weaponQueue) {
			ImmutableArray<WeaponMount> wms = ShipHelper.getWeaponMountsForPod(podEntity);
			TransformComponent tc = transformMapper.get(podEntity);
			
			for(WeaponMount mount : wms) {
				Weapon weapon = mount.getWeapon();
				
				if( weapon instanceof LaserWeapon ) {
					LaserWeapon laserWeapon = (LaserWeapon) weapon;
					if(laserWeapon.firing) {
						// Create a beam
						Vector2 beamStartPos = new Vector2(mount.barrelTip);
						beamStartPos.rotate(mount.direction.angle());
						beamStartPos.add(mount.position);
						Vector2 beamDirection = new Vector2(mount.direction);

						tc.transformPositionToGlobal(beamStartPos);
						beamDirection.rotate(tc.rotation);

						createBeam(beamStartPos, beamDirection, laserWeapon.range, laserWeapon.strength);
						
						laserWeapon.firing = false;
						
					}
				}
			}
		}
		
		weaponQueue.clear();
	}
	
	private void createBeam(Vector2 beamStartPos, Vector2 beamDirection, float range, float strength) {
		Entity beamEntity = new Entity();
		beamEntity.add(new OneTickComponent());
		beamEntity.add(new RenderComponent());
		
		TransformComponent tc = new TransformComponent();
		tc.position.set(beamStartPos);
		beamEntity.add(tc);
		
		BeamComponent bc = new BeamComponent();
		bc.direction = beamDirection;
		bc.range = range;
		bc.strength = strength;
		beamEntity.add(bc);
		
		engine.addEntity(beamEntity);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		weaponQueue.add(entity);
	}

}
