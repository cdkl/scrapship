package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

public class FixtureComponent implements Component {
	public Vector2 localPosition = new Vector2(0.0f, 0.0f);
	public float localRotation = 0.0f;
	public Fixture fixture = null;
}
