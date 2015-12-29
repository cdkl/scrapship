package com.zanateh.scrapship.engine.helpers;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.HardpointComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Hardpoint;

public class HardpointHelper {
	
	
	public static Hardpoint createHardpoint(Component component, Vector2 position) {
		Hardpoint hardpoint = new Hardpoint();
		hardpoint.component = component;
		hardpoint.position = new Vector2(position);
		
		return hardpoint;
	}
	

	public static void attach(Hardpoint hp1, Hardpoint hp2) {
		if( hp1.attached != null ) {
			throw new RuntimeException("Cannot attach hardpoints: hardpoint " + hp1.toString() + " already attached to " + hp1.attached.toString());
		}
		if( hp2.attached != null ) {
			throw new RuntimeException("Cannot attach hardpoints: hardpoint " + hp2.toString() + " already attached to " + hp2.attached.toString());
		}
		
		hp1.attached = hp2;
		hp2.attached = hp1;
	}
	
	public static void detach(Hardpoint hp1) {
		Hardpoint hp2 = hp1.attached;
		if( hp2 == null ) {
			throw new RuntimeException("Cannot attach hardpoints: hardpoint " + hp1.toString() + " not attached.");			
		}
			
		hp1.attached = null;
		hp2.attached = null;
	}
	
	public static void detachAll(Array<Hardpoint> hardpoints) {
		for( Hardpoint hardpoint : hardpoints) {
			if(hardpoint.attached != null) {
				detach(hardpoint);
			}
		}
		
	}
	
	
}
