package com.beanfarmergames.spaceboat;

import com.badlogic.gdx.Game;

public class SpaceBoatScreen extends Game {

    @Override
    public void create() {
        setScreen(new MainMenu());
    }

}
