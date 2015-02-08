package com.beanfarmergames.spaceboat.boat;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.beanfarmergames.spaceboat.entities.GameEntity;

public class EmissionSource implements RayCastCallback {

    private Vector2 source = null;
    private Body origin = null;
    
    private CollisionRecord lastHit;

    public EmissionSource() {
    }

    public void emit(World world, Body origin, Vector2 source, Vector2 dest) {
        this.source = source;
        this.origin = origin;
        
        lastHit = null;

        world.rayCast(this, source, dest);
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        //Avoid self collisions.
        if (fixture.getBody().equals(origin)) {
            return -1;
        }
        
        GameEntity hit = null;

        if (fixture.getUserData() != null) {
            hit = (GameEntity) fixture.getUserData();
        } else {
            hit = (GameEntity) fixture.getBody().getUserData();
        }
        
        lastHit = new CollisionRecord(hit, fixture, point, normal, fraction);

        return 0;
    }

    public CollisionRecord getLastHit() {
        return lastHit;
    }
    
    

}