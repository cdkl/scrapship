package com.zanateh.scrapship.ship.component;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.zanateh.scrapship.scene.ScrapShipActorGroup;
import com.zanateh.scrapship.ship.ComponentShip;
import com.zanateh.scrapship.ship.DetachComponentAction;
import com.zanateh.scrapship.state.ISelectable;

public class PodComponent extends ScrapShipActorGroup implements ISelectable {

	private Sprite sprite;
	
	Fixture fixture = null;
	ComponentShip ship = null;
	
	final float radius = 0.5f;
	
	private ArrayList<ComponentThruster> thrusters = new ArrayList<ComponentThruster>();
	
	private ArrayList<Hardpoint> hardpoints = new ArrayList<Hardpoint>();
	
	public PodComponent() {
		setHeight(radius);
		setWidth(radius);
	}
	
//	public void act(float delta) {
//	}
//	
//	@Override
//	public void postUpdate()
//	{		
//	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		Vector2 worldPos = getPosition();
	
		sprite.setPosition(worldPos.x - (sprite.getWidth()/2), 
						   worldPos.y - sprite.getHeight()/2);

		sprite.setRotation( getRotation() );
		sprite.setAlpha(0.5f);
		
		sprite.draw(batch);
	}


	public void attach(ComponentShip ship, float posx, float posy, float deg) {
		this.ship = ship;
		Body shipBody = ship.getBody();
		
		FixtureDef fixDef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		setRotation(deg); // can't rotate a sphere
		this.setX(posx);
		this.setY(posy);
		shape.setPosition(getPosition());
		
		fixDef.shape = shape;
		fixDef.density = 1.0f;
		fixDef.restitution = 0.5f;
		fixDef.friction = 0.6f;
		this.fixture = shipBody.createFixture(fixDef);
		
		Texture image = new Texture(Gdx.files.internal("data/pod.png"));
		sprite = new Sprite(image);
		sprite.setSize(1,1);
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
	}

	public void dispose() {
	}
	
	public void setPosition( Vector2 pos)
	{
		this.setPosition(pos.x, pos.y);
	}
	
	public Vector2 getPosition()
	{
		return new Vector2(this.getX(), this.getY());
	}
	
	public ComponentShip getShip()
	{
		return ship;
	}
	
	public Fixture getFixture()
	{
		return this.fixture;
	}
	
	public void addHardpoint(Vector2 pos)
	{
		Hardpoint hp = new Hardpoint(this, pos);
		hardpoints.add(hp);
	}
	
	public Hardpoint getHardpoint(int index) {
		return hardpoints.get(index);
	}

	public ComponentThruster addThruster(Vector2 pos, Vector2 vec, float strength) {
		ComponentThruster thruster = new ComponentThruster(this, pos, vec, strength);
		this.addActor(thruster);
		thrusters.add(thruster);
		return thruster;
	}
	
	public void removeThruster(ComponentThruster thruster) {
		if(thrusters.contains(thruster)) {
			this.removeActor(thruster);
			thrusters.remove(thruster);
		}
	}
	
	public ArrayList<ComponentThruster> getThrusters() {
		return thrusters;
	}
	
	public void attachRelativeComponent(PodComponent component, float offsetX, float offsetY, float rad) {
		this.ship.attachComponent(component, offsetX, offsetY, rad);
		
	}

	public void transformPositionToParent(Vector2 position) {
		position.add(this.getPosition());
	}

	public void transformVectorToParent(Vector2 vector) {
		vector.rotate(this.getRotation());
	}
	
	boolean isSelected = false;
	
	@Override
	public void select()
	{
		Group parent = getParent();
		
		// If parented, tell the parent we're detaching.
		if(parent != null) {
			this.detach();
		}
		
		isSelected=true;
	}
	
	public void detach()
	{
		getParent().addAction(new DetachComponentAction(this, this.getFixture()));
		Gdx.app.log("Test", "AboutToRemove");

		Group root = this.getStage().getRoot();
		this.remove();
		root.addActor(this);
		
		for(Hardpoint hp : hardpoints) {
			hp.detach();
		}
	}

	@Override
	public void release() {
		isSelected=false;
		
		Gdx.app.log("Selection", "Firing ReleasedComponent.");
		this.fire(new ReleasedComponentEvent(getPosition()));
		
	}

	public ArrayList<Hardpoint> getHardpoints() {
		// TODO Auto-generated method stub
		return this.hardpoints;
	}

	
}
