package com.zanateh.scrapship.state;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.zanateh.scrapship.engine.components.CameraTargetComponent;
import com.zanateh.scrapship.engine.components.EnvironmentComponent;
import com.zanateh.scrapship.engine.components.HardpointComponent;
import com.zanateh.scrapship.engine.components.PlayerControlComponent;
import com.zanateh.scrapship.engine.components.RenderComponent;
import com.zanateh.scrapship.engine.components.ThrusterComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Hardpoint;
import com.zanateh.scrapship.engine.components.subcomponents.Thruster;
import com.zanateh.scrapship.engine.entity.EntityRegistry;
import com.zanateh.scrapship.engine.helpers.HardpointHelper;
import com.zanateh.scrapship.engine.helpers.ShipFactory;
import com.zanateh.scrapship.engine.helpers.ShipHelper;
import com.zanateh.scrapship.engine.helpers.ShipFactory.ShipType;
import com.zanateh.scrapship.engine.systems.AISystem;
import com.zanateh.scrapship.engine.systems.CameraTargetSystem;
import com.zanateh.scrapship.engine.systems.CleanupSystem;
import com.zanateh.scrapship.engine.systems.DragAndDropSystem;
import com.zanateh.scrapship.engine.systems.MessageDispatchSystem;
import com.zanateh.scrapship.engine.systems.OrdnanceSystem;
import com.zanateh.scrapship.engine.systems.PhysicsSystem;
import com.zanateh.scrapship.engine.systems.PlayerControlSystem;
import com.zanateh.scrapship.engine.systems.RenderingSystem;
import com.zanateh.scrapship.engine.systems.ThrusterSystem;
import com.zanateh.scrapship.engine.systems.WeaponSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;

public class AshleyPlayState extends GameState implements IWorldSource, IStageSource {

	private World world;
	private PooledEngine engine;
	AshleyPlayStateInputProcessor stage;
	EntityRegistry entityRegistry;
	
//	ArrayList<ComponentShip> shipList = new ArrayList<ComponentShip>();
	
	CameraManager cameraManager;
	ShipFactory shipFactory;

	
	@Override
	public void Init(ScrapShipGame game) throws RuntimeException {
		super.Init(game);
		
		world = new World(new Vector2(0,0.0f), false);
		cameraManager = new CameraManager(game, world);
		
		engine = new PooledEngine();

		engine.addEntityListener(EntityRegistry.instance());
		
		MessageDispatchSystem mds = new MessageDispatchSystem();
		engine.addSystem(mds);
		AISystem ais = new AISystem();
		
		engine.addSystem(ais);
		mds.addHandler(ais);
		
		engine.addSystem(new PlayerControlSystem());
		engine.addSystem(new ThrusterSystem());
		engine.addSystem(new PhysicsSystem(world));
		DragAndDropSystem dads = new DragAndDropSystem(engine, world);
		engine.addSystem(dads);
		engine.addSystem(new WeaponSystem(engine));
		engine.addSystem(new OrdnanceSystem(engine));
		engine.addSystem(new CameraTargetSystem(cameraManager));
		engine.addSystem(new RenderingSystem(game.getSpriteBatch(), cameraManager));
		engine.addSystem(new CleanupSystem(engine));
		

		
		stage = new AshleyPlayStateInputProcessor(this, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), game.getSpriteBatch(),
				engine, cameraManager, dads);
		dads.setViewport(stage.getViewport());
		stage.getViewport().setCamera(game.getCamera());
		Gdx.input.setInputProcessor(stage);
		
		shipFactory = new ShipFactory(engine, world);
		
		Entity environmentEntity = new Entity();
		EnvironmentComponent ec = new EnvironmentComponent();
		ec.background = new Sprite(new Texture(Gdx.files.internal("img/ion_pillar___fractal_resource_by_packeranatic-d3lgcci.png")));
		//ec.background = new Sprite(new Texture(Gdx.files.internal("img/libgdx.png")));
		ec.background.setOrigin(ec.background.getWidth()/2, ec.background.getHeight()/2);
		//ec.background.setScale(0.999f);
		ec.background.setSize(100,100);
		ec.background.setPosition(-ec.background.getWidth()/2, -ec.background.getHeight()/2);
		environmentEntity.add(ec);
		environmentEntity.add(new RenderComponent());
		engine.addEntity(environmentEntity);
		
		Entity e = shipFactory.createShip(ShipType.PlayerShip);
		PlayerControlComponent pcc = e.getComponent(PlayerControlComponent.class);
		stage.setShipControl(pcc.shipControl);

		Random rand = new Random();
		rand.setSeed(1);
		
		float distRange = 10;
		
		for( int i = 0; i < 10; ++i ) {
			Entity randomShip = shipFactory.createShip(ShipType.ManeuvreShip);
			ShipHelper.setShipTransform(randomShip, 
					new Vector2((float)rand.nextGaussian() * distRange, (float) rand.nextGaussian() * distRange ), 
					rand.nextInt(360));
			Entity randomComponent = shipFactory.createShip(ShipType.RandomShip);
			ShipHelper.setShipTransform(randomComponent, 
					new Vector2((float)rand.nextGaussian() * distRange, (float) rand.nextGaussian() * distRange ), 
					rand.nextInt(360));
			
		}
	}
		
	
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
