package com.beanfarmergames.spaceboat;

public enum RenderLayer {
    BACKGROUND(0), COWS(5), PLAYER_BODY(6), SCORES(8);

    private int layer;

    RenderLayer(int layer) {
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
    }
}
