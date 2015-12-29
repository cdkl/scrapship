package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.subcomponents.Hardpoint;

public class HardpointComponent implements Component {
	public float hardpointRadius = 0.1f;
	public Array<Hardpoint> hardpoints = new Array<Hardpoint>();
}
