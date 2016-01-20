package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.zanateh.scrapship.engine.ai.ShipStateMachine;

public class ShipAIComponent implements Component {
	public ShipStateMachine shipStateMachine = new ShipStateMachine();
}
