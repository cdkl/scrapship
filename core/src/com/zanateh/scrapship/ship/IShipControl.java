package com.zanateh.scrapship.ship;

public interface IShipControl {

	void setForwardThrust(float thrust);

	void setReverseThrust(float thrust);

	void setLeftThrust(float thrust);

	void setRightThrust(float thrust);

	void remove();

	void setCCWThrust(float thrust);

	void setCWThrust(float thrust);

}
