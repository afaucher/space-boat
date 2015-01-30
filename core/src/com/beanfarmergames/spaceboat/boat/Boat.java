package com.beanfarmergames.spaceboat.boat;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.beanfarmergames.common.callbacks.RenderCallback;
import com.beanfarmergames.common.callbacks.UpdateCallback;
import com.beanfarmergames.common.controls.AxisControl;
import com.beanfarmergames.common.physics.PhysicsUtil;
import com.beanfarmergames.spaceboat.RenderContext;
import com.beanfarmergames.spaceboat.RenderLayer;
import com.beanfarmergames.spaceboat.field.Field;

public class Boat implements UpdateCallback, RenderCallback<RenderContext> {

    private static final float MAX_THRUST_N = 10.0f;
    private static final float PART_RAIDIUS = 15;

    private final Body body;
    private final Field field;
    private final BoatControl controls = new BoatControl();
    private final Fixture left, right;

    private static Fixture attachShape(Body body, Vector2 offset, float radius, Object userData) {
        Fixture fixture = null;

        CircleShape sd = new CircleShape();
        sd.setRadius(radius);
        sd.setPosition(offset);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = sd;
        fdef.density = 1.0f;
        fdef.friction = 0.5f;
        fdef.restitution = 0.6f;

        fixture = body.createFixture(fdef);
        fixture.setUserData(userData);

        return fixture;
    }

    public Boat(final Field field, Vector2 spawn) {
        this.field = field;

        World world = field.getWorld();

        BodyDef bd = new BodyDef();
        bd.allowSleep = true;
        bd.position.set(spawn.x, spawn.y);
        body = world.createBody(bd);
        body.setBullet(true);
        body.setAngularDamping(0.01f);
        body.setLinearDamping(0.01f);

        body.setType(BodyDef.BodyType.DynamicBody);

        left = attachShape(body, new Vector2(-5, 0), PART_RAIDIUS, this);
        right = attachShape(body, new Vector2(5, 0), PART_RAIDIUS, this);

        field.registerUpdateCallback(this);
        field.registerRenderCallback(this);
        field.spawnBoat(this);
    }

    public void spawn(Vector2 spwan) {
        body.setAngularVelocity(0);
        body.setTransform(spwan.cpy(), 0);
    }

    public BoatControl getBoatControl() {
        return controls;
    }

    @Override
    public void render(RenderContext renderContext) {
        if (!RenderLayer.PLAYER_BODY.equals(renderContext.getRenderLayer())) {
            return;
        }

        ShapeRenderer r = renderContext.getShapeRenderer();
        Vector2 leftFixturePosition = PhysicsUtil.getWorldFixturePosition(left);
        Vector2 rightFixturePosition = PhysicsUtil.getWorldFixturePosition(right);

        r.begin(ShapeType.Filled);
        r.setColor(1, 0, 0, 1);
        r.circle(leftFixturePosition.x, leftFixturePosition.y, PART_RAIDIUS);
        r.circle(rightFixturePosition.x, rightFixturePosition.y, PART_RAIDIUS);

        r.line(leftFixturePosition.x, leftFixturePosition.y, leftFixturePosition.x + 40, leftFixturePosition.y);
        r.line(leftFixturePosition.x, leftFixturePosition.y, leftFixturePosition.x, leftFixturePosition.y + 40);
        r.setColor(0, 1, 0, 1);

        float forwardRad = body.getAngle() + (float)Math.PI / 2.0f;
        float proboscusLength = 100;
        Vector2 p = body.getPosition();
        r.rectLine(p.x, p.y,
                p.x + proboscusLength * (float) Math.cos(forwardRad), p.y + proboscusLength * (float) Math.sin(forwardRad), 5);
        r.end();
    }

    private static void applyAxisThrustToBody(Body body, float thrustPercent, long miliseconds, Fixture fixture) {
        // TODO: Scale for time?
        float thrustNewtons = thrustPercent * MAX_THRUST_N;
        if (thrustNewtons <= 0) {
            return;
        }

        Transform transform = PhysicsUtil.getWorldFixturePositionTransform(fixture);
        Vector2 point = transform.getPosition().cpy();

        float rotationRad = transform.getRotation() + MathUtils.PI * 1.0f / 2.0f;
        float xThrustNewtons = (float) Math.cos(rotationRad) * thrustNewtons;
        float yThrustNewtons = (float) Math.sin(rotationRad) * thrustNewtons;
        Vector2 thrust = new Vector2(xThrustNewtons, yThrustNewtons);

        body.applyForce(thrust, point, true);
    }

    @Override
    public void updateCallback(long miliseconds) {

        AxisControl leftAxis = controls.getLeft();
        AxisControl rightAxis = controls.getRight();
        float l = leftAxis.getX();
        float r = rightAxis.getX();
        float blended = (r + l) / 2;
        float blend = 0.9f;
        l = l * blend + blended * (1-blend);
        r = r * blend + blended * (1-blend);
        
        applyAxisThrustToBody(body, l, miliseconds, left);
        
        applyAxisThrustToBody(body, r, miliseconds, right);
    }

}
