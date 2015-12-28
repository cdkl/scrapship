package com.zanateh.scrapship.engine;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.zanateh.scrapship.engine.components.BodyComponent;
import com.zanateh.scrapship.engine.components.FixtureComponent;
import com.zanateh.scrapship.engine.components.PodComponent;
import com.zanateh.scrapship.engine.components.ShipComponent;
import com.zanateh.scrapship.engine.components.ThrusterComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Thruster;

public class ShipRenderVisitor {

	private ComponentMapper<PodComponent> podMapper = ComponentMapper.getFor(PodComponent.class);
	private ComponentMapper<ThrusterComponent> thrusterMapper = ComponentMapper.getFor(ThrusterComponent.class);
	private ComponentMapper<FixtureComponent> fixtureMapper = ComponentMapper.getFor(FixtureComponent.class);
	private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);


	private Sprite podSprite;
	private Sprite thrusterOnSprite;
	private Sprite thrusterOffSprite;
	
	public ShipRenderVisitor() {
		podSprite = new Sprite(new Texture(Gdx.files.internal("data/pod.png")));
		podSprite.setSize(1,1);
		podSprite.setOrigin(podSprite.getWidth()/2, podSprite.getHeight()/2);

		thrusterOffSprite = new Sprite(new Texture(Gdx.files.internal("data/thruster.png")));
		thrusterOffSprite.setSize(0.25f, 0.25f);
		thrusterOffSprite.setOrigin(0, 0);
		
		thrusterOnSprite = new Sprite(new Texture(Gdx.files.internal("data/thrusterOn.png")));
		thrusterOnSprite.setSize(0.25f, 0.25f);
		thrusterOnSprite.setOrigin(0, 0);		
	}
	
	
	public void visit(Entity entity, SpriteBatch batch) {
		
		TransformComponent tc = transformMapper.get(entity);
		PodComponent pc = podMapper.get(entity);
		
		if( pc != null ) {
			Vector2 spritePos = tc.position;
			podSprite.setPosition(spritePos.x - (podSprite.getWidth()/2),
								  spritePos.y - (podSprite.getHeight()/2));
			
			podSprite.setRotation(tc.rotation);
			podSprite.draw(batch);
		}
		
		
		ThrusterComponent thc = thrusterMapper.get(entity);
		if( thc != null ) {
			for( Thruster thruster : thc.thrusters ) {
				Sprite thrusterSprite = thruster.power > 0f ? thrusterOnSprite : thrusterOffSprite;

				Vector2 spriteRenderOffset = new Vector2(thrusterSprite.getWidth(), thrusterSprite.getHeight()/2);
				Vector2 spriteVec = new Vector2(thruster.direction);
				spriteRenderOffset.rotate(spriteVec.angle() + 180);
				spriteRenderOffset.add(thruster.position);
				spriteRenderOffset.rotate(tc.rotation);
				
				Vector2 spritePos = new Vector2(tc.position);
				spritePos.add(spriteRenderOffset);
				thrusterSprite.setPosition(spritePos.x, spritePos.y);
				
				spriteVec.rotate(tc.rotation);
				
				float rotation = spriteVec.angle();
				thrusterSprite.setRotation(rotation);
				thrusterSprite.draw(batch);
			}
		}
		

	}
}
