package com.beanfarmergames.spaceboat.field;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.beanfarmergames.common.callbacks.RenderCallback;
import com.beanfarmergames.common.callbacks.UpdateCallback;
import com.beanfarmergames.spaceboat.RenderContext;
import com.beanfarmergames.spaceboat.boat.Boat;

public class Field implements UpdateCallback, RenderCallback<RenderContext> {

    private World world;
    public static final float G = -0.001f;
    //private static final float WORLD_STEPS_MILISECOND = 0.1f;

    private List<UpdateCallback> updateCallbacks = new ArrayList<UpdateCallback>();
    private List<RenderCallback<RenderContext>> renderCallbacks = new ArrayList<RenderCallback<RenderContext>>();

    private List<Boat> boats = new ArrayList<Boat>();

    private Rectangle extents;

    public Rectangle getFieldExtents() {
        return extents;
    }

    public void resetLevel() {
        Vector2 gravity = new Vector2(0.0f, G);
        boolean doSleep = false;
        world = new World(gravity, doSleep);
        updateCallbacks.clear();
        renderCallbacks.clear();
        boats.clear();

        int x = Gdx.app.getGraphics().getWidth();
        int y = Gdx.app.getGraphics().getHeight();
        extents = new Rectangle(0, 0, x, y);

        createWall(world, extents);

        /*
         * BodyDef bd = new BodyDef(); bd.allowSleep = false; bd.position.set(0,
         * 0);
         * 
         * Body body = world.createBody(bd); body.setUserData(null);
         * 
         * EdgeShape es = new EdgeShape(); es.set(-500,-50,500,-50);
         * 
         * FixtureDef fdef = new FixtureDef(); fdef.shape = es; fdef.density =
         * 1.0f; fdef.friction = 0.5f; fdef.restitution = 0.6f;
         * body.createFixture(fdef);
         * 
         * 
         * body.setType(BodyDef.BodyType.StaticBody);
         */
    }

    private static FixtureDef wallFixture(float x1, float y1, float x2, float y2) {
        EdgeShape es = new EdgeShape();
        es.set(x1, y1, x2, y2);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = es;
        fdef.density = 1.0f;
        fdef.friction = 0.5f;
        fdef.restitution = 0.6f;

        return fdef;
    }

    public static void createWall(World world, Rectangle box) {

        BodyDef bd = new BodyDef();
        bd.allowSleep = false;
        bd.position.set(0, 0);

        Body body = world.createBody(bd);
        body.setUserData(null);

        FixtureDef fdef1 = wallFixture(box.x, box.y + box.height, box.x, box.y);
        body.createFixture(fdef1);
        FixtureDef fdef2 = wallFixture(box.x, box.y, box.x + box.width, box.y);
        body.createFixture(fdef2);
        FixtureDef fdef3 = wallFixture(box.x + box.width, box.y, box.x + box.width, box.y + box.height);
        body.createFixture(fdef3);
        FixtureDef fdef4 = wallFixture(box.x + box.width, box.y + box.height, box.x, box.y + box.height);
        body.createFixture(fdef4);

        body.setType(BodyDef.BodyType.StaticBody);
    }

    @Override
    public void render(RenderContext renderContext) {

        for (RenderCallback<RenderContext> callback : renderCallbacks) {
            callback.render(renderContext);
        }
    }

    @Override
    public void updateCallback(long miliseconds) {
        // FIXME: HARDCODE 4 ITERS PER FRAME
        long iters = 4;// (long)(miliseconds * WORLD_STEPS_MILISECOND);
        float seconds = (float) miliseconds;
        float worldStepSeconds = seconds / (float) iters;

        for (int i = 0; i < iters; i++) {
            world.step(worldStepSeconds, 10, 10);
        }

        for (UpdateCallback callback : updateCallbacks) {
            callback.updateCallback(miliseconds);
        }
    }

    public World getWorld() {
        return world;
    }

    public void spawnBoat(Boat b) {
        boats.add(b);
    }

    public List<Boat> getBoats() {
        return boats;
    }

    public void registerUpdateCallback(UpdateCallback updateCallback) {
        updateCallbacks.add(updateCallback);
    }

    public void registerRenderCallback(RenderCallback<RenderContext> renderCallback) {
        renderCallbacks.add(renderCallback);
    }
}
