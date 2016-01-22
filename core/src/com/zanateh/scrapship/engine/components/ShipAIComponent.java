package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.zanateh.scrapship.engine.ai.State;

public class ShipAIComponent implements Component {
	public State currentState;
	public State previousState;
}
