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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.beanfarmergames.spaceboat.boat.Boat;
import com.beanfarmergames.spaceboat.field.Field;

public class SpaceBoat implements Screen, InputProcessor {
    private OrthographicCamera camera = null;
    private SpriteBatch batch = null;
    private Texture[] img = new Texture[3];
    private Vector2 clouds[];
    private ShapeRenderer renderer = null;

    private Field field;
    private RenderContextImpl renderContext = new RenderContextImpl();
    private List<Boat> boats = new ArrayList<Boat>();
    
    private Vector2 spawn = null;
    private Random rand = new Random();
    
    public List<Boat> getBoats() {
        return boats;
    }
    
    private static final float CLOUD_OFFSCREEN_DIST = 200;

    public SpaceBoat() {
        camera = new OrthographicCamera();
        int x = Gdx.app.getGraphics().getWidth();
        int y = Gdx.app.getGraphics().getHeight();
        camera.setToOrtho(false, x, y);

        renderer = new ShapeRenderer(500);

        Gdx.input.setInputProcessor(this);

        batch = new SpriteBatch();
        buildClouds(x, y);

        field = new Field();
        field.resetLevel();

        spawn = new Vector2(x / 2, y / 2);
        boats.add(new Boat(field, spawn));

       
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
        //Sort so the largest y is first so we render front to back
        Arrays.sort(clouds, new Comparator<Vector2>() {
            @Override
            public int compare(Vector2 arg0, Vector2 arg1) {
                return -Float.compare(arg0.y,arg1.y);
            }
        });
    }

    @Override
    public void render(float delta) {
        float tickTimeSeconds = Gdx.graphics.getDeltaTime();
        long tickTimeMiliseconds = (long) (tickTimeSeconds * 1000);
        field.updateCallback(tickTimeMiliseconds);

        //Color matches platformer pack from: http://open.commonly.cc/
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

        /*
         * Matrix4 matrix = new Matrix4(); int width = Gdx.graphics.getWidth();
         * int height = Gdx.graphics.getHeight(); matrix.setToOrtho2D(0, 0,
         * width, height); renderer.setProjectionMatrix(matrix);
         */
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
            
            batch.draw(img[i%img.length], pos.x, pos.y);
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
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }
}
