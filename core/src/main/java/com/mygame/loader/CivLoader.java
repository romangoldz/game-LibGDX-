package com.mygame.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.model.Civilization;
import com.mygame.model.WorldMap;

public class CivLoader {
    public static void load(WorldMap map, String path) {
        FileHandle file = Gdx.files.internal(path);
        JsonValue root = new JsonReader().parse(file);

        for (JsonValue jv : root) {
            String tag = jv.getString("tag");
            String name = jv.getString("name");
            String flagPath = jv.getString("flag");
            String ideology = jv.getString("ideology");
            String religion = jv.getString("religion");
            Color color = Color.valueOf(jv.getString("color"));

            Civilization civ = new Civilization(tag, name, flagPath, ideology, religion, color);
            civ.flagTexture = new Texture(Gdx.files.internal(flagPath));

            map.civilizations.put(tag, civ);
        }
    }
}