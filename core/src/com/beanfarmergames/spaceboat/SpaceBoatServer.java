package com.beanfarmergames.spaceboat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.beanfarmergames.common.callbacks.UpdateCallback;
import com.beanfarmergames.spaceboat.boat.Boat;
import com.beanfarmergames.spaceboat.boat.BoatControl;
import com.beanfarmergames.spaceboat.net.ControlPad;
import com.beanfarmergames.spaceboat.net.PlayerData;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class SpaceBoatServer extends Listener implements UpdateCallback {

    private final SpaceBoat sb;
    private Server server = new Server();
    private Map<Connection, Player> connectionToPlayer = new HashMap<Connection, Player>();

    public SpaceBoatServer(SpaceBoat sb) {
        this.sb = sb;
        Kryo kryo = server.getKryo();
        kryo.register(Vector2.class);
        kryo.register(ControlPad.class);
        kryo.register(PlayerData.class);
        // server.start();
        try {
            server.bind(54555, 54777);
        } catch (IOException e) {
            // Ignore
            throw new RuntimeException(e);
        }

        server.addListener(this);

        sb.getUpdateCallbackHandler().registerCallback(this);
    }

    public void steerBoatControl(Vector2 v, BoatControl bc) {
        /**
         * 0,1 1,1 1,0
         * 
         * 0,.5 .5,.5 .5,0
         * 
         * 0,0 0,0 0,0
         */
        float x = v.x;
        float y = v.y;
        float l = x < 0 ? 1 + x : 1;
        float r = x > 0 ? 1 - x : 1;
        float scale = Math.max(0, y);
        l *= scale;
        r *= scale;

        bc.getLeft().setX(l);
        bc.getRight().setX(r);
        Gdx.app.log("Server", "Got new: " + l + "," + r);
    }

    public void received(Connection connection, Object object) {

        if (object instanceof ControlPad) {
            ControlPad cp = (ControlPad) object;
            // TODO: This is not thread safe!!! boo!
            Player player = connectionToPlayer.get(connection);
            if (player == null)
                return;
            Boat b = player.getBoat();
            if (b == null)
                return;
            BoatControl bc = b.getBoatControl();

            steerBoatControl(cp.getAxis(), bc);
            bc.getTractor().setEnabled(cp.isButtonA());
            
        } else if (object instanceof Vector2) {
            Vector2 v = (Vector2) object;
            // TODO: This is not thread safe!!! boo!
            Player player = connectionToPlayer.get(connection);
            if (player == null)
                return;
            Boat b = player.getBoat();
            if (b == null)
                return;
            BoatControl bc = b.getBoatControl();

            steerBoatControl(v, bc);
        } else if (object instanceof PlayerData) {
            // Bind the connection to a player once the player data is received.
            PlayerData pd = (PlayerData) object;
            Player player = connectionToPlayer.get(connection);
            if (player == null) {
                player = new Player();
                player.setName(pd.getName());
                connectionToPlayer.put(connection, player);
                // FIXME: This fails because this callback is not on the render
                // thread when using sync client
                sb.addPlayer(player);
            } else {
                player.setName(pd.getName());
            }
        }
    }

    public void connected(Connection connection) {
    }

    /**
     * Called when the remote end is no longer connected. There is no guarantee
     * as to what thread will invoke this method.
     */
    public void disconnected(Connection connection) {
        Player player = connectionToPlayer.get(connection);
        if (player == null)
            return;
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
