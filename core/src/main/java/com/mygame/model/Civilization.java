package com.mygame.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Civilization {
    public String tag;
    public String name;
    public String flagPath;
    public String ideology;
    public String religion;
    public Color color;
    public Texture flagTexture;

    public Civilization(String tag, String name, String flagPath,
                        String ideology, String religion, Color color) {
        this.tag = tag;
        this.name = name;
        this.flagPath = flagPath;
        this.ideology = ideology;
        this.religion = religion;
        this.color = color;
    }
}