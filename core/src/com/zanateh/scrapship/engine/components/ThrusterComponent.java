package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.subcomponents.Thruster;

public class ThrusterComponent implements Component {
	public Array<Thruster> thrusters = new Array<Thruster>();
}
