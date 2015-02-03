package com.beanfarmergames.spaceboat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.beanfarmergames.common.callbacks.CallbackHandler;
import com.beanfarmergames.common.callbacks.UpdateCallback;
import com.beanfarmergames.common.callbacks.impl.ListCallbackHandler;
import com.beanfarmergames.spaceboat.boat.Boat;
import com.beanfarmergames.spaceboat.field.Field;
import com.siondream.core.physics.MapBodyManager;

public class SpaceBoat implements Screen, InputProcessor {
    
    private SpriteBatch batch = null;
    private Texture[] img = new Texture[3];
    private Vector2 clouds[];
    private BitmapFont font = new BitmapFont();
    private ShapeRenderer renderer = null;

    private Field field;
    private RenderContextImpl renderContext = new RenderContextImpl();
    private List<Player> players = new ArrayList<Player>();

    private Vector2 spawn = null;
    private Random rand = new Random();
    
    private AssetManager assetManager;

    private ListCallbackHandler<UpdateCallback> updateCallbacks = new ListCallbackHandler<UpdateCallback>();

    public CallbackHandler<UpdateCallback> getUpdateCallbackHandler() {
        return updateCallbacks;
    }

    public List<Player> getPlayers() {
        return players;
    }

    private static final float CLOUD_OFFSCREEN_DIST = 200;

    public SpaceBoat() {
        assetManager = new AssetManager();
        
        int x = Gdx.app.getGraphics().getWidth();
        int y = Gdx.app.getGraphics().getHeight();

        renderer = new ShapeRenderer(500);

        Gdx.input.setInputProcessor(this);

        batch = new SpriteBatch();
        buildClouds(x, y);

        field = new Field(assetManager);
        field.resetLevel();

        spawn = new Vector2(x / 2, y / 2);
    }

    /**
     * Add a new player and spawn them in the game.
     * 
     * @param player
     */
    public void addPlayer(Player player) {
        players.add(player);
        Boat boat = new Boat(field, spawn);
        player.setBoat(boat);
    }

    /**
     * Remove player and their boat
     * 
     * @param player
     */
    public void removePlayer(Player player) {
        players.remove(player);
        Boat boat = player.getBoat();
        if (boat != null) {
            boat.dispose();
            player.setBoat(null);
        }
    }

    private void buildClouds(int x, int y) {
        img[0] = new Texture("art/cloud1.png");
        img[1] = new Texture("art/cloud2.png");
        img[2] = new Texture("art/cloud3.png");

        int countCount = 5;
        clouds = new Vector2[5];

        for (int i = 0; i < countCount; i++) {
            clouds[i] = new Vector2(rand.nextFloat() * x, rand.nextFloat() * y / 3 + y / 2);
        }
        // Sort so the largest y is first so we render front to back
        Arrays.sort(clouds, new Comparator<Vector2>() {
            @Override
            public int compare(Vector2 arg0, Vector2 arg1) {
                return -Float.compare(arg0.y, arg1.y);
            }
        });
    }

    @Override
    public void render(float delta) {
        float tickTimeSeconds = Gdx.graphics.getDeltaTime();
        long tickTimeMiliseconds = (long) (tickTimeSeconds * 1000);
        field.updateCallback(tickTimeMiliseconds);
        for (UpdateCallback callback : updateCallbacks.getCallbacks()) {
            callback.updateCallback(tickTimeMiliseconds);
        }

        // Color matches platformer pack from: http://open.commonly.cc/
        Gdx.gl.glClearColor(208f / 255, 244f / 255, 247f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderClouds();

        // Camera

        renderer.identity();

        // Render Callbacks

        renderContext.setBoat(null);
        renderContext.setRenderer(renderer);
        for (RenderLayer renderLayer : RenderLayer.values()) {
            renderContext.setRenderLayer(renderLayer);

            field.render(renderContext);
        }

        // HUD

        Matrix4 matrix = new Matrix4();
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        matrix.setToOrtho2D(0, 0, width, height);
        renderer.setProjectionMatrix(matrix);

        batch.begin();
        int yOffset = 50;
        for (Player player : players) {
            TextBounds bounds = font.draw(batch, player.getName(), 50, yOffset);
            yOffset += bounds.height * 1.5;
        }
        batch.end();
        
    }

    private void renderClouds() {
        batch.begin();

        int x = Gdx.graphics.getWidth();
        float stageSize = CLOUD_OFFSCREEN_DIST * 2 + x;
        float cloudTime = TimeUtils.nanoTime() / 1000000000.0f;
        float timeMultiplierPerI = 10;
        for (int i = 0; i < clouds.length; i++) {
            Vector2 pos = clouds[i].cpy();
            pos.x = ((pos.x + cloudTime * (i + 1) * timeMultiplierPerI) % stageSize) - CLOUD_OFFSCREEN_DIST;

            batch.draw(img[i % img.length], pos.x, pos.y);
        }

        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        Boat firstBoat = field.getBoats().get(0);

        switch (keycode) {
        case Input.Keys.Q:
            firstBoat.getBoatControl().getLeft().setX(1);
            break;
        case Input.Keys.W:
            firstBoat.getBoatControl().getRight().setX(1);
            break;
        case Input.Keys.R:
            firstBoat.spawn(spawn);
            break;
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        Boat firstBoat = field.getBoats().get(0);

        switch (keycode) {
        case Input.Keys.Q:
            firstBoat.getBoatControl().getLeft().setX(0);
            break;
        case Input.Keys.W:
            firstBoat.getBoatControl().getRight().setX(0);
            break;
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        field.dispose();
        font.dispose();
        batch.dispose();
        for (Texture i : img) {
            i.dispose();
        }
        renderer.dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }
}
