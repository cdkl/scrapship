package com.zanateh.scrapship.ship;

import com.badlogic.gdx.physics.box2d.Body;

public interface IThrust {
	void setThrust(float thrust);
	void applyThrust(Body body);
}
