package com.morejesuslessme.tnelsond.unmutate.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.morejesuslessme.tnelsond.unmutate.Unmutate;

public class DesktopLauncher {
	public static void main (String[] arg) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Unmutate(), config);
	}
}
