package com.beanfarmergames.common.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Transform;

public class PhysicsUtil {
    public static Vector2 getWorldFixturePosition(Fixture fixture) {
        Vector2 vec = new Vector2();
        Body body = fixture.getBody();
        Transform transform = body.getTransform();
        CircleShape shape = (CircleShape) fixture.getShape();
        vec.set(shape.getPosition());
        // TODO: Not clear if we need to cpy
        return transform.mul(vec).cpy();
    }

    public static Transform getWorldFixturePositionTransform(Fixture fixture) {
        Vector2 vec = new Vector2();
        Body body = fixture.getBody();
        Transform transform = body.getTransform();
        CircleShape shape = (CircleShape) fixture.getShape();
        vec.set(shape.getPosition());
        // TODO: Not clear if we need to cpy
        transform.setPosition(transform.mul(vec).cpy());
        return transform;
    }
}
