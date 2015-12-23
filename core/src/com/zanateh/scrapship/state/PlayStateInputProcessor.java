package com.zanateh.scrapship.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.zanateh.scrapship.camera.CameraManager;
import com.zanateh.scrapship.scene.ScrapShipStage;
import com.zanateh.scrapship.ship.ComponentShip;
import com.zanateh.scrapship.ship.ComponentShipFactory;
import com.zanateh.scrapship.ship.ShipControl;
import com.zanateh.scrapship.ship.component.PodComponent;

public class PlayStateInputProcessor extends ScrapShipStage {

	PlayState state;
	ShipControl shipControl = null;
	CameraManager cameraManager = null;
	SelectionManager selectionManager = new SelectionManager(this);
	
	PodComponent selected = null;
	
	public PlayStateInputProcessor(PlayState state,int width, int height, 
			SpriteBatch spriteBatch) {
		super(width, height, spriteBatch);
		this.state = state;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if( keycode == Input.Keys.D ) {
			if(cameraManager != null ) cameraManager.toggleDebugRender();
			return true;
		}
		
		if( shipControl != null ) {
			if( keycode == Input.Keys.UP ) {
				shipControl.setForwardThrust(1);
				return true;
			}
			if( keycode == Input.Keys.DOWN ) {
				shipControl.setReverseThrust(1);
				return true;
			}
			if( keycode == Input.Keys.LEFT ) {
				shipControl.setLeftThrust(1);
				return true;
			}	
			if( keycode == Input.Keys.RIGHT ) {
				shipControl.setRightThrust(1);
				return true;
			}	
		}
		
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if( shipControl != null ) {
			if( keycode == Input.Keys.UP ) {
				shipControl.setForwardThrust(0);
				return true;
			}
			if( keycode == Input.Keys.DOWN ) {
				shipControl.setReverseThrust(0);
				return true;
			}
			if( keycode == Input.Keys.LEFT ) {
				shipControl.setLeftThrust(0);
				return true;
			}	
			if( keycode == Input.Keys.RIGHT ) {
				shipControl.setRightThrust(0);
				return true;
			}	
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if( character == '`') {
			state.reset();
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {		
		Vector2 stageCoords = this.screenToStageCoordinates(new Vector2(screenX, screenY));
		Actor actor = this.hit(stageCoords.x, stageCoords.y, false);

		if( actor == null ) { 
			Gdx.app.log("HitTest", "Hit --");
		}
		else {
			Gdx.app.log("HitTest", "Hit " + actor.toString());
			if( actor instanceof ISelectable ) {
				ISelectable selectable = ((ISelectable)actor);
				this.touchScreenPos.set(screenX, screenY);
				selectionManager.setSelectionPosition(this.screenToStageCoordinates(new Vector2(screenX, screenY)));
				selectionManager.setSelected(selectable);
				
				return true;
			}
		}
		
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		
		if( this.selectionManager.getSelected() != null ) {
			
			this.selectionManager.releaseSelected();
			
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		this.touchScreenPos.set(screenX, screenY);
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

	public void setShipControl(ShipControl shipControl) {
		this.shipControl = shipControl;
	}

	public void removeShipControl(ShipControl shipControl) {
		if(this.shipControl == shipControl) {
			this.shipControl = null;
		}
	}
	
	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

}
