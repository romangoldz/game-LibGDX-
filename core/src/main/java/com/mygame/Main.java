package com.mygame;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main implements ApplicationListener {
    SpriteBatch batch;
    Texture img; 
    @Override
    public void create() {
        batch = new SpriteBatch;
        img = new Texture ("images/images1.png")
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        batch.draw(img, 100, 100, 200, 200);

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
