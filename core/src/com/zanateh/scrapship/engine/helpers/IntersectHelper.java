package com.zanateh.scrapship.engine.helpers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zanateh.scrapship.engine.components.BeamComponent;
import com.zanateh.scrapship.engine.components.IntersectComponent;
import com.zanateh.scrapship.engine.components.TransformComponent;
import com.zanateh.scrapship.engine.components.subcomponents.Hitbox;

public class IntersectHelper {

	private static ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
	private static ComponentMapper<BeamComponent> beamMapper = ComponentMapper.getFor(BeamComponent.class);
	private static ComponentMapper<IntersectComponent> intersectMapper = ComponentMapper.getFor(IntersectComponent.class);
	
	public static Vector2 intersectBeamHitbox(Entity beamEntity, Entity hittableEntity) {
		TransformComponent btc = transformMapper.get(beamEntity);
		BeamComponent bbc = beamMapper.get(beamEntity);
		
		TransformComponent htc = transformMapper.get(hittableEntity);
		IntersectComponent hic = intersectMapper.get(hittableEntity);
		
		Vector2 beamEnd = new Vector2(btc.position);
		Vector2 direction = bbc.getDirection();
		beamEnd.add(direction.x * bbc.range, direction.y * bbc.range);
		
		Vector2 returnVec = null;
		
		for(Hitbox hitbox : hic.hitboxes) {
			Vector2 hitboxLocation = new Vector2(hitbox.position);
			hitboxLocation.add(htc.position);
			Array<Vector2> hitLocations = IntersectHelper.intersectSegmentCircle(btc.position, beamEnd, hitboxLocation, hitbox.radius * hitbox.radius);
			Vector2 closestHitLocation;
			if(hitLocations.size == 2) {
				Vector2 localClosest = getShortestVector(btc.position, hitLocations.get(0), hitLocations.get(1));
				if(returnVec == null) {
					returnVec = localClosest;
				}
				else {
					returnVec = getShortestVector(btc.position, returnVec, localClosest);
				}
			}
			else if(hitLocations.size == 1) {
				if(returnVec == null ) {
					returnVec = hitLocations.get(0);
				}
				else {
					returnVec = getShortestVector(btc.position, returnVec, hitLocations.get(0));
				}
			}
		}
		
		return returnVec;
	}

	private static Vector2 da = new Vector2();
	private static Vector2 db = new Vector2();
	
	public static Vector2 getShortestVector(Vector2 o, Vector2 a, Vector2 b) {
		da.set(a);
		da.sub(o);
		db.set(b);
		db.sub(o);
		
		if(Math.abs(da.x) < Math.abs(db.x) || Math.abs(da.y) < Math.abs(db.y)) {
			return a;
		}
		else {
			return b;
		}

	}
	
	private static Vector2 p1 = new Vector2();
	private static Vector2 p2 = new Vector2();
	private static Vector2 d = new Vector2();
	
	private static Array<Vector2> intersectSegmentCircle(Vector2 a, Vector2 b, Vector2 o, float squareRadius) {
		p1.set(a);
	    p2.set(b);
	    p1.sub(o);
	    p2.sub(o);

	    d.set(p2);
	    d.sub(p1);

	    float det = p1.x * p2.y - p2.x * p1.y;

	    float dSq = d.len2();

	    float discrimant = squareRadius * dSq - det * det;

	    if (discrimant < 0) {
	        return new Array<Vector2>();
	    }
	    else if (discrimant == 0) {
	        Array<Vector2> t = new Array<Vector2>();
	        t.add(new Vector2(det * d.y / dSq + o.x, -det * d.x / dSq + o.y));

	        checkPointsOnSegment(t,a,b);
	        return t;
	    }
	    else {
	    	float discSqrt = (float) Math.sqrt(discrimant);

		    float sgn = 1;
		    if (d.y < 0) {
		        sgn = -1;
		    }
	
	        Array<Vector2> t = new Array<Vector2>();
		    t.add(new Vector2((det * d.y + sgn * d.x * discSqrt) / dSq + o.x, (-det * d.x + Math.abs(d.y) * discSqrt) / dSq + o.y));
		    t.add(new Vector2((det * d.y - sgn * d.x * discSqrt) / dSq + o.x, (-det * d.x - Math.abs(d.y) * discSqrt) / dSq + o.y));
		    checkPointsOnSegment(t,a,b);
		    return t;
	    }
	}
	
	// Assumed: points are on the line. Simple between checks.
	private static void checkPointsOnSegment(Array<Vector2> t, Vector2 a, Vector2 b) {
		Array<Vector2> delArray = new Array<Vector2>();
		for(Vector2 v : t) {
			if( ((a.x <= b.x && v.x >= a.x && v.x <= b.x) || (b.x <= a.x && v.x >= b.x && v.x <= a.x))
			  && ((a.y <= b.y && v.y >= a.y && v.y <= b.y) || (b.y <= a.y && v.y >= b.y && v.y <= a.y)))
			{
				continue;
			}
			delArray.add(v);
		}
		
		for(Vector2 v : delArray) {
			t.removeValue(v, true);
		}
	}
	
}
