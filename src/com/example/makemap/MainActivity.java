package com.example.makemap;

import loon.LGame;
import loon.LSetting;
import loon.core.graphics.opengl.LTexture;

/**
 * Created by Administrator on 2014/6/14.
 */
public class MainActivity extends LGame {


    @Override
    public void onMain() {
        LTexture.ALL_LINEAR = true;
        LSetting setting = new LSetting();
        setting.width = 800;
        setting.height = 600;
        setting.fps = 60;
        setting.landscape = true;
        setting.showFPS = false;
        register(setting, Map.class);
    }

    @Override
    public void onGameResumed() {

    }

    @Override
    public void onGamePaused() {

    }
}
