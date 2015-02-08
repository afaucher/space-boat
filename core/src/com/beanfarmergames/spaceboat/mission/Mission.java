package com.beanfarmergames.spaceboat.mission;

import java.util.List;

import com.beanfarmergames.common.callbacks.UpdateCallback;
import com.beanfarmergames.spaceboat.field.Field;
import com.beanfarmergames.spaceboat.mission.objective.Objective;

/**
 * The mission is responsible for defining the objectives and requirements.
 * 
 * @author alex
 *
 */
public interface Mission extends UpdateCallback {

    public String getMissionName();
    public String getMapName();
    
    /**
     * Setup objectives & field for mission.
     */
    public void loadMission(Field field);
    public boolean isMissionComplete();
    
    /**
     * Get the number of players required to start the round.
     * 
     * @return
     */
    public int getRequiredNumberOfPlayers();
    
    /**
     * Get the currently available objectives.
     * 
     * @return
     */
    public List<Objective> getObjectives();
}
