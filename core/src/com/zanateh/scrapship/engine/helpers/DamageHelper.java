package com.zanateh.scrapship.engine.helpers;

import java.util.Hashtable;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.effects.ScrapshipParticleEffect;
import com.zanateh.scrapship.engine.components.BeamComponent;
import com.zanateh.scrapship.engine.components.BodyComponent;
import com.zanateh.scrapship.engine.components.HardpointComponent;
import com.zanateh.scrapship.engine.components.HitpointComponent;
import com.zanateh.scrapship.engine.components.PodComponent;
import com.zanateh.scrapship.engine.components.ShipComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Hardpoint;

public class DamageHelper {
	private static ComponentMapper<BeamComponent> beamMapper = ComponentMapper.getFor(BeamComponent.class);
	private static ComponentMapper<HitpointComponent> hitpointMapper = ComponentMapper.getFor(HitpointComponent.class);
	private static ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	private static ComponentMapper<PodComponent> podMapper = ComponentMapper.getFor(PodComponent.class);
	private static ComponentMapper<HardpointComponent> hardpointMapper = ComponentMapper.getFor(HardpointComponent.class);
	private static ComponentMapper<ShipComponent> shipMapper = ComponentMapper.getFor(ShipComponent.class);
	private static ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
	
	public static void applyDamage(Engine engine, Entity damagedEntity, Entity ordnanceEntity, Vector2 strikePosition, float damageTime ) {
		BeamComponent bc = beamMapper.get(ordnanceEntity);
		if( bc != null ) {
			HitpointComponent hc = hitpointMapper.get(damagedEntity);
			if( hc != null ) {
				float damage = bc.strength * damageTime;
				if(hc.current - damage <= 0 ) {
					hc.current = 0;
					// destroy it!
					// Create explosion
					TransformComponent dtc = transformMapper.get(damagedEntity);
					Vector2 destroyedPodPosition = dtc.position;
					ScrapshipParticleEffect.createExplosionEffect(engine, destroyedPodPosition);
					
					HardpointComponent hpc = hardpointMapper.get(damagedEntity);

					Array<Entity> connectedEntities = new Array<Entity>();
					
					if( hpc != null ) {
						// For each attached hardpoint, cache the component.
						for(Hardpoint hp : hpc.hardpoints) {
							if( hp.attached != null) {
								connectedEntities.add(hp.attached.entity);
							}
						}
					}
					
					// destroy the pod and deal with the consequences.
					PodComponent pc = podMapper.get(damagedEntity);
					if(pc != null) {
						Array<Entity> shipEntities = ShipHelper.removePodFromShip(engine, damagedEntity);
						engine.removeEntity(damagedEntity);
						
						// Apply explosion impetus
						for(Entity excon : connectedEntities) {
							BodyComponent body = bodyMapper.get(podMapper.get(excon).ship);
							TransformComponent etc = transformMapper.get(excon);
							Vector2 forceDir = new Vector2(etc.position).sub(destroyedPodPosition).nor().scl(0.25f);
							
							body.body.applyLinearImpulse(forceDir, etc.position, true);
						}
					}					
				}
				else {
					hc.current -= damage;
				}
			}
		}
	}
}
