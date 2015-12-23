package com.zanateh.scrapship.state;

import com.zanateh.scrapship.ScrapShipGame;

public abstract class GameState {

	protected ScrapShipGame game;
	
	public void Init(ScrapShipGame game) {
		this.game = game;
	}
	
	public abstract void Cleanup();
	
	public abstract void Pause();
	public abstract void Resume();
	
	public abstract void HandleEvents(ScrapShipGame game);
	public abstract void Update(ScrapShipGame game);
	public abstract void Draw(ScrapShipGame game);
	
	public void ChangeState(ScrapShipGame game, GameState state)
	{
		game.changeState(state);
	}
	
	
}
