package com.beanfarmergames.spaceboat.mission;

import java.util.ArrayList;
import java.util.List;

import com.beanfarmergames.spaceboat.RenderContext;
import com.beanfarmergames.spaceboat.field.Field;
import com.beanfarmergames.spaceboat.mission.objective.Objective;
import com.beanfarmergames.spaceboat.mission.objective.PlaceCowsInZoneObjective;

public class MissionOne implements Mission {
    //push into abstract mission
    private List<Objective> objectives = new ArrayList<Objective>();
    private boolean missionComplete = false;
    
    private Field field;
    
    public void loadMission(Field field) {
        this.field = field;
        
        objectives.add(new PlaceCowsInZoneObjective(MapConstants.MAP_ONE_ZONE_WATER, 3, field));
    }

    @Override
    public void updateCallback(long miliseconds) {
        
        if (!missionComplete) {
            boolean objectivesComplete = true;
            
            for (Objective o : objectives) {
                o.updateCallback(miliseconds);
                
                objectivesComplete = objectivesComplete && o.getObjectiveCompletion();
            }
            
            missionComplete = objectivesComplete;
        }
    }

    @Override
    public String getMissionName() {
        return "Splash Splash Cow Bath";
    }

    @Override
    public String getMapName() {
        return MapConstants.MAP_ONE;
    }

    @Override
    public boolean isMissionComplete() {
        return missionComplete;
    }

    @Override
    public int getRequiredNumberOfPlayers() {
        return 1;
    }

    @Override
    public List<Objective> getObjectives() {
        return objectives;
    }
    
    

}
