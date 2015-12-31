package com.zanateh.scrapship.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.subcomponents.WeaponMount;

public class WeaponMountComponent implements Component {
	public Array<WeaponMount> weaponMounts = new Array<WeaponMount>();
}
