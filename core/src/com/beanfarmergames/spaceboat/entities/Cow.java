package com.beanfarmergames.spaceboat.entities;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.beanfarmergames.common.callbacks.RenderCallback;
import com.beanfarmergames.common.callbacks.UpdateCallback;
import com.beanfarmergames.spaceboat.RenderContext;
import com.beanfarmergames.spaceboat.RenderLayer;
import com.beanfarmergames.spaceboat.boat.CollisionRecord;
import com.beanfarmergames.spaceboat.field.Field;

public class Cow implements UpdateCallback, RenderCallback<RenderContext>, Disposable, GameEntity {
    private final Body body;
    private SpriteBatch batch = null;
    private final Field field;
    private Texture ship = new Texture("art/cow.png");
    
    private static final float COW_RADIUS = 15.0f;
    private static final float SPRITE_PADDING = 15;
    private static final float MAX_COW_TORQUE = 25.0f;
    private static final Random r = new Random();
    private static final float TIME_SCALE = 1 + 2 * r.nextFloat();
    
    private final float offset = r.nextFloat() * 10;
    

    //FIXME: Stolen from boat
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

    public Cow(Field field, Vector2 spawn) {
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
        body.setUserData(this);
        body.setType(BodyDef.BodyType.DynamicBody);
        
        attachShape(body, new Vector2(), COW_RADIUS, null);

        batch = new SpriteBatch();
        
        // Insert into field
        field.getRenderCallbacks().registerCallback(this);
        field.getUpdateCallbacks().registerCallback(this);
        field.getDisposeCallbacks().registerCallback(this);
    }

    @Override
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

    @Override
    public void render(RenderContext renderContext) {
        
        if (!RenderLayer.COWS.equals(renderContext.getRenderLayer())) {
            return;
        }
        
        //FIXME: Copied from boat
        batch.begin();
        float width = COW_RADIUS * 2;
        float height = COW_RADIUS * 2 * 1.13f;
        Vector2 pos = body.getTransform().getPosition();
        
        //TODO: There has to be an easier way to do this
        Matrix4 m1 = new Matrix4().trn(pos.x, pos.y, 0);
        Matrix4 m2 = new Matrix4().rotateRad(0, 0, 1, body.getAngle());
        
        batch.setTransformMatrix(m1.mul(m2));
        batch.draw(ship, - width / 2, - height / 2, width, height);
        batch.end();
        batch.setTransformMatrix(new Matrix4());
    }

    @Override
    public void updateCallback(long miliseconds) {
        // TODO Auto-generated method stub
        float cloudTime = TimeUtils.nanoTime() / 1000000000.0f;
        body.applyTorque((float)Math.sin(cloudTime + offset) * MAX_COW_TORQUE, true);
    }

    @Override
    public boolean canTractor() {
        return true;
    }

    @Override
    public void hitWithLaser(CollisionRecord cr, long miliseconds) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.COW;
    }

    @Override
    public int getMaxHealth() {
        return 1;
    }

    @Override
    public float getHealth() {
        return 1;
    }

}
