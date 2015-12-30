package com.zanateh.scrapship.engine;

public interface IShipControl {

	void setForwardThrust(float thrust);

	void setReverseThrust(float thrust);

	void setLeftThrust(float thrust);

	void setRightThrust(float thrust);

	void setCCWThrust(float thrust);

	void setCWThrust(float thrust);

	void remove();
}
