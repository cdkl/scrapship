package com.zanateh.scrapship.state;

import java.util.ArrayList;

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
import com.zanateh.scrapship.scene.ScrapShipStage;
import com.zanateh.scrapship.ship.ComponentShip;
import com.zanateh.scrapship.ship.ComponentShipFactory;
import com.zanateh.scrapship.ship.DestroyShipEventListener;
import com.zanateh.scrapship.ship.ShipControl;
import com.zanateh.scrapship.ship.component.ComponentJoiner;

public class PlayState extends GameState implements IWorldSource, IStageSource {

	private World world;
	PlayStateInputProcessor stage;
	ComponentShipFactory shipFactory;
	ComponentJoiner componentJoiner;
	
	ArrayList<ComponentShip> shipList = new ArrayList<ComponentShip>();
	
	CameraManager cameraManager = new CameraManager();
	
	
	@Override
	public void Init(ScrapShipGame game) {
		super.Init(game);
		
		this.shipFactory = new ComponentShipFactory(this, this);
		
		final PlayState eventPlayState = this;

		world = new World(new Vector2(0,0.0f), false);
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
		
		ComponentShip ship2 = shipFactory.createShip(
				ComponentShipFactory.ShipType.DebugShip);
		ship2.setPosition(new Vector2(8,4.4f));
		ship2.setVelocity(new Vector2(-1,0));
		shipList.add(ship2);
		
		stage.setShipControl(ship1.getShipControl());
	}

	protected void destroyShip(ComponentShip target) {
		if( target != null ) {
			shipList.remove(target);
			target.remove();
			target.dispose();
		}
		
	}

	void addShip(ComponentShip ship) {
		
	}
	
	@Override
	public void Cleanup() {
		// TODO Auto-generated method stub
		world.dispose();

		for(ComponentShip ship : shipList)
		{
			ship.dispose();
		}
		shipList.clear();
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
	
		world.step(game.getUpdateFrame(),7,3);

		stage.postUpdate();
		
	}

	@Override
	public void Draw(ScrapShipGame game) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		cameraManager.setupRenderCamera(game);
		
		stage.draw();
		
		cameraManager.finalizeRender(game, world);
	}


	public void reset() {
		game.changeState(new PlayState());
	}
	
	@Override
	public World getWorld() {
		return world;
	}
	
	@Override
	public Stage getStage() {
		return stage;
	}
	
	public ComponentShipFactory getShipFactory()
	{
		return this.shipFactory;
	}
	
}
