package com.beanfarmergames.spaceboat.boat;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.beanfarmergames.spaceboat.entities.CollisionData;

public class CollisionRecord {
    private final Fixture fixture;
    private final Vector2 point;
    private final Vector2 normal;
    private final float fraction;
    private final CollisionData collisionData;
    
    public Fixture getFixture() {
        return fixture;
    }
    public Vector2 getPoint() {
        return point;
    }
    public Vector2 getNormal() {
        return normal;
    }
    public float getFraction() {
        return fraction;
    }
    public CollisionData getCollisionData() {
        return collisionData;
    }
    public CollisionRecord(CollisionData collisionData, Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        super();
        this.collisionData = collisionData;
        this.fixture = fixture;
        this.point = point.cpy();
        if (normal != null) {
            this.normal = normal.cpy();
        } else {
            this.normal = null;
        }
        this.fraction = fraction;
    }
    
    
}
