package com.beanfarmergames.spaceboat.boat;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.beanfarmergames.spaceboat.entities.CollisionData;

public class EmissionSource implements RayCastCallback {

    private Vector2 source = null;
    private Body origin = null;
    private static final float TRACTOR_FORCE = 0.25f;
    private static final float MIN_DISTANCE = 20f;

    public EmissionSource() {
    }

    public void emit(World world, Body origin, Vector2 source, Vector2 dest) {
        this.source = source;
        this.origin = origin;

        world.rayCast(this, source, dest);
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        CollisionData hit = null;

        if (fixture.getUserData() != null) {
            hit = (CollisionData) fixture.getUserData();
        } else {
            hit = (CollisionData) fixture.getBody().getUserData();
        }
        
        if (hit != null) {
            
            float dist = source.dst(point);
            if (hit.canTractor() && dist > MIN_DISTANCE) {
                Body body = fixture.getBody();
                
                Vector2 force = normal.cpy().scl(TRACTOR_FORCE);
                Vector2 originForce = force.cpy().scl(-1);
                
                body.applyForceToCenter(force, true);
                
                //origin.applyForceToCenter(originForce, true);
            }
        }

        return 1;
    }

}