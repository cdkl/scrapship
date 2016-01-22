package com.zanateh.scrapship.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.zanateh.scrapship.ScrapShipGame;

public class CameraManager {

	Vector3 cameraPos = new Vector3();

	private ScrapShipGame game;
	private World world;
	
	boolean debugRender = false;
	public boolean getDebugRender() { return debugRender; }
	// Debug display stuff for box2d
	private Matrix4 debugMatrix;
	Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
	
	
	public enum CameraMode {
		Free,
		Target
	};
	
	CameraMode cameraMode = CameraMode.Free;
	private IHasPosition target = null;
	
	public CameraManager(ScrapShipGame game, World world) {
		this.game = game;
		this.world = world;
	}

	public void setCameraMode(CameraMode cameraMode)
	{
		this.cameraMode = cameraMode; 
	}
	
	public void setTarget(IHasPosition target) {
		this.target = target;
	}
	
	public void setCameraPos(Vector3 cameraPos) {
		this.cameraPos.set(cameraPos);
	}
	
	public Vector2 getCameraPos() {
		OrthographicCamera cam = game.getCamera();
		return new Vector2(cam.position.x, cam.position.y);
	}
	
	public void setupRenderCamera()
	{
		if(this.cameraMode == CameraMode.Target) {
			Vector2 targetPos = target.getPosition();
			setCameraPos(new Vector3(targetPos.x, targetPos.y, 0));
		}
		
		OrthographicCamera camera = game.getCamera();
		camera.position.set(cameraPos);
		camera.zoom = zoom;
		camera.update();
		
		game.getSpriteBatch().setProjectionMatrix(game.getCamera().combined);
	}
	
	public void finalizeRender()
	{
		if(getDebugRender()) {
			game.getSpriteBatch().begin();

//			game.getSpriteBatch().flush();
			debugMatrix = new Matrix4(game.getCamera().combined);
			debugRenderer.render(world,  debugMatrix);
			game.getSpriteBatch().end();

		}
	}

	public void toggleDebugRender() {
		debugRender =! debugRender;
	}

	float zoom = 1.0f;
	final float MINZOOM = 0.5f;
	final float MAXZOOM = 10f;
	final float ZOOMRATE = 1.1f;
	
	public void incrementZoom(int amount) {
		boolean zoomIn = false;
		
		if( amount < 0 ) {
			amount *= -1;
			zoomIn = true;
		}
		
		for(int i = 0; i < amount; ++i ) {
			if( zoomIn ) {
				float newZoom = zoom / ZOOMRATE;
				if( newZoom >= MINZOOM) {
					zoom = newZoom;
				}
			}
			else {
				float newZoom = zoom * ZOOMRATE;
				if( newZoom <= MAXZOOM) {
					zoom = newZoom;
				}
			}
		}
	}

	public float getZoom() {
		return game.getCamera().zoom;
	}


	
}
