package com.zanateh.scrapship.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.zanateh.scrapship.ScrapShipGame;

public class InitState extends GameState {

	InitInputProcessor inputProcessor;
	
	Texture texture;
	
	@Override
	public void Init(ScrapShipGame game) {
		super.Init(game);

		inputProcessor = new InitInputProcessor(this);
		Gdx.input.setInputProcessor(inputProcessor);
		
		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
	}

	@Override
	public void Cleanup() {
		Gdx.input.setInputProcessor(null);
		texture.dispose();
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

	public void goToPlay() {
		game.changeState(new PlayState());
	}
	
	@Override
	public void Update(ScrapShipGame game) {
		// TODO Auto-generated method stub

	}

	@Override
	public void Draw(ScrapShipGame game) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.getCamera().update();
		game.getSpriteBatch().setProjectionMatrix(game.getCamera().combined);
		game.getSpriteBatch().begin();
		game.getSpriteBatch().draw(texture, 0, 0, 5,5);
		game.getSpriteBatch().end();
	}

}
