package com.zanateh.scrapship.state;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.zanateh.scrapship.ScrapShipGame;
import com.zanateh.scrapship.camera.CameraManager;
import com.zanateh.scrapship.engine.components.BodyComponent;
import com.zanateh.scrapship.engine.components.ThrusterComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Thruster;
import com.zanateh.scrapship.engine.helpers.ShipHelper;
import com.zanateh.scrapship.engine.systems.PhysicsSystem;
import com.zanateh.scrapship.engine.systems.PlayerControlSystem;
import com.zanateh.scrapship.engine.systems.RenderingSystem;
import com.zanateh.scrapship.engine.systems.ThrusterSystem;
import com.zanateh.scrapship.scene.ScrapShipStage;
import com.zanateh.scrapship.ship.ComponentShip;
import com.zanateh.scrapship.ship.ComponentShipFactory;
import com.zanateh.scrapship.ship.DestroyShipEventListener;
import com.zanateh.scrapship.ship.component.ComponentJoiner;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;

public class AshleyPlayState extends GameState implements IWorldSource, IStageSource {

	private World world;
	private PooledEngine engine;
	AshleyPlayStateInputProcessor stage;
//	ComponentShipFactory shipFactory;
	ComponentJoiner componentJoiner;
	
//	ArrayList<ComponentShip> shipList = new ArrayList<ComponentShip>();
	
	CameraManager cameraManager;
	
	
	@Override
	public void Init(ScrapShipGame game) throws RuntimeException {
		super.Init(game);
		
		world = new World(new Vector2(0,0.0f), false);
		cameraManager = new CameraManager(game, world);
		
		engine = new PooledEngine();

		PlayerControlSystem pcs = new PlayerControlSystem();
		engine.addSystem(pcs);
		engine.addSystem(new ThrusterSystem());
		engine.addSystem(new PhysicsSystem(world));
		engine.addSystem(new RenderingSystem(game.getSpriteBatch(), cameraManager));

		
		stage = new AshleyPlayStateInputProcessor(this, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), game.getSpriteBatch());
		stage.getViewport().setCamera(game.getCamera());
		stage.setCameraManager(cameraManager);
		Gdx.input.setInputProcessor(stage);
		
		stage.setShipControl(pcs.getShipControl());
		
		Entity e = buildPlayerShip();
		
		Entity e2 = buildStaticShip();
		ShipHelper.setShipTransform(e2, new Vector2(3,3), 45f);
		
		e2.getComponent(BodyComponent.class).body.setLinearVelocity(new Vector2(-0.7f, -0.8f));
	}
	
	private Entity buildPlayerShip() {
		Entity shipEntity = ShipHelper.createShipEntity(engine, world);
	
		Entity podEntity = ShipHelper.createPodEntity(engine, world);
		ShipHelper.addPodToShip(podEntity, shipEntity, new Vector2(0,0), 0);
		
		float pow = 4f;
		
		ThrusterComponent thc = podEntity.getComponent(ThrusterComponent.class);
		Thruster thruster = new Thruster();
		thruster.position.set(-0.5f, 0);
		thruster.direction.set(1, 0);
		thruster.strength=pow;
		thc.thrusters.add(thruster);
		
		thruster = new Thruster();
		thruster.position.set(0.5f, 0);
		thruster.direction.set(-1, 0);
		thruster.strength=pow * 0.5f;
		thc.thrusters.add(thruster);

		thruster = new Thruster();
		thruster.position.set(-0.5f, 0);
		thruster.direction.set(0, 1);
		thruster.strength=pow * 0.25f;
		thc.thrusters.add(thruster);

		thruster = new Thruster();
		thruster.position.set(-0.5f, 0);
		thruster.direction.set(0, -1);
		thruster.strength=pow * 0.25f;
		thc.thrusters.add(thruster);

		thruster = new Thruster();
		thruster.position.set(0.5f, 0);
		thruster.direction.set(0, 1);
		thruster.strength=pow * 0.25f;
		thc.thrusters.add(thruster);

		thruster = new Thruster();
		thruster.position.set(0.5f, 0);
		thruster.direction.set(0, -1);
		thruster.strength=pow * 0.25f;
		thc.thrusters.add(thruster);
		
		podEntity = ShipHelper.createPodEntity(engine, world);
		ShipHelper.addPodToShip(podEntity, shipEntity, new Vector2(0,1), 90);
		thc = podEntity.getComponent(ThrusterComponent.class);
		
		thruster = new Thruster();
		thruster.position.set(0.5f, 0);
		thruster.direction.set(0, -1);
		thruster.strength=pow * 0.25f;
		thc.thrusters.add(thruster);
		
		podEntity = ShipHelper.createPodEntity(engine, world);
		ShipHelper.addPodToShip(podEntity, shipEntity, new Vector2(0,-1), -90);
		thc = podEntity.getComponent(ThrusterComponent.class);
		
		thruster = new Thruster();
		thruster.position.set(0.5f, 0);
		thruster.direction.set(0, 1);
		thruster.strength=pow * 0.25f;
		thc.thrusters.add(thruster);

		
		return shipEntity;
	}
	
	private Entity buildStaticShip() {
		Entity shipEntity = ShipHelper.createShipEntity(engine, world);
		
		Entity podEntity = ShipHelper.createPodEntity(engine, world);
		ShipHelper.addPodToShip(podEntity, shipEntity, new Vector2(0,0), 0);
		
		podEntity = ShipHelper.createPodEntity(engine, world);
		ShipHelper.addPodToShip(podEntity, shipEntity, new Vector2(0,1), 90);

		podEntity = ShipHelper.createPodEntity(engine, world);
		ShipHelper.addPodToShip(podEntity, shipEntity, new Vector2(0,-1), -90);
		
		return shipEntity;
	}
	
	
	
	
