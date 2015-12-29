package com.zanateh.scrapship.state;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.zanateh.scrapship.camera.CameraManager;
import com.zanateh.scrapship.engine.helpers.PickHelper;
import com.zanateh.scrapship.engine.systems.DragAndDropSystem;
import com.zanateh.scrapship.scene.ScrapShipStage;
import com.zanateh.scrapship.ship.ComponentShip;
import com.zanateh.scrapship.ship.ComponentShipFactory;
import com.zanateh.scrapship.ship.IShipControl;
import com.zanateh.scrapship.ship.component.PodComponent;

public class AshleyPlayStateInputProcessor extends ScrapShipStage {

	GameState state;
	IShipControl shipControl = null;
	CameraManager cameraManager = null;
	SelectionManager selectionManager = new SelectionManager();
	// God this is horrible and shouldn't be here.
	DragAndDropSystem dragAndDropSystem;
	Engine engine;
	
	PodComponent selected = null;
	
	public AshleyPlayStateInputProcessor(AshleyPlayState state,int width, int height, 
			SpriteBatch spriteBatch, Engine engine, CameraManager cameraManager, DragAndDropSystem dragAndDropSystem) {
		super(width, height, spriteBatch);
		this.state = state;
		this.engine = engine;
		this.cameraManager = cameraManager;
		this.dragAndDropSystem = dragAndDropSystem;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if( keycode == Input.Keys.P ) {
			if(cameraManager != null ) cameraManager.toggleDebugRender();
			return true;
		}
		
		if( shipControl != null ) {
			if( keycode == Input.Keys.W ) {
				shipControl.setForwardThrust(1);
				return true;
			}
			if( keycode == Input.Keys.S ) {
				shipControl.setReverseThrust(1);
				return true;
			}
			if( keycode == Input.Keys.A ) {
				shipControl.setCCWThrust(1);
				return true;
			}	
			if( keycode == Input.Keys.D ) {
				shipControl.setCWThrust(1);
				return true;
			}
			if(keycode == Input.Keys.Q ) {
				shipControl.setLeftThrust(1);
				return true;
			}
			if(keycode == Input.Keys.E ) {
				shipControl.setRightThrust(1);
				return true;
			}
		}
		
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if( shipControl != null ) {
			if( keycode == Input.Keys.W ) {
				shipControl.setForwardThrust(0);
				return true;
			}
			if( keycode == Input.Keys.S ) {
				shipControl.setReverseThrust(0);
				return true;
			}
			if( keycode == Input.Keys.A ) {
				shipControl.setCCWThrust(0);
				return true;
			}	
			if( keycode == Input.Keys.D ) {
				shipControl.setCWThrust(0);
				return true;
			}	
			if(keycode == Input.Keys.Q ) {
				shipControl.setLeftThrust(0);
				return true;
			}
			if(keycode == Input.Keys.E ) {
				shipControl.setRightThrust(0);
				return true;
			}	
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if( character == '`') {
			if( state instanceof PlayState ) {
				((PlayState)state).reset();
			}
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {	
		ImmutableArray<Entity> pickedEntities = PickHelper.pick(engine, getViewport(), new Vector2(screenX, screenY));
		if( pickedEntities.size() > 0 ) {
			for(Entity entity : pickedEntities ) {
				Gdx.app.log("HitTest", "Hit " + entity.toString());
			}
			this.dragAndDropSystem.setSelectedPosition(new Vector2(screenX, screenY));
			PickHelper.setSelected(pickedEntities.first());
			return true;
		}
		else {
			Gdx.app.log("HitTest", "Hit --");
			return false;
		}

	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		
		Entity entity = PickHelper.getSelected(engine);
		if( entity != null ) {
			this.dragAndDropSystem.setSelectedPosition(new Vector2(screenX, screenY));
			PickHelper.setUnselected(entity);
			return true;
		}

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		this.touchScreenPos.set(screenX, screenY);
		this.dragAndDropSystem.setSelectedPosition(new Vector2(screenX, screenY));
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		touchScreenTranslatedPos.set(touchScreenPos);
		selectionManager.setSelectionPosition(this.screenToStageCoordinates(touchScreenTranslatedPos));
	}
	
	Vector2 touchScreenPos = new Vector2(0,0);
	Vector2 touchScreenTranslatedPos = new Vector2(0,0);
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
//		touchScreenPos.set(screenX, screenY);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if(cameraManager != null ) cameraManager.incrementZoom(amount);
		return true;
	}

	public void setShipControl(IShipControl shipControl) {
		this.shipControl = shipControl;
	}

	public void removeShipControl(IShipControl shipControl) {
		if(this.shipControl == shipControl) {
			this.shipControl = null;
		}
	}

}
