package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

public class ShipComponent implements Component {
	public Array<Entity> pods = new Array<Entity>();
}
