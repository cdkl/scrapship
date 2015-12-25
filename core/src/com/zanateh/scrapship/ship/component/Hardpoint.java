package com.zanateh.scrapship.ship.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.zanateh.scrapship.scene.ScrapShipActor;

public class Hardpoint extends ScrapShipActor {

	Hardpoint attached = null;
	Vector2 position;
	PodComponent component = null;
	
	Sprite sprite;
	
	final float HARDPOINT_RADIUS = 0.1f;
	
	public Hardpoint(PodComponent component, Vector2 pos)
	{
		this.component = component;
		this.position = new Vector2(pos);
		this.component.addActor(this);
		
		Texture image = new Texture(Gdx.files.internal("data/hardPointGreen.png"));
		sprite = new Sprite(image);
		sprite.setSize(HARDPOINT_RADIUS*2,HARDPOINT_RADIUS*2);
 		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		super.draw(batch, parentAlpha);
		if(this.attached == null) {
			sprite.setPosition(position.x - (sprite.getWidth()/2),
					position.y - (sprite.getHeight()/2));
			sprite.draw(batch);
		}
	}

	public void attach(Hardpoint hp)
	{
		if( attached != null )
		{
			detach();
		}
	
		// Vector of hardpoint to origin
		Vector2 rotatedRelativePosition = new Vector2(position);
		rotatedRelativePosition.rotate(this.component.getRotation());
		float masterRotationDegrees = rotatedRelativePosition.angle();
		rotatedRelativePosition.add(this.component.getPosition());
		
		this.attached = hp;

		// Need to take the provided vector, 
		// rotate our vector to match @ 180 degrees,
		
		float slaveRotationDegrees = hp.position.angle();	
		float requiredRotationDegrees = 
				(masterRotationDegrees - slaveRotationDegrees + 180) % 360;
		
		Vector2 newPosition = new Vector2(hp.position);
		newPosition.rotate(requiredRotationDegrees);
		
		Vector2 diff = new Vector2(rotatedRelativePosition);
		diff.sub(newPosition);
		
		this.component.attachRelativeComponent(
				hp.component,
				diff.x, diff.y, 
	 			requiredRotationDegrees);

		this.attached.slaveAttach(this);
		
	}
	
	private void slaveAttach(Hardpoint hp) 
	{
		this.attached = hp;
	}
	
	public void detach()
	{
		if( this.attached != null ) {
			this.attached.slaveDetach();
			this.attached = null;
		}
	}
	
	public void slaveDetach()
	{
		if( this.attached != null ) {
			this.attached = null;
		}
	}

	public boolean intersect(Hardpoint hardpoint) {
		if(hardpoint.component == this.component ) return false;
		
		Vector2 thisPos = new Vector2(position);
		this.localToStageCoordinates(thisPos);
		Vector2 thatPos = new Vector2(hardpoint.position);
		hardpoint.localToStageCoordinates(thatPos);
		
		thatPos.sub(thisPos);
		if(( thatPos.x < HARDPOINT_RADIUS && thatPos.x > -HARDPOINT_RADIUS ) &&
			(thatPos.y < HARDPOINT_RADIUS && thatPos.y > -HARDPOINT_RADIUS )) {
			return true;
		}
		
		// TODO Auto-generated method stub
		return false;
	}
	
}
