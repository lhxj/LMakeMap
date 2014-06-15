package com.example.makemap;

import android.util.Log;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.core.LSystem;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.GameTime;

/**
 * Created by Administrator on 2014/6/14.
 */
public class Map extends DrawableScreen {

    private static final String TAG = "Map";

    private LTexture aT;
    private LTexture bT;
    private LTexture xT;
    private LTexture yT;
    private int count = 0;

    public Map() {
        LSystem.EMULATOR_BUTTIN_SCALE = 1.5f;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (!isOnLoadComplete()) {
            return;
        }
        count += 1;
        spriteBatch.draw(aT, 0 + count, 0 + count);
        spriteBatch.draw(bT, 50 + count, 50 + count, 0);
        spriteBatch.draw(xT, 50 + count, 0 + count, 30, 30, 0);
        spriteBatch.draw(yT, 0, 50);
    }

    @Override
    public void loadContent() {
        Log.d(TAG, "--loadContent method-->> on start");
        aT = new LTexture("assets/Themes/gc_gamepad_a.png");
        bT = new LTexture("assets/Themes/gc_gamepad_b.png");
        xT = new LTexture("assets/Themes/gc_gamepad_x.png");
        yT = new LTexture("assets/Themes/gc_gamepad_y.png");
        Log.d(TAG, "--loadContent method-->> on finish");
    }

    @Override
    public void unloadContent() {
        Log.d(TAG, "--unloadContent method-->> on start");
    }

    @Override
    public void pressed(LTouch lTouch) {
        Log.d(TAG, "--pressed method-->> on start");
    }

    @Override
    public void released(LTouch lTouch) {
        Log.d(TAG, "--released method-->> on start");
    }

    @Override
    public void move(LTouch lTouch) {
        Log.d(TAG, "--move method-->> on start");
    }

    @Override
    public void drag(LTouch lTouch) {
        Log.d(TAG, "--drag method-->> on start");
    }

    @Override
    public void pressed(LKey lKey) {
        Log.d(TAG, "--pressed method-->> on start");
    }

    @Override
    public void released(LKey lKey) {
        Log.d(TAG, "--released method-->> on start");
    }

    @Override
    public void update(GameTime gameTime) {
        Log.d(TAG, "--update method-->> on start");
    }
}
