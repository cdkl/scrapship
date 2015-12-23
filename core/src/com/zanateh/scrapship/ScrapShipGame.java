package com.zanateh.scrapship;

import java.awt.List;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zanateh.scrapship.state.GameState;
import com.zanateh.scrapship.state.InitState;

public class ScrapShipGame implements ApplicationListener {
	private OrthographicCamera camera;
	public OrthographicCamera getCamera() { return camera; }
	
	private SpriteBatch batch;
	public SpriteBatch getSpriteBatch() { return batch; }
	
		
	float updateFrame = 1.0f/60.0f;
	public float getUpdateFrame() { return updateFrame; }
	float leftoverRender = 0.0f;
	
	GameState state;
	
	@Override
	public void create() {		
	
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 10, 7);
		batch = new SpriteBatch();
	
		changeState(new InitState());
	}

	@Override
	public void dispose() {
		batch.dispose();
		state.Cleanup();
	}

	@Override
	public void render() {		
		// Run the update loop as often as necessary to advance the game engine.
		float deltaTime = Gdx.graphics.getDeltaTime() + leftoverRender;
		while( deltaTime >= updateFrame ) {
			deltaTime -= updateFrame;
			update();
		}
		
		leftoverRender = deltaTime;

		if(state != null) {
			state.Draw(this);
		}
	}

	public void update()
	{
		if(state != null) {
			state.Update(this);
		}
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public void changeState(GameState state) {
		if(this.state != null) {
			this.state.Cleanup();
		}
		
		this.state = state;
		this.state.Init(this);		
	}
}
