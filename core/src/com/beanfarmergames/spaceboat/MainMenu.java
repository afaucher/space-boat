package com.beanfarmergames.spaceboat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.beanfarmergames.spaceboat.controls.SpaceBoatKeyboard;
import com.beanfarmergames.spaceboat.controls.SpaceBoatServer;

public class MainMenu implements Screen {
    private Stage stage = new Stage();
    private Table table = new Table();

    private Skin skin = new Skin(Gdx.files.internal("skins/uiskin.json")
            ,new TextureAtlas(Gdx.files.internal("skins/uiskin.atlas"))
        );

    private TextButton buttonPlay = new TextButton("Play", skin),
            buttonJoin = new TextButton("Join", skin),
        buttonExit = new TextButton("Exit", skin);

    @Override
    public void render(float delta) {
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
        //The elements are displayed in the order you add them.
        //The first appear on top, the last at the bottom.
        table.add(buttonPlay).size(150,60).padBottom(20).row();
        table.add(buttonJoin).size(150,60).padBottom(20).row();
        table.add(buttonExit).size(150,60).padBottom(20).row();

        table.setFillParent(true);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
        
        buttonPlay.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                SpaceBoat sb = new SpaceBoat();
                SpaceBoatServer sbs = new SpaceBoatServer(sb);
                SpaceBoatKeyboard sbk = new SpaceBoatKeyboard(sb);
                ((Game)Gdx.app.getApplicationListener()).setScreen(sb);
            }
        });
        
        buttonJoin.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new ControlScreen());
            }
        });
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
