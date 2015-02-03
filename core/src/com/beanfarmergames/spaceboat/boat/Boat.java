package com.beanfarmergames.spaceboat.boat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.beanfarmergames.common.callbacks.RenderCallback;
import com.beanfarmergames.common.callbacks.UpdateCallback;
import com.beanfarmergames.common.controls.AxisControl;
import com.beanfarmergames.common.physics.PhysicsUtil;
import com.beanfarmergames.spaceboat.RenderContext;
import com.beanfarmergames.spaceboat.RenderLayer;
import com.beanfarmergames.spaceboat.debug.DebugSettings;
import com.beanfarmergames.spaceboat.field.Field;

public class Boat implements UpdateCallback, RenderCallback<RenderContext>, Disposable {

    private static final float JET_EMISSION_RATE = 100.0f;
    private static final int JET_SPREAD_ANGLE_DEG = 10;
    private static final float STEERING_BLENDING_FACTOR = 0.9f;
    private static final float MAX_THRUST_N = 10.0f;
    private static final float PART_RAIDIUS = 15;
    private static final float PART_DISTANCE = 30.0f;

    private final Body body;
    private final Field field;
    private final BoatControl controls = new BoatControl();
    private final Fixture left, right;
    private final SpriteBatch batch;

    private ParticleEffect[] jetEffect = new ParticleEffect[2];
    private Texture ship = new Texture("art/ship.png");

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

        // Build Body
        World world = field.getWorld();

        BodyDef bd = new BodyDef();
        bd.allowSleep = true;
        bd.position.set(spawn.x, spawn.y);
        body = world.createBody(bd);
        body.setBullet(true);
        body.setAngularDamping(0.01f);
        body.setLinearDamping(0.005f);

        body.setType(BodyDef.BodyType.DynamicBody);

        left = attachShape(body, new Vector2(-PART_DISTANCE / 2, 0), PART_RAIDIUS, this);
        right = attachShape(body, new Vector2(PART_DISTANCE / 2, 0), PART_RAIDIUS, this);

        // Insert into field
        field.getRenderCallbacks().registerCallback(this);
        field.getUpdateCallbacks().registerCallback(this);
        field.getDisposeCallbacks().registerCallback(this);
        field.spawnBoat(this);

        // Setup particles
        batch = new SpriteBatch();
        for (int i = 0; i < jetEffect.length; i++) {
            jetEffect[i] = new ParticleEffect();
            jetEffect[i].load(Gdx.files.internal("particles/jet.p"), Gdx.files.internal(""));
            jetEffect[i].start();
        }
    }
    
    public void dispose() {
        if (body != null) {
            World world = field.getWorld();
            world.destroyBody(body);
            //body = null;
        }
        field.getRenderCallbacks().removeCallback(this);
        field.getUpdateCallbacks().removeCallback(this);
        field.getDisposeCallbacks().removeCallback(this);
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

        batch.begin();
        for (int i = 0; i < jetEffect.length; i++) {
            jetEffect[i].draw(batch);
        }
        batch.end();

        if (DebugSettings.DEBUG_DRAW) {
            r.begin(ShapeType.Filled);
            r.setColor(1, 0, 0, 1);
            r.circle(leftFixturePosition.x, leftFixturePosition.y, PART_RAIDIUS);
            r.circle(rightFixturePosition.x, rightFixturePosition.y, PART_RAIDIUS);
    
            r.line(leftFixturePosition.x, leftFixturePosition.y, leftFixturePosition.x + 40, leftFixturePosition.y);
            r.line(leftFixturePosition.x, leftFixturePosition.y, leftFixturePosition.x, leftFixturePosition.y + 40);
            r.setColor(0, 1, 0, 1);
    
            float forwardRad = body.getAngle() + (float) Math.PI / 2.0f;
            float proboscusLength = 100;
            Vector2 p = body.getPosition();
            r.rectLine(p.x, p.y, p.x + proboscusLength * (float) Math.cos(forwardRad),
                    p.y + proboscusLength * (float) Math.sin(forwardRad), 5);
            r.end();
        }
        
        batch.begin();
        float width = PART_RAIDIUS * 2 + PART_DISTANCE;
        float height = PART_RAIDIUS * 2;
        Vector2 pos = body.getTransform().getPosition();
        
        //TODO: There has to be an easier way to do this
        Matrix4 m1 = new Matrix4().trn(pos.x, pos.y, 0);
        Matrix4 m2 = new Matrix4().rotateRad(0, 0, 1, body.getAngle());
        
        batch.setTransformMatrix(m1.mul(m2));
        batch.draw(ship, - width / 2, - height / 2, width, height);
        batch.end();
        batch.setTransformMatrix(new Matrix4());
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
        //We blend the controls together to avoid spinning in place
        float l = leftAxis.getX();
        float r = rightAxis.getX();
        float blended = (r + l) / 2;
        float blend = STEERING_BLENDING_FACTOR;
        l = l * blend + blended * (1 - blend);
        r = r * blend + blended * (1 - blend);

        applyAxisThrustToBody(body, l, miliseconds, left);
        applyAxisThrustToBody(body, r, miliseconds, right);

        // Jets
        float deltaSeconds = miliseconds / 1000.0f;
        updateJetEffect(jetEffect[0], left, l, deltaSeconds);
        updateJetEffect(jetEffect[1], right, r, deltaSeconds);
    }
    
    private void updateJetEffect(ParticleEffect pe, Fixture fixture, float scale, float deltaSeconds) {
        ParticleEmitter e = pe.getEmitters().first();
        
        Vector2 fixturePosition = PhysicsUtil.getWorldFixturePosition(fixture);
        e.setPosition(fixturePosition.x, fixturePosition.y);
        
        //Point along -y (down)
        float bodyAngle = body.getAngle() * MathUtils.radiansToDegrees + 270;
        float jetSpread = JET_SPREAD_ANGLE_DEG;
        
        ScaledNumericValue jetAngle = e.getAngle();
        jetAngle.setHigh(bodyAngle - jetSpread, bodyAngle + jetSpread);
        jetAngle.setLow(bodyAngle);
        ScaledNumericValue jetRate = e.getEmission();
        //We smooth the scale a bit so it doesn't drop off as fast
        float jetEmissionRate = JET_EMISSION_RATE * (float)Math.sqrt(scale);
        jetRate.setHigh(jetEmissionRate, jetEmissionRate);
        jetRate.setLow(jetEmissionRate, jetEmissionRate);
        
        pe.update(deltaSeconds);
    }

}
