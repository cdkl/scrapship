package com.zanateh.scrapship.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.camera.CameraManager;
import com.zanateh.scrapship.engine.components.SpriteComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;

public class RenderingSystem extends IteratingSystem {

	private Array<Entity> renderQueue;
	private SpriteBatch batch;
	private CameraManager camManager;
	
	private ComponentMapper<SpriteComponent> spriteMapper;
	private ComponentMapper<TransformComponent> transformMapper;
	
	public RenderingSystem(SpriteBatch batch, CameraManager camManager) {
		super(Family.all(TransformComponent.class, SpriteComponent.class).get());
		renderQueue = new Array<Entity>();
		
		this.batch = batch;
		this.camManager = camManager;
		
		spriteMapper = ComponentMapper.getFor(SpriteComponent.class);
		transformMapper = ComponentMapper.getFor(TransformComponent.class);

	}

	@Override
	public void update(float delta) {
		super.update(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camManager.setupRenderCamera();
		
		batch.enableBlending();
		batch.begin();
		
		for(Entity entity : renderQueue) {
			SpriteComponent spriteComponent = spriteMapper.get(entity);
			TransformComponent transformComponent = transformMapper.get(entity);
			
			if( spriteComponent.sprite == null ) {
				continue;
			}
			
			Vector2 spritePos = transformComponent.position;
			spriteComponent.sprite.setPosition(spritePos.x - (spriteComponent.sprite.getWidth()/2),
											   spritePos.y - (spriteComponent.sprite.getHeight()/2));
			
			spriteComponent.sprite.setRotation(transformComponent.rotation);
			spriteComponent.sprite.draw(batch);
		}
		batch.end();
		
		camManager.finalizeRender();
		
		renderQueue.clear();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		renderQueue.add(entity);
	}
	
}
