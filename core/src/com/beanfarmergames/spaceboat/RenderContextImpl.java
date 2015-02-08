package com.beanfarmergames.spaceboat;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.beanfarmergames.spaceboat.boat.Boat;

public class RenderContextImpl implements RenderContext {

    private ShapeRenderer renderer;
    private RenderLayer renderLayer;

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
    public ShapeRenderer getShapeRenderer() {
        return renderer;
    }

    @Override
    public RenderLayer getRenderLayer() {
        return renderLayer;
    }

}
