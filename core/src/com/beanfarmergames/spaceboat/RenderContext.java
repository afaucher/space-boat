package com.beanfarmergames.spaceboat;

import com.beanfarmergames.spaceboat.boat.Boat;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface RenderContext {

    public ShapeRenderer getShapeRenderer();

    public RenderLayer getRenderLayer();
}
