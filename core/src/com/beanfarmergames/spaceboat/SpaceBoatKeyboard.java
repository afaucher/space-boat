package com.beanfarmergames.spaceboat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.beanfarmergames.spaceboat.boat.Boat;
import com.beanfarmergames.spaceboat.debug.DebugSettings;

public class SpaceBoatKeyboard implements InputProcessor {
    
    private final SpaceBoat sb;
    
    private Map<Integer, Player> mappingToPlayer = new HashMap<Integer, Player>();
    
    class KeyBinding {
        public final int keycodeLeft;
        public final int keycodeRight;
        public final int keycodeStart;
        
        public KeyBinding(int keycodeLeft, int keycodeRight, int keycodeStart) {
            super();
            this.keycodeLeft = keycodeLeft;
            this.keycodeRight = keycodeRight;
            this.keycodeStart = keycodeStart;
        }
        
        public boolean matches(int keycode) {
            return (keycode == keycodeLeft)
                    || (keycode == keycodeRight)
                    || (keycode == keycodeStart);
        }
    }
    
    private List<KeyBinding> bindings = new ArrayList<KeyBinding>();

    public SpaceBoatKeyboard(SpaceBoat sb) {
        this.sb = sb;
        
        Gdx.input.setInputProcessor(this);
        
        bindings.add(new KeyBinding(Input.Keys.Q, Input.Keys.W, Input.Keys.R));
        bindings.add(new KeyBinding(Input.Keys.I, Input.Keys.O, Input.Keys.P));
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        
        switch (keycode) {
        case Input.Keys.F1:
            DebugSettings.DEBUG_DRAW = DebugSettings.DEBUG_DRAW;
            return true;
        }
        
        for (int i = 0; i < bindings.size(); i++) {
            KeyBinding binding = bindings.get(i);
            if (!binding.matches(keycode)) {
                continue;
            }

            //Check for player
            Player player = mappingToPlayer.get(i);
            if (keycode == binding.keycodeStart) {
                if (player == null) {
                    player = new Player();
                    player.setName("Goat");
                    
                    mappingToPlayer.put(i, player);
                    
                    sb.addPlayer(player);
                    break;
                }
            }
            if (player == null) {
                break;
            }
            
            Boat boat = player.getBoat();
            if (boat == null) {
                break;
            }
            
            if (binding.keycodeLeft == keycode) {
                boat.getBoatControl().getLeft().setX(1);
            } else if (binding.keycodeRight == keycode) {
                boat.getBoatControl().getRight().setX(1);
            } else if (keycode == binding.keycodeStart) {
                //This should actually 'ready' the player to be spawned
                sb.spawn(boat);
            }
            
            break;
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        
        for (int i = 0; i < bindings.size(); i++) {
            KeyBinding binding = bindings.get(i);
            if (!binding.matches(keycode)) {
                continue;
            }

            //Lookup player
            Player player = mappingToPlayer.get(i);
            if (player == null) {
                break;
            }
            
            //Lookup Boat
            Boat boat = player.getBoat();
            if (boat == null) {
                break;
            }
            
            //Game Controls
            if (binding.keycodeLeft == keycode) {
                boat.getBoatControl().getLeft().setX(0);
            } else if (binding.keycodeRight == keycode) {
                boat.getBoatControl().getRight().setX(0);
            }
            
            break;
        }

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }

}
