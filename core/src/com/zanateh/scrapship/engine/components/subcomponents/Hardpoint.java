package com.zanateh.scrapship.engine.components.subcomponents;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class Hardpoint {
	public Hardpoint attached = null;
	public Vector2 position = null;
	public Component component = null;
	
	public static final float HARDPOINT_RADIUS = 0.1f;
}
