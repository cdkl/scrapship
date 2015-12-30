package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class BeamComponent implements Component {
	public Vector2 direction = new Vector2();
	public float range = 10f;
	public float strength = 1f;
}
