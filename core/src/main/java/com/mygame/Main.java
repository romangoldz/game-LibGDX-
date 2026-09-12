package com.mygame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.mygame.screen.GameScreen;

public class Main extends Game {
    public AssetManager assets;

    @Override
    public void create() {
        assets = new AssetManager();
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        assets.dispose();
        super.dispose();
    }
}