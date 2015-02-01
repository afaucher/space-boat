package com.beanfarmergames.spaceboat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.beanfarmergames.common.callbacks.UpdateCallback;
import com.beanfarmergames.spaceboat.boat.Boat;
import com.beanfarmergames.spaceboat.boat.BoatControl;
import com.beanfarmergames.spaceboat.net.PlayerData;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class SpaceBoatServer extends Listener implements UpdateCallback {
    
    private SpaceBoat sb;
    private Server server = new Server();
    private Map<Connection, Player> connectionToPlayer = new HashMap<Connection, Player>();

    public SpaceBoatServer(SpaceBoat sb) {
        this.sb = sb;
        Kryo kryo = server.getKryo();
        kryo.register(Vector2.class);
        kryo.register(PlayerData.class);
        //server.start();
        try {
            server.bind(54555, 54777);
        } catch (IOException e) {
            // Ignore
            throw new RuntimeException(e);
        }
        
        server.addListener(this);
        
        sb.getUpdateCallbackHandler().registerCallback(this);
    }
    
    public void received (Connection connection, Object object) {
        
        if (object instanceof Vector2) {
            Vector2 v = (Vector2)object;
            //TODO: This is not thread safe!!! boo!
            Player player = connectionToPlayer.get(connection);
            if (player == null) return;
            Boat b = player.getBoat();
            if (b == null) return;
            BoatControl bc = b.getBoatControl();
            bc.getLeft().setX(v.x);
            bc.getRight().setX(v.y);
        } else if (object instanceof PlayerData) {
            //Bind the connection to a player once the player data is received.
            PlayerData pd = (PlayerData)object;
            Player player = connectionToPlayer.get(connection);
            if (player == null) {
                player = new Player();
                player.setName(pd.getName());
                connectionToPlayer.put(connection, player);
                //FIXME: This fails because this callback is not on the render thread
                sb.addPlayer(player);
            } else {
                player.setName(pd.getName());
            }
        }
    }
    
    public void connected (Connection connection) {
    }

    /** Called when the remote end is no longer connected. There is no guarantee as to what thread will invoke this method. */
    public void disconnected (Connection connection) {
        Player player = connectionToPlayer.get(connection);
        if (player == null) return;
        sb.removePlayer(player);
        connectionToPlayer.remove(connection);
    }

    @Override
    public void updateCallback(long miliseconds) {
        try {
            server.update(0);
        } catch (IOException e) {
            Gdx.app.log("ServerUpdate", e.getMessage());
        }
    }
    
    
}
