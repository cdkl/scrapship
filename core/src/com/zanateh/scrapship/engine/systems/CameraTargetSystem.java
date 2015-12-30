package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.camera.CameraManager;
import com.zanateh.scrapship.engine.components.BodyComponent;
import com.zanateh.scrapship.engine.components.CameraTargetComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;

public class CameraTargetSystem extends IteratingSystem {

	private Array<Entity> cameraQueue = new Array<Entity>();
	private CameraManager manager;
	
	private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	
	public CameraTargetSystem(CameraManager manager) {
		super(Family.all(CameraTargetComponent.class, TransformComponent.class).get());
		this.manager = manager;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		Entity cameraTarget = cameraQueue.first();
		if( cameraTarget != null ) {
			TransformComponent tc = transformMapper.get(cameraTarget);
			manager.setCameraPos(new Vector3(tc.position.x, tc.position.y, 0));
		}
		
		cameraQueue.clear();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		cameraQueue.add(entity);
	}

}
