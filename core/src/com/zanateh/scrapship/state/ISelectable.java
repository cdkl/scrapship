package com.zanateh.scrapship.state;

import com.badlogic.gdx.math.Vector2;

public interface ISelectable {
	public void select();
	public void release();
	public void setPosition(Vector2 selectionPosition);
}
