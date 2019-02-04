package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.GameInit;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = GameInit.V_WIDTH;
		config.height = GameInit.V_HEIGHT;
		config.backgroundFPS = 60;
        config.foregroundFPS = 60;
		new LwjglApplication(new GameInit(), config);
	}
}
