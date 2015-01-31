package com.beanfarmergames.spaceboat;

import java.io.IOException;
import java.net.InetAddress;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

public class ControlScreen implements Screen {
    private Stage stage = new Stage();
    private Table table = new Table();
    
    private final Client client;
    private float lastTickTimeSeconds = 0;
    private float resendTickDeltaSeconds = 0.05f;
    
    private Skin skin = new Skin(Gdx.files.internal("skins/uiskin.json")
            ,new TextureAtlas(Gdx.files.internal("skins/uiskin.atlas"))
        );
    
    private Touchpad pad = new Touchpad(10, skin);
    private Label debugLabel = new Label("",skin);
    
    public ControlScreen() {
        super();
        client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(Vector2.class);
        client.start();
    }

    @Override
    public void render(float delta) {
        
        //Try and discover or publish periodically
        //TODO: This code is garbage, don't look.
        if (!client.isConnected()) {
            //TODO: THIS BLOCKS THE UI
            InetAddress address = client.discoverHost(54777, 5000);
            
            if (address != null) {
                lastTickTimeSeconds = 0;
                try {
                    //TODO: SO DOES THIS
                    client.connect(100, address, 54555, 54777);
                } catch (IOException e) {
                    //TODO: THIS MIGHT BE BAD
                    //Ignore
                }
            }
        } else {
        
            float tickTimeSeconds = TimeUtils.nanoTime() / 1000000000.0f;
            
            if (tickTimeSeconds - lastTickTimeSeconds > resendTickDeltaSeconds) {
                float x = pad.getKnobPercentX();
                float y = pad.getKnobPercentY();
                
                client.sendTCP(new Vector2(x,y));
                debugLabel.setText("X:" + x + ",Y:" + y + ",T:" + tickTimeSeconds);
                
                lastTickTimeSeconds = tickTimeSeconds;
            }
        }
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        
        
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void show() {
        
        int x = Gdx.graphics.getWidth();
        int y = Gdx.graphics.getHeight();
        int padSize = Math.min(x, y);
        table.add(pad).size(padSize,padSize);
        table.add(debugLabel).size(x - padSize, padSize).row();

        table.setFillParent(true);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        dispose();
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
        stage.dispose();
        skin.dispose();
    }

}
