package com.zanateh.scrapship.engine.helpers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.zanateh.scrapship.engine.components.PlayerControlComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;

public class NavigationHelper {
	
	private static ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	private static ComponentMapper<PlayerControlComponent> controlMapper = ComponentMapper.getFor(PlayerControlComponent.class);

	final static float ARC_HALF_WIDTH = 45f;
	final static float COURSE_TOLERANCE = 5f;
	
	public static void moveTowards(Entity entity, Vector2 travelDirection) {
		TransformComponent tc = transformMapper.get(entity);
		PlayerControlComponent pcc = controlMapper.get(entity);

		pcc.shipControl.resetThrust();

		// Is the target in our front arc?
		float bearing = (travelDirection.angle() - tc.rotation) % 360f;
		if( Math.abs(bearing) <= ARC_HALF_WIDTH) {
			// fire forward
			pcc.shipControl.setForwardThrust(1f);
		}
		if( bearing < -COURSE_TOLERANCE ) {
			pcc.shipControl.setCWThrust(1f);
			// turn left
		}
		else if( bearing > COURSE_TOLERANCE) {
			pcc.shipControl.setCCWThrust(1f);
		}
	}


}
