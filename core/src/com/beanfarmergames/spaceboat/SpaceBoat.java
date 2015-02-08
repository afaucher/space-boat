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
import com.beanfarmergames.spaceboat.mission.Mission;
import com.beanfarmergames.spaceboat.mission.MissionOne;
import com.beanfarmergames.spaceboat.mission.objective.Objective;
import com.siondream.core.physics.MapBodyManager;

public class SpaceBoat implements Screen {

    // Clouds
    private static final float CLOUD_OFFSCREEN_DIST = 200;
    private SpriteBatch batch = null;
    private Texture[] img = new Texture[3];
    private Vector2 clouds[];

    private BitmapFont font = new BitmapFont();
    private ShapeRenderer renderer = null;
    private AssetManager assetManager;
    private RenderContextImpl renderContext = new RenderContextImpl();
    private Random rand = new Random();

    // Seconds between having enough players and starting
    private static final float READY_TIMEOUT_SECONDS = 10;

    private Mission mission;
    private Field field;
    private GameState gameState;
    private float gameStateCounter = 0;

    private List<Player> players = new ArrayList<Player>();

    private Vector2 spawn = null;

    enum GameState {
        WaitingForPlayers, Playing, MissionComplete
    };

    private ListCallbackHandler<UpdateCallback> updateCallbacks = new ListCallbackHandler<UpdateCallback>();

    public CallbackHandler<UpdateCallback> getUpdateCallbackHandler() {
        return updateCallbacks;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public SpaceBoat() {
        assetManager = new AssetManager();

        int x = Gdx.app.getGraphics().getWidth();
        int y = Gdx.app.getGraphics().getHeight();

        renderer = new ShapeRenderer(500);

        batch = new SpriteBatch();
        buildClouds(x, y);

        mission = new MissionOne();
        field = new Field(assetManager, mission.getMapName());
        mission.loadMission(field);
        field.resetLevel();
        gameState = GameState.WaitingForPlayers;

        spawn = new Vector2(x / 2, y / 2);
    }

    public void spawn(Boat boat) {
        boat.spawn(spawn);
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

    /**
     * @return Number of additional players needed to start the game. Zero if
     *         ready.
     */
    private int getPlayersNeeded() {
        int needed = mission.getRequiredNumberOfPlayers() - players.size();
        if (needed < 0) {
            needed = 0;
        }
        return needed;
    }

    private void updateGameState(float delta) {
        float tickTimeSeconds = Gdx.graphics.getDeltaTime();
        long tickTimeMiliseconds = (long) (tickTimeSeconds * 1000);

        switch (gameState) {
        case WaitingForPlayers:
            if (getPlayersNeeded() <= 0) {
                gameStateCounter += delta;
                if (gameStateCounter > READY_TIMEOUT_SECONDS) {
                    gameStateCounter = READY_TIMEOUT_SECONDS;
                    gameState = GameState.Playing;
                }
            } else {
                gameStateCounter = 0;
            }
            break;
        case Playing:
            mission.updateCallback(tickTimeMiliseconds);
            if (mission.isMissionComplete()) {
                gameState = GameState.MissionComplete;
            }
            field.updateCallback(tickTimeMiliseconds);
            break;
        case MissionComplete:
            break;
        }

        // General callbacks run all the time
        for (UpdateCallback callback : updateCallbacks.getCallbacks()) {
            callback.updateCallback(tickTimeMiliseconds);
        }
    }

    private void renderMissionScreen() {
        batch.begin();
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        int horizontalPadding = 200;
        int xOffset = horizontalPadding;
        int yOffset = height - 50;
        int wrapWidth = width - horizontalPadding * 2;

        TextBounds bounds = null;
        switch (gameState) {
        case WaitingForPlayers: {
            bounds = font.drawWrapped(batch, "Mission: " + mission.getMissionName(), xOffset, yOffset, wrapWidth);
            yOffset -= bounds.height * 1.5;
            int needed = getPlayersNeeded();
            if (needed > 0) {
                bounds = font.drawWrapped(batch, "Waiting for " + needed + " more players.", xOffset, yOffset,
                        wrapWidth);
                yOffset -= bounds.height * 1.5;
            } else {
                int secondsRemaining = (int) (READY_TIMEOUT_SECONDS - gameStateCounter);
                bounds = font.drawWrapped(batch, "Starting in " + secondsRemaining + " seconds.", xOffset, yOffset,
                        wrapWidth);
                yOffset -= bounds.height * 1.5;
            }
            break;
        }
        case Playing:
            break;
        case MissionComplete: {
            bounds = font.drawWrapped(batch, mission.getMissionName(), xOffset, yOffset, wrapWidth);
            yOffset -= bounds.height * 1.5;
            for (Objective objective : mission.getObjectives()) {
                String objectiveSummary = "   " + objective.getObjectiveName() + ": "
                        + (objective.getObjectiveCompletion() ? "Complete" : "Incomplete");
                bounds = font.drawWrapped(batch, objectiveSummary, xOffset, yOffset, wrapWidth);
                yOffset -= bounds.height * 1.5;
            }
            break;
        }
        }
        batch.end();
    }

    @Override
    public void render(float delta) {

        updateGameState(delta);

        // Color matches platformer pack from: http://open.commonly.cc/
        Gdx.gl.glClearColor(208f / 255, 244f / 255, 247f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderClouds();

        // Camera

        renderer.identity();

        // Render Callbacks

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

        renderMissionScreen();

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
