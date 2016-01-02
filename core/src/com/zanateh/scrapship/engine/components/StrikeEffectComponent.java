package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.effects.StrikeEffect;

public class StrikeEffectComponent implements Component {
	
	
	public Array<StrikeEffect> strikeEffects = new Array<StrikeEffect>();
}
