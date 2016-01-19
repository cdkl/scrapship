package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.zanateh.scrapship.engine.ShipControlVisitor;

public class PlayerControlComponent implements Component {
	public ShipControlVisitor shipControl = new ShipControlVisitor();
}
