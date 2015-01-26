package com.beanfarmergames.spaceboat;

import com.beanfarmergames.spaceboat.boat.Boat;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface RenderContext {
    public boolean renderForBoat(Boat b);

    public ShapeRenderer getShapeRenderer();

    public RenderLayer getRenderLayer();
}
