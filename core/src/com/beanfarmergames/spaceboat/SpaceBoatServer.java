package com.beanfarmergames.spaceboat;

import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.beanfarmergames.spaceboat.boat.Boat;
import com.beanfarmergames.spaceboat.boat.BoatControl;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class SpaceBoatServer extends Listener {
    
    private SpaceBoat sb;

    public SpaceBoatServer(SpaceBoat sb) {
        this.sb = sb;
        Server server = new Server();
        Kryo kryo = server.getKryo();
        kryo.register(Vector2.class);
        server.start();
        try {
            server.bind(54555, 54777);
        } catch (IOException e) {
            // Ignore
            throw new RuntimeException(e);
        }
        
        server.addListener(this);
    }
    
    public void received (Connection connection, Object object) {
        
        if (object instanceof Vector2) {
            Vector2 v = (Vector2)object;
            //TODO: This is not thread safe!!! boo!
            if (sb.getBoats().size() <= 0) return;
            Boat b = sb.getBoats().get(0);
            BoatControl bc = b.getBoatControl();
            bc.getLeft().setX(v.x);
            bc.getRight().setX(v.y);
        }
    }
    
    
}
