package com.beanfarmergames.spaceboat.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.beanfarmergames.spaceboat.SpaceBoatScreen;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1028;
		config.height = 720;
		new LwjglApplication(new SpaceBoatScreen(), config);
	}
}
