package com.ygo.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ygo.game.YGO;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "YGO";
        config.width = YGO.WINDOW_WIDTH;
        config.height = YGO.WINDOW_HEIGHT;
        config.resizable = true;
        config.samples = 4;
		new LwjglApplication(new YGO(), config);
	}
}
