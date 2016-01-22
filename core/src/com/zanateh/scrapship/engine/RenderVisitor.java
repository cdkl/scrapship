package com.zanateh.scrapship.engine;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.camera.CameraManager;
import com.zanateh.scrapship.effects.ScrapshipParticleEffect;
import com.zanateh.scrapship.engine.components.BeamComponent;
import com.zanateh.scrapship.engine.components.EnvironmentComponent;
import com.zanateh.scrapship.engine.components.FixtureComponent;
import com.zanateh.scrapship.engine.components.HardpointComponent;
import com.zanateh.scrapship.engine.components.PodComponent;
import com.zanateh.scrapship.engine.components.ParticleEffectComponent;
import com.zanateh.scrapship.engine.components.ThrusterComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Hardpoint;
import com.zanateh.scrapship.engine.components.subcomponents.Thruster;
import com.zanateh.scrapship.engine.components.subcomponents.WeaponMount;
import com.zanateh.scrapship.engine.helpers.ShipHelper;

public class RenderVisitor {

	private ComponentMapper<PodComponent> podMapper = ComponentMapper.getFor(PodComponent.class);
	private ComponentMapper<ThrusterComponent> thrusterMapper = ComponentMapper.getFor(ThrusterComponent.class);
	private ComponentMapper<FixtureComponent> fixtureMapper = ComponentMapper.getFor(FixtureComponent.class);
	private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	private ComponentMapper<BeamComponent> beamMapper = ComponentMapper.getFor(BeamComponent.class);
	private ComponentMapper<ParticleEffectComponent> strikeEffectMapper = ComponentMapper.getFor(ParticleEffectComponent.class);
	private ComponentMapper<EnvironmentComponent> environmentMapper = ComponentMapper.getFor(EnvironmentComponent.class);
	
	
	private Sprite podSprite;
	private Sprite thrusterOnSprite;
	private Sprite thrusterOffSprite;
	private Sprite hardpointSprite;
	private Sprite weaponMountSprite;
	private Sprite laserbeamSprite;
	private CameraManager camManager;
	
	private static final float beamWidthFactor = 0.005f;
	
	public RenderVisitor() {
		podSprite = new Sprite(new Texture(Gdx.files.internal("img/pod.png")));
		podSprite.setSize(1,1);
		podSprite.setOrigin(podSprite.getWidth()/2, podSprite.getHeight()/2);

		thrusterOffSprite = new Sprite(new Texture(Gdx.files.internal("img/thruster.png")));
		thrusterOffSprite.setSize(0.25f, 0.25f);
		thrusterOffSprite.setOrigin(0, 0);
		
		thrusterOnSprite = new Sprite(new Texture(Gdx.files.internal("img/thrusterOn.png")));
		thrusterOnSprite.setSize(0.25f, 0.25f);
		thrusterOnSprite.setOrigin(0, 0);
		
		hardpointSprite = new Sprite(new Texture(Gdx.files.internal("img/hardpointGreen.png")));
		
		weaponMountSprite = new Sprite(new Texture(Gdx.files.internal("img/weaponmount.png")));
		
		laserbeamSprite = new Sprite(new Texture(Gdx.files.internal("img/laserbeam.png")));
	}
	
	
	public void visit(Entity entity, SpriteBatch batch, float delta) {
		
		PodComponent pc = podMapper.get(entity);
		if( pc != null ) {
			renderPod(entity, batch);
		}
		
		
		ThrusterComponent thc = thrusterMapper.get(entity);
		if( thc != null ) {
			renderThrusters(entity, batch, thc);
		}
		
		ImmutableArray<Hardpoint> hps = ShipHelper.getHardpointsForPod(entity);
		if( hps != null ) {
			renderHardpoints(entity, batch, hps);
		}
		
		ImmutableArray<WeaponMount> wms = ShipHelper.getWeaponMountsForPod(entity);
		if(wms != null) {
			renderWeaponMounts(entity, batch, wms);
		}
		
		BeamComponent bc = beamMapper.get(entity);
		if( bc != null ) {
			renderBeams(entity, batch, bc);
		}

		ParticleEffectComponent sec = strikeEffectMapper.get(entity);
		if( sec != null ) {
			renderStrikeEffects(sec.particleEffects, batch, delta);
		}
		
		EnvironmentComponent ec = environmentMapper.get(entity);
		if( ec != null ) {
			renderEnvironment(entity, batch, ec, delta);
		}
		
	}


