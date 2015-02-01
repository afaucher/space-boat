package com.beanfarmergames.spaceboat;

import java.io.IOException;
import java.net.InetAddress;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.TimeUtils;
import com.beanfarmergames.spaceboat.net.PlayerData;
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
    private Button[] buttons = new Button[]{
        new Button(skin),
        new Button(skin),
        new Button(skin),
    };
    private PlayerData playerData = new PlayerData();
    
    public ControlScreen() {
        super();
        client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(Vector2.class);
        kryo.register(PlayerData.class);
        //client.start();
        playerData.setName("Ducky");
    }

    @Override
    public void render(float delta) {
        
        try {
            client.update(0);
        } catch (IOException e) {
            Gdx.app.log("Client", e.getMessage());
        }
        
        //Try and discover or publish periodically
        //TODO: This code is garbage, don't look.
        if (!client.isConnected()) {
            //TODO: THIS BLOCKS THE UI
            InetAddress address = client.discoverHost(54777, 100);
            
            if (address != null) {
                lastTickTimeSeconds = 0;
                try {
                    //TODO: SO DOES THIS
                    client.connect(100, address, 54555, 54777);
                    client.sendTCP(playerData);
                } catch (IOException e) {
                    //TODO: THIS MIGHT BE BAD
                    Gdx.app.log("Client", e.getMessage());
                }
            }
        } else {
        
            float tickTimeSeconds = TimeUtils.nanoTime() / 1000000000.0f;
            
            if (tickTimeSeconds - lastTickTimeSeconds > resendTickDeltaSeconds) {
                float x = pad.getKnobPercentX();
                float y = pad.getKnobPercentY();
                
                client.sendTCP(new Vector2(x,y));
                String labelText = "Connected: " + client.isConnected();
                labelText += "\n Ping: " + client.getReturnTripTime();
                debugLabel.setText(labelText);
                
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
        int padPadding = 50;
        int padSize = Math.min(x, y) - padPadding;
        int buttonSize = 30;
        
        table.add(pad).size(padSize,padSize);
        table.add(buttons[0]).size(buttonSize);
        table.add(buttons[1]).size(buttonSize);
        table.add(buttons[2]).size(buttonSize).row();
        table.add(debugLabel).size(padSize, padPadding).row();

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
