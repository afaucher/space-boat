package com.beanfarmergames.spaceboat;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.beanfarmergames.spaceboat.boat.Boat;
import com.beanfarmergames.spaceboat.field.Field;

public class SpaceBoat extends ApplicationAdapter implements InputProcessor {
    private OrthographicCamera camera = null;
    private SpriteBatch batch = null;
    private Texture img = null;
    private ShapeRenderer renderer = null;

    private Field field;
    private RenderContextImpl renderContext = new RenderContextImpl();

    @Override
    public void create() {
        camera = new OrthographicCamera();
        int x = Gdx.app.getGraphics().getWidth();
        int y = Gdx.app.getGraphics().getHeight();
        camera.setToOrtho(false, x, y);

        renderer = new ShapeRenderer(500);

        Gdx.input.setInputProcessor(this);

        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

        field = new Field();
        field.resetLevel();

        Vector2 spawn = new Vector2(x / 2, y / 2);
        new Boat(field, spawn);
    }

    @Override
    public void render() {
        float tickTimeSeconds = Gdx.graphics.getDeltaTime();
        long tickTimeMiliseconds = (long) (tickTimeSeconds * 1000);
        field.updateCallback(tickTimeMiliseconds);

        Gdx.gl.glClearColor(135f / 255, 206f / 255, 250f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();

        // Camera

        // Camera
        // Vector2 cameraPosition = new Vector2(0,0); //TODO

        // camera.position.set(cameraPosition, 0);
        // camera.update();

        renderer.identity();
        // renderer.setProjectionMatrix(camera.combined);

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
}
