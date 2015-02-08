package com.beanfarmergames.spaceboat.controls;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.beanfarmergames.spaceboat.Player;
import com.beanfarmergames.spaceboat.SpaceBoat;
import com.beanfarmergames.spaceboat.boat.Boat;

public class SpaceBoatController implements ControllerListener {
    private static final String TAG = "SpaceBoatController";
    private SpaceBoat sb;
    
    private Map<Controller, Player> mappingToPlayer = new HashMap<Controller, Player>();

    public SpaceBoatController(SpaceBoat sb) {
        this.sb = sb;

        for (Controller controller : Controllers.getControllers()) {
            Gdx.app.log(TAG, controller.getName());
        }
        
        Controllers.addListener(this);
    }

    @Override
    public void connected(Controller controller) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void disconnected(Controller controller) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        
        Player player = mappingToPlayer.get(controller);
        if (player == null) {
            player = new Player();
            player.setName("Sheep");
            
            mappingToPlayer.put(controller, player);
            
            sb.addPlayer(player);
        }
        
        Boat boat = player.getBoat();
        if (boat == null) {
            return true;
        }
        
        Gdx.app.log(TAG, "Button: " + buttonCode);
        
        return true;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        
        
        
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        // TODO Auto-generated method stub
        Player player = mappingToPlayer.get(controller);
        if (player == null) {
            return true;
        }
        
        Boat boat = player.getBoat();
        if (boat == null) {
            return true;
        }
        
        value = Math.max(0,value);
        if (axisCode == 0 || axisCode == 1) {
            float x = controller.getAxis(0);
            float y = controller.getAxis(1);
            SpaceBoatServer.steerBoatControl(new Vector2(x,y), boat.getBoatControl());
        } else {
            Gdx.app.log(TAG, "UnusedAxis: " + axisCode + " value: " + value);
        }
        
        
        
        return true;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        // TODO Auto-generated method stub
        return true;
    }
}
