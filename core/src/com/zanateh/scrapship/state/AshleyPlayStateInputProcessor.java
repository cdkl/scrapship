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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.zanateh.scrapship.camera.CameraManager;
import com.zanateh.scrapship.engine.IShipControl;
import com.zanateh.scrapship.engine.helpers.PickHelper;
import com.zanateh.scrapship.engine.systems.DragAndDropSystem;

public class AshleyPlayStateInputProcessor extends Stage {

	GameState state;
	IShipControl shipControl = null;
	CameraManager cameraManager = null;
	// God this is horrible and shouldn't be here.
	DragAndDropSystem dragAndDropSystem;
	Engine engine;
	
	boolean currentTouchDown = false;
	
	public AshleyPlayStateInputProcessor(AshleyPlayState state,int width, int height, 
			SpriteBatch spriteBatch, Engine engine, CameraManager cameraManager, DragAndDropSystem dragAndDropSystem) {
		super(new StretchViewport(width, height), spriteBatch);
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
			if( state instanceof AshleyPlayState ) {
				((AshleyPlayState)state).reset();
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
			currentTouchDown = true;
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

		currentTouchDown = false;
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
<<<<<<< HEAD:core/src/com/zanateh/scrapship/state/AshleyPlayStateInputProcessor.java
		if(currentTouchDown) {
			dragAndDropSystem.rotateSelected(-10f*amount);
		}
		else {
			if(cameraManager != null ) cameraManager.incrementZoom(amount);
		}
		return true;
=======
		if(selectionManager.getSelected() != null) {
			selectionManager.rotateSelected(-10f*amount);
			return true;
		}
		else {
			if(cameraManager != null ) cameraManager.incrementZoom(amount);
			return true;
		}
>>>>>>> master:core/src/com/zanateh/scrapship/state/PlayStateInputProcessor.java
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
