package com.beanfarmergames.spaceboat.field;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.beanfarmergames.common.callbacks.RenderCallback;
import com.beanfarmergames.common.callbacks.UpdateCallback;
import com.beanfarmergames.common.callbacks.impl.ListCallbackHandler;
import com.beanfarmergames.spaceboat.RenderContext;
import com.beanfarmergames.spaceboat.RenderLayer;
import com.beanfarmergames.spaceboat.boat.Boat;
import com.beanfarmergames.spaceboat.entities.Cow;
import com.beanfarmergames.spaceboat.mission.MapConstants;
import com.siondream.core.physics.MapBodyManager;

public class Field implements UpdateCallback, RenderCallback<RenderContext>, Disposable {
    private static final String TAG = "Field";

    private World world = null;
    public static final float G = -0.001f;
    // private static final float WORLD_STEPS_MILISECOND = 0.1f;
    
    private ListCallbackHandler<UpdateCallback> updateCallbacks = new ListCallbackHandler<UpdateCallback>();
    private ListCallbackHandler<RenderCallback<RenderContext>> renderCallbacks = new ListCallbackHandler<RenderCallback<RenderContext>>();
    private ListCallbackHandler<Disposable> disposeCallbacks = new ListCallbackHandler<Disposable>();

    private List<Boat> boats = new ArrayList<Boat>();

    private Rectangle extents;

    private OrthographicCamera camera = null;
    private TiledMap map = null;
    private TiledMapRenderer mapRenderer = null;
    private final String levelName;

    private final AssetManager assetManager;

    public Field(AssetManager assetManager, String levelName) {
        this.assetManager = assetManager;
        this.levelName = levelName;

        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load(levelName, TiledMap.class);
        assetManager.finishLoading();
    }
    
    public Map getMap() {
        return map;
    }

    public Rectangle getFieldExtents() {
        return extents;
    }

    public void resetLevel() {
        Vector2 gravity = new Vector2(0.0f, G);
        boolean doSleep = false;
        if (world != null) {
            world.dispose();
            world = null;
        }
        world = new World(gravity, doSleep);
        updateCallbacks.clear();
        renderCallbacks.clear();
        boats.clear();

        int x = Gdx.app.getGraphics().getWidth();
        int y = Gdx.app.getGraphics().getHeight();
        extents = new Rectangle(0, 0, x, y);

        createWall(world, extents);
        loadTiles();
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, x,y);
    }

    private void loadTiles() {
        if (map != null) {
            map.dispose();
            map = null;
        }
        map = assetManager.get(levelName);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        MapBodyManager mbm = new MapBodyManager(getWorld(), 1, null, 1);
        mbm.createPhysics(map);
        
        MapLayer gameObjectsLayer = map.getLayers().get(MapConstants.MAP_GAME_OBJECTS_LAYER);
        for (MapObject mapObject : gameObjectsLayer.getObjects()) {
            if (MapConstants.OBJECT_TYPE_COW.equals(mapObject.getProperties().get(MapConstants.OBJECT_TYPE))) {
                //mapObject.getProperties().g
                //TODO: Get x,y
                if (mapObject instanceof CircleMapObject) {
                    CircleMapObject circleMapObject = (CircleMapObject)mapObject;
                    Vector2 position = new Vector2(circleMapObject.getCircle().x, circleMapObject.getCircle().y);
                    Cow cow = new Cow(this, position);
                } else if (mapObject instanceof EllipseMapObject) {
                    EllipseMapObject elipseMapObject = (EllipseMapObject)mapObject;
                    Vector2 position = new Vector2(elipseMapObject.getEllipse().x, elipseMapObject.getEllipse().y);
                    Cow cow = new Cow(this, position);
                } else {
                    Gdx.app.log(TAG, "Unknown map object: " + mapObject.getName());
                }
            }
                
        }
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

    private static void createWall(World world, Rectangle box) {

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
        
        if (RenderLayer.BACKGROUND.equals(renderContext.getRenderLayer())) {
            camera.update();
            mapRenderer.setView(camera);
            mapRenderer.render();
        }

        for (RenderCallback<RenderContext> callback : renderCallbacks.getCallbacks()) {
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

        for (UpdateCallback callback : updateCallbacks.getCallbacks()) {
            callback.updateCallback(miliseconds);
        }
    }

    public World getWorld() {
        return world;
    }

    public void spawnBoat(Boat b) {
        boats.add(b);
    }

    public void removeBoat(Boat b) {
        boats.remove(b);
    }

    public List<Boat> getBoats() {
        return boats;
    }

    public ListCallbackHandler<UpdateCallback> getUpdateCallbacks() {
        return updateCallbacks;
    }
    
    public ListCallbackHandler<RenderCallback<RenderContext>> getRenderCallbacks() {
        return renderCallbacks;
    }

    public ListCallbackHandler<Disposable> getDisposeCallbacks() {
        return disposeCallbacks;
    }

    @Override
    public void dispose() {
        for (Disposable d : disposeCallbacks.getCallbacks()) {
            d.dispose();
        }
        disposeCallbacks.clear();
        
        if (world != null) {
            world.dispose();
            world = null;
        }
    }
}
