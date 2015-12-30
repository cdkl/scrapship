package com.zanateh.scrapship.engine.helpers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zanateh.scrapship.engine.components.PickableComponent;
import com.zanateh.scrapship.engine.components.SelectedComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;

public class PickHelper {
	private static Family pickableFamily = Family.all(PickableComponent.class, TransformComponent.class).get();
	private static Family selectedFamily = Family.all(SelectedComponent.class).get();
	private static ComponentMapper<PickableComponent> pickableMapper = ComponentMapper.getFor(PickableComponent.class);
	private static ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	private static ComponentMapper<SelectedComponent> selectedMapper = ComponentMapper.getFor(SelectedComponent.class);
	
	public static ImmutableArray<Entity> pick(Engine engine, Viewport viewport, Vector2 pickLocation)
	{
		Vector2 worldCoords = viewport.unproject(pickLocation);
		
		Array<Entity> returnArray = new Array<Entity>();
		for(Entity entity : engine.getEntitiesFor(pickableFamily)) {
			PickableComponent pc = pickableMapper.get(entity);
			TransformComponent tc = transformMapper.get(entity);
			
			Vector2 diffVec = new Vector2(pickLocation).sub(tc.position);
			if( diffVec.len2() <= pc.radius*pc.radius ) {
				returnArray.add(entity);
			}
		}
		
		return new ImmutableArray<Entity>(returnArray);
	}
	
	public static void setSelected(Entity entity) {
		if(selectedMapper.get(entity) != null ) {
			throw new RuntimeException("Entity " + entity.toString() + " cannot be selected: already selected!");
		}
		entity.add(new SelectedComponent());
	}
	
	public static void setUnselected(Entity entity) {
		if( selectedMapper.get(entity) == null ) {
			throw new RuntimeException("Entity " + entity.toString() + " cannot be unselected: is not selected!");
		}
		entity.remove(SelectedComponent.class);
	}
	
	public static Entity getSelected(Engine engine) {
		ImmutableArray<Entity> entities = engine.getEntitiesFor(selectedFamily);
		if( entities.size() > 0 ) {
			return entities.first();
		}
		else {
			return null;
		}
	}
	
}
