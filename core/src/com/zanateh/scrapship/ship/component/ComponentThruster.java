package com.zanateh.scrapship.ship.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.zanateh.scrapship.scene.ScrapShipActor;
import com.zanateh.scrapship.ship.IThrust;

public class ComponentThruster extends ScrapShipActor implements IThrust {

	// direction of acceleration (NOT the direction of "Thrust")
	Vector2 vec;
	// Strength of thruster
	float strength;
	// Current thruster power setting: should be 0-1
	float power;
	
	PodComponent component;
	
	private Sprite offSprite;
	private Sprite onSprite;
	
	public ComponentThruster(PodComponent component, Vector2 pos, Vector2 vec, float strength) {
		this.component = component;
		this.setPosition(pos.x, pos.y);
		
		this.vec = new Vector2(vec);
		this.strength = strength;
		this.power = 0;
		
		offSprite = new Sprite(new Texture(Gdx.files.internal("data/thruster.png")));
		offSprite.setSize(0.25f, 0.25f);
		offSprite.setOrigin(0, 0);
		onSprite = new Sprite(new Texture(Gdx.files.internal("data/thrusterOn.png")));
		onSprite.setSize(0.25f, 0.25f);
		onSprite.setOrigin(0, 0);

	}

	@Override
	public void setThrust(float thrust) {
		power = thrust;
	}

	public void act(float delta) {
		super.act(delta);
		Body body = component.getFixture().getBody();
		applyThrust(body);
	}
	
	public Vector2 getPosition() {
		return new Vector2(this.getX(), this.getY());
	}
	
	public Vector2 getThrustVector() {
		return vec;
	}
	
	public float getStrength() {
		return strength;
	}
	
	public PodComponent getComponent() {
		return component;
	}
	
	@Override 
	public void applyThrust(Body body) {		
		Vector2 shipPos = new Vector2(this.getPosition());
		component.transformPositionToParent(shipPos);
		Vector2 shipVec = new Vector2(vec);
		component.transformVectorToParent(shipVec);
		
		body.applyForce(body.getWorldVector(shipVec.scl(strength*power)), body.getWorldPoint(shipPos), true);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	
		Sprite sprite = power > 0 ? onSprite : offSprite;
		
		Vector2 spriteRenderOffset = new Vector2(sprite.getWidth(), sprite.getHeight()/2);
		Vector2 spriteVec = new Vector2(vec);
		spriteRenderOffset.rotate(spriteVec.angle() + 180);
				
		Vector2 spritePos = new Vector2(getPosition());
		spritePos.add(spriteRenderOffset);
		sprite.setPosition(spritePos.x, spritePos.y);
		
		float rotation = spriteVec.angle();
		sprite.setRotation(rotation);
		sprite.draw(batch);
	}

	@Override
	public void postUpdate() {
	}
	
	@Override
	public String toString() {
		String retStr = vec.toString() + ", " + strength + " @" + power;
		return retStr;
	}
	
}