	private void renderEnvironment(Entity entity, SpriteBatch batch, EnvironmentComponent ec, float delta) {
		float damp = 0.8f; 
		Vector2 camPos = this.camManager.getCameraPos();
		float zoom = this.camManager.getZoom();
//		ec.background.setSize(100*zoom, 100*zoom);
//		ec.background.setPosition(camPos.x * damp - camPos.x * (zoom-1), camPos.y * damp - camPos.y * (zoom-1)); //- (ec.background.getWidth()/(zoom*2)), (camPos.y * damp) - (ec.background.getHeight()/(zoom*2)));
		ec.background.setPosition(camPos.x * damp - ec.background.getWidth()/2, camPos.y * damp - ec.background.getHeight()/2);
		ec.background.draw(batch);
	}


	private void renderStrikeEffects(Array<PooledEffect> strikeEffects, SpriteBatch batch, float delta) {
		for(PooledEffect se : strikeEffects) {
			se.draw(batch, delta);
			if(se.isComplete()) {
				strikeEffects.removeValue(se, true);
				se.free();
			}
		}
	}


	private void renderBeams(Entity entity, SpriteBatch batch, BeamComponent bc) {
		TransformComponent tc = transformMapper.get(entity);
		// draw a line from tc.position in bc.direction for bc.range
		
		if(bc.strike == null) {
			laserbeamSprite.setSize(bc.range, bc.strength * beamWidthFactor);
		}
		else {
			Vector2 diff = new Vector2(bc.strike);
			diff.sub(tc.position);
			laserbeamSprite.setSize(diff.len(), bc.strength * beamWidthFactor);
		}
		laserbeamSprite.setOrigin(0, laserbeamSprite.getHeight()*0.5f);
		
		laserbeamSprite.setPosition(tc.position.x, tc.position.y);
		laserbeamSprite.setCenterY(tc.position.y);
		laserbeamSprite.setRotation(bc.getDirection().angle());
		
		laserbeamSprite.draw(batch);
	}


	private void renderWeaponMounts(Entity entity, SpriteBatch batch, ImmutableArray<WeaponMount> wms) {
		TransformComponent tc = transformMapper.get(entity);
		for( WeaponMount mount : wms) {
			weaponMountSprite.setSize(mount.radius*2, mount.radius*2);
			weaponMountSprite.setOriginCenter();
			
			Vector2 wmp = new Vector2(mount.position);
			tc.transformPositionToGlobal(wmp);
			float wrot = tc.transformRotationToGlobal(mount.direction.angle());
			
			weaponMountSprite.setCenter(wmp.x, wmp.y);
			weaponMountSprite.setRotation(wrot);
			
			weaponMountSprite.draw(batch);

		}
	}


	private void renderHardpoints(Entity entity, SpriteBatch batch, ImmutableArray<Hardpoint> hps) {
		TransformComponent tc = transformMapper.get(entity);
		for( Hardpoint hardpoint : hps ) {
			if(hardpoint.attached == null) {
				hardpointSprite.setSize(hardpoint.hardpointRadius*2, hardpoint.hardpointRadius*2);
				hardpointSprite.setOrigin(hardpointSprite.getWidth()/2, hardpointSprite.getHeight()/2);

				Vector2 hardpointPosition = new Vector2(hardpoint.position);
				tc.transformPositionToGlobal(hardpointPosition);
				float hardpointRotation = tc.transformRotationToGlobal(hardpoint.position.angle());

				hardpointSprite.setPosition(hardpointPosition.x - (hardpointSprite.getWidth()/2),
						hardpointPosition.y - (hardpointSprite.getHeight()/2));
				hardpointSprite.setRotation(hardpointRotation);
				
				hardpointSprite.draw(batch);
			}
		}
	}


	private void renderThrusters(Entity entity, SpriteBatch batch, ThrusterComponent thc) {
		TransformComponent tc = transformMapper.get(entity);
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


	private void renderPod(Entity entity, SpriteBatch batch) {
		TransformComponent tc = transformMapper.get(entity);
		Vector2 spritePos = tc.position;
		podSprite.setPosition(spritePos.x - (podSprite.getWidth()/2),
							  spritePos.y - (podSprite.getHeight()/2));
		
		podSprite.setRotation(tc.rotation);
		podSprite.draw(batch);
	}


	public void setCameraManager(CameraManager camManager) {
		this.camManager = camManager;	
	}
}
