package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zanateh.scrapship.engine.components.FixtureComponent;
import com.zanateh.scrapship.engine.components.PodComponent;
import com.zanateh.scrapship.engine.components.SelectedComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.helpers.ShipHelper;

public class DragAndDropSystem extends IteratingSystem {

	private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	private ComponentMapper<SelectedComponent> selectedMapper = ComponentMapper.getFor(SelectedComponent.class);
	private ComponentMapper<PodComponent> podMapper = ComponentMapper.getFor(PodComponent.class);
	private ComponentMapper<FixtureComponent> fixtureMapper = ComponentMapper.getFor(FixtureComponent.class);
	
	
	private Array<Entity> knownSelected = new Array<Entity>();
	private Array<Entity> selectedQueue = new Array<Entity>();
	
	private Vector2 selectedPosition = new Vector2();
	private Engine engine;
	private World world;
	private Viewport viewport;
	
	public DragAndDropSystem(Engine engine, World world) {
		super(Family.all(SelectedComponent.class, TransformComponent.class).get());
		this.engine = engine;
		this.world = world;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
	
	public void setSelectedPosition(Vector2 position) {
		this.selectedPosition = position;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		for(Entity entity : selectedQueue) {
			// Already in knownSelected?
			if(! knownSelected.contains(entity, false)) {
				// Need to perform initial selection action.
				knownSelected.add(entity);
				PodComponent pc = podMapper.get(entity);
				if( pc != null ) {
					Entity shipEntity = pc.ship;
					if( shipEntity != null ) { 
						ShipHelper.removePodFromShip(entity);
						ShipHelper.destroyIfNoComponentsForShip(shipEntity, engine, world);
					}
					entity.remove(FixtureComponent.class);
					
				}
			}
		}
		
		Array<Entity> disappearedEntities = new Array<Entity>();
		for(Entity entity : knownSelected) {
			if(selectedQueue.contains(entity, true)) {
				TransformComponent tc = transformMapper.get(entity);
				if( tc != null ) {
					tc.position.set(selectedPosition);
					viewport.unproject(tc.position);
				}
			}
			else {
				// Something has been removed!
				disappearedEntities.add(entity);
			}
		}
		
		for(Entity entity : disappearedEntities ) {
			knownSelected.removeValue(entity, true);

			PodComponent pc = podMapper.get(entity);
			if( pc != null ) {
				TransformComponent tc = transformMapper.get(entity);
				entity.add(new FixtureComponent());
				// If we're dropping a PodComponent, then we'll create a ship in the new location.
				Entity shipEntity = ShipHelper.createShipEntity(engine, world);
				Vector2 dropPosition = new Vector2(this.selectedPosition);
				viewport.unproject(dropPosition);
				ShipHelper.setShipTransform(shipEntity, dropPosition, tc.rotation);
				ShipHelper.addPodToShip(entity, shipEntity, new Vector2(0,0), 0);
			}

		}
		
		selectedQueue.clear();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		selectedQueue.add(entity);
	}

}
