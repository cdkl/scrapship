package com.zanateh.scrapship.ship;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.zanateh.scrapship.camera.IHasPosition;
import com.zanateh.scrapship.scene.ScrapShipActorGroup;
import com.zanateh.scrapship.ship.component.PodComponent;

public class ComponentShip extends ScrapShipActorGroup implements IHasPosition {

	private Body body;
	
	private ShipControl control = null;
	
	public ComponentShip(World world, Stage stage)
	{
		BodyDef def = new BodyDef();
		def.position.set(0,0);
		def.type = BodyDef.BodyType.DynamicBody;
		
		def.angularDamping = 0.8f;
		def.linearDamping = 0.2f;
		
		body = world.createBody(def);
		
		stage.addActor(this);
				
	}
	
	public void attachComponent(PodComponent component, float x, float y, float deg) {
		component.attach(this, x, y, deg);
		this.addActor(component);
	}

	public void dispose() {
	}

	@Override
	public void act(float delta) {
		body.setTransform(this.getPosition(), this.getRotation() * MathUtils.degreesToRadians);
		super.act(delta);
	}
	
	@Override
	public void postUpdate() {
		setPosition( body.getTransform().getPosition() );
		setRotation( body.getTransform().getRotation() * MathUtils.radiansToDegrees);
		super.postUpdate();
	}

	public void setShipControl(ShipControl control) {

		if( this.control != null ) {
			removeControl();
		}

		this.control = control;
	}
	
	public void removeControl() {
		this.control.remove();
		this.control = null;
	}

	public void setVelocity(Vector2 vel) {
		body.setLinearVelocity(vel);

	}

	public Body getBody() {
		return body;
	}

	public void setPosition(Vector2 position) {
		this.setPosition(position.x, position.y);
	}
	
	public Vector2 getPosition() {
		return new Vector2(this.getX(), this.getY());
	}
	
	public ShipControl getShipControl() {
		return this.control;
	}

	public void reactToDetach(PodComponent podComponent, Fixture fixture) {
		Gdx.app.log("Test", "AboutToReactToDetach");
		this.body.destroyFixture(fixture);
		
		checkIfNoComponents();
	}

	public void checkIfNoComponents() {
		boolean hasComponents = false;
		for( Actor actor : this.getChildren() ) {
			if( actor instanceof PodComponent ) {
				hasComponents = true;
				break;
			}
		}
		
		if( !hasComponents ) {
			this.fire(new DestroyShipEvent());
		}
	}
}
