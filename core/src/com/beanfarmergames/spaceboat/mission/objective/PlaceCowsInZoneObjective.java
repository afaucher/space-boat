package com.beanfarmergames.spaceboat.mission.objective;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.beanfarmergames.spaceboat.entities.GameEntity;
import com.beanfarmergames.spaceboat.entities.GameEntity.EntityType;
import com.beanfarmergames.spaceboat.field.Field;
import com.beanfarmergames.spaceboat.mission.MapConstants;

public class PlaceCowsInZoneObjective implements Objective {

    private final String zoneName;
    private final int cowCountGoal;
    private final Field field;
    private boolean complete = false;
    // To be reused for iterating over bodies
    private final Array<Body> bodies = new Array<Body>();
    private boolean objectiveCompletion = false;

    public PlaceCowsInZoneObjective(String zoneName, int cowCountGoal, Field field) {
        super();
        this.zoneName = zoneName;
        this.cowCountGoal = cowCountGoal;
        this.field = field;
    }

    @Override
    public void updateCallback(long miliseconds) {
        if (objectiveCompletion) {
            return;
        }
        Rectangle rect = getZoneFromMap(field, zoneName).getRectangle();

        World w = field.getWorld();

        // TODO: Field should just keep track
        w.getBodies(bodies);

        int cowsInZone = 0;

        for (Body b : bodies) {
            Object data = b.getUserData();
            if (data == null || !(data instanceof GameEntity)) {
                continue;
            }
            GameEntity e = (GameEntity) data;
            if (!EntityType.COW.equals(e.getEntityType())) {
                continue;
            }
            boolean inRect = rect.contains(b.getPosition());
            if (inRect) {
                cowsInZone++;
            }
        }

        if (cowsInZone >= cowCountGoal) {
            objectiveCompletion = true;
        }

    }

    private static RectangleMapObject getZoneFromMap(Field field, String zoneName) {
        Map map = field.getMap();
        MapLayer mapLayer = map.getLayers().get(MapConstants.MAP_ZONE_LAYER);
        MapObject mapObject = mapLayer.getObjects().get(zoneName);
        assert (mapObject instanceof RectangleMapObject);
        RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObject;
        return rectangleMapObject;
    }

    @Override
    public String getObjectiveName() {
        //TODO: Zone name is not the pretty name.
        return "Bring " + cowCountGoal + " cows to " + zoneName;
    }

    @Override
    public boolean getObjectiveCompletion() {
        return objectiveCompletion;
    }

}
