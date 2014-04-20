package com.morejesuslessme.tnelsond.unmutate.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.morejesuslessme.tnelsond.unmutate.Unmutate;

public class GwtLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(480, 320);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new Unmutate();
        }

				public static void main(String[] args){
				}
}
