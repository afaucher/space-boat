package com.beanfarmergames.spaceboat;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.beanfarmergames.spaceboat.boat.Boat;

public class RenderContextImpl implements RenderContext {
    
    private Boat boat;
    private ShapeRenderer renderer;
    private RenderLayer renderLayer;

    public Boat getBoat() {
        return boat;
    }

    public void setBoat(Boat boat) {
        this.boat = boat;
    }

    public ShapeRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(ShapeRenderer renderer) {
        this.renderer = renderer;
    }

    public void setRenderLayer(RenderLayer renderLayer) {
        this.renderLayer = renderLayer;
    }

    @Override
    public boolean renderForBoat(Boat b) {
        return boat == b;
    }

    @Override
    public ShapeRenderer getShapeRenderer() {
        return renderer;
    }

    @Override
    public RenderLayer getRenderLayer() {
        return renderLayer;
    }

}
