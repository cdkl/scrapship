package com.zanateh.scrapship.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.zanateh.scrapship.ScrapShipGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Scrapship";
		config.fullscreen = false;
		config.width = 1280;
		config.height = 720;
		//config.samples = 16;
		new LwjglApplication(new ScrapShipGame(), config);
	}
}
