package com.zanateh.scrapship.engine.helpers;

import java.util.HashSet;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.BodyComponent;
import com.zanateh.scrapship.engine.components.FixtureComponent;
import com.zanateh.scrapship.engine.components.HardpointComponent;
import com.zanateh.scrapship.engine.components.PlayerCommandPodComponent;
import com.zanateh.scrapship.engine.components.ShipComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Hardpoint;

public class ShipDivider {

	private static ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
	private static ComponentMapper<ShipComponent> shipMapper = ComponentMapper.getFor(ShipComponent.class);
	private static ComponentMapper<HardpointComponent> hardpointMapper = ComponentMapper.getFor(HardpointComponent.class);
	private static ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	private static ComponentMapper<PlayerCommandPodComponent> playerCommandPodMapper = ComponentMapper.getFor(PlayerCommandPodComponent.class);
	private static ComponentMapper<FixtureComponent> fixtureMapper = ComponentMapper.getFor(FixtureComponent.class);

	
	public static Array<Entity> divideShipIfBroken(Engine engine, Entity ship) {
		Array<Entity> returnShips = new Array<Entity>();
		returnShips.add(ship);
		
		ShipComponent sc = shipMapper.get(ship);
		if(sc == null ) {
			throw new RuntimeException("Cannot check ship division for non-ship Entity");
		}
		Entity startPod = null;
		for( Entity podEntity : sc.pods ) {
			if( startPod == null ) {
				// Make sure we have a starting point.
				startPod = podEntity;
			}
			if(playerCommandPodMapper.get(podEntity) != null) {
				// Found a command pod, we want to start here.
				startPod = podEntity;
				break;
			}
		}
		
		if(startPod == null) { 
			return returnShips;
		}
		
		// Starting from the start pod, walk through existing pods as connected by hardpoint. Map the ones we find.
		HashSet<Entity> foundPods = new HashSet<Entity>();
		findConnectedPods(startPod, foundPods);
		
		if(foundPods.size() < sc.pods.size) {
			// Time to find the pods that are now disconnected.
			Array<Entity> disconnectedPods = new Array<Entity>();
			for(Entity podEntity : sc.pods) {
				if(! foundPods.contains(podEntity)) {
					disconnectedPods.add(podEntity);
				}
			}
			if( disconnectedPods.size == 0) {
				throw new RuntimeException("Ship splitting broke: no disconnected pods found even though connected pods size less than total pods!");
			}
			// Create a ship!
			BodyComponent bc = bodyMapper.get(ship);
			Entity newShipEntity = ShipHelper.createShipEntity(engine, bc.body.getWorld());

			FixtureComponent firstPodFixture = fixtureMapper.get(disconnectedPods.first());
			TransformComponent firstPodTC = transformMapper.get(disconnectedPods.first());
			
			// The new ship will be based around the first pod, so all pods being included need to be transformed from their Fixture's old localPos/Rot
			// to the new one.
			Vector2 firstPodOrigPos = new Vector2(firstPodFixture.localPosition);
			float firstPodOrigRot = firstPodFixture.localRotation;

			Vector2 firstPodVelocity = bc.body.getLinearVelocityFromLocalPoint(firstPodOrigPos);
			
			ShipHelper.setShipTransform(newShipEntity, firstPodTC.position, firstPodTC.rotation - firstPodOrigRot );
			BodyComponent nsebc = bodyMapper.get(newShipEntity);
			nsebc.body.setLinearVelocity(firstPodVelocity);
			
			for(Entity podEntity : disconnectedPods) {
				FixtureComponent podFC = fixtureMapper.get(podEntity);
				
				ShipHelper.depopulatePodFromShip(podEntity);
				ShipHelper.addPodToShip(podEntity, newShipEntity, new Vector2(podFC.localPosition).sub(firstPodOrigPos), podFC.localRotation);
			}
			
			// Before we move on, let's make sure that THIS ship can't also be split again:
			Array<Entity> splitShips = ShipDivider.divideShipIfBroken(engine, newShipEntity);
			returnShips.addAll(splitShips);
		}
		
		return returnShips;
	}

	private static void findConnectedPods(Entity podEntity, HashSet<Entity> foundPods )
	{
		foundPods.add(podEntity);
		HardpointComponent hc = hardpointMapper.get(podEntity);
		if( hc != null ) {
			for(Hardpoint hp : hc.hardpoints) {
				if(hp.attached != null) {
					if(hp.attached.entity != null && ! foundPods.contains(hp.attached.entity)) {
						findConnectedPods(hp.attached.entity, foundPods);
					}
				}
			}
		}
	}
	
}
