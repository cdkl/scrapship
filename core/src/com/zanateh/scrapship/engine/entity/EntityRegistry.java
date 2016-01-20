package com.zanateh.scrapship.engine.entity;

import java.util.Hashtable;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.utils.LongMap;

public class EntityRegistry implements EntityListener {

	LongMap<ScrapEntity> entityTable = new LongMap<ScrapEntity>();	
	
	@Override
	public void entityAdded(Entity entity) {
		if(entity instanceof ScrapEntity) {
			ScrapEntity se = (ScrapEntity)entity;
			entityTable.put(se.ID(), se);
		}
		
	}

	@Override
	public void entityRemoved(Entity entity) {
		// TODO Auto-generated method stub
		if(entity instanceof ScrapEntity) {
			entityTable.remove(((ScrapEntity)entity).ID());
		}
	}

	public ScrapEntity getEntity(long ID) {
		return entityTable.get(ID, null);
	}
	
}