/*		

		this.shipFactory = new ComponentShipFactory(this, this);
		
		final AshleyPlayState eventPlayState = this;


		stage = new PlayStateInputProcessor(this, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), game.getSpriteBatch());
		stage.getViewport().setCamera(game.getCamera());
		stage.setCameraManager(cameraManager);
		Gdx.input.setInputProcessor(stage);
		
		InputListener listener;
		
		componentJoiner = new ComponentJoiner(this, stage);
		DestroyShipEventListener dseListener = new DestroyShipEventListener() {
			public boolean handleDestroyShip(ComponentShip target) {
				eventPlayState.destroyShip( target );
				return true;
			}
		};
		stage.getRoot().addListener(dseListener);
		
		ComponentShip ship1 = shipFactory.createShip(
				ComponentShipFactory.ShipType.PlayerShip);
		ship1.setPosition(new Vector2(0.5f,3.5f));
		ship1.setVelocity(new Vector2(1,0));
		shipList.add(ship1);
		cameraManager.setCameraMode(CameraManager.CameraMode.Target);
		cameraManager.setTarget(ship1);		
		
		Random rand = new Random();
		rand.setSeed(1);
		
		float distRange = 10;
		
		for( int i = 0; i < 10; ++i ) {
			ComponentShip randomShip = shipFactory.createShip(ComponentShipFactory.ShipType.RandomShip);
			randomShip.setPosition(new Vector2((float)rand.nextGaussian() * distRange, (float) rand.nextGaussian() * distRange ));
			randomShip.setRotation(rand.nextInt(360));
			shipList.add(randomShip);
		}
		
		
//		ComponentShip ship2 = shipFactory.createShip(
//				ComponentShipFactory.ShipType.DebugShip);
//		ship2.setPosition(new Vector2(8,4.4f));
//		ship2.setVelocity(new Vector2(-1,0));
//		shipList.add(ship2);
		
		stage.setShipControl(ship1.getShipControl());
		
		*/
//	}
	
	@Override
	public void Cleanup() {
		// TODO Auto-generated method stub
		world.dispose();
		engine.removeAllEntities();

		Gdx.input.setInputProcessor(null);
		
	}

	@Override
	public void Pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void HandleEvents(ScrapShipGame game) {
		// TODO Auto-generated method stub

	}

	@Override
	public void Update(ScrapShipGame game) {
		stage.act(game.getUpdateFrame());
		engine.update(game.getUpdateFrame());
		stage.postUpdate();
	}

	@Override
	public void Draw(ScrapShipGame game) {
//		Gdx.gl.glClearColor(0, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//		cameraManager.setupRenderCamera();
		
//		stage.draw();
		
//		cameraManager.finalizeRender();
	}


	public void reset() {
		game.changeState(new AshleyPlayState());
	}
	
	@Override
	public World getWorld() {
		return world;
	}
	
	@Override
	public Stage getStage() {
		return stage;
	}
	

}
