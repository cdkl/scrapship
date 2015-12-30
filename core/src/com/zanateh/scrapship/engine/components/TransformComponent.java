package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class TransformComponent implements Component {
	public final Vector2 position = new Vector2();
	public float rotation = 0.0f;

	public void transformPositionToGlobal(Vector2 position) {
		position.rotate(this.rotation);
		position.add(this.position);
	}

	public float transformRotationToGlobal(float rotation) {
		return rotation + this.rotation;
	}
	
	
}
