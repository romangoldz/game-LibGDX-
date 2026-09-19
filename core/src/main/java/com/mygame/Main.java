package com.mygame;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame implements ApplicationListener {

    @Override
    public void create() {
        
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        

        if (Gdx.input.isTouched()) {
            ScreenUtils.clear(0.2f, 0.1f, 0.2f, 1f);
        }
    }

    @Override
    public void pause() {
        
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void dispose() {
        
    }
}
