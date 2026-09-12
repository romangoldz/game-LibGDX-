package com.mygame.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.mygame.model.WorldMap;

public class MapLoader {
    public static WorldMap load(String technicalPath, String graphicalPath,
                                String provincesJson, String civsJson) {
        WorldMap map = new WorldMap();

        map.technicalPixmap = new Pixmap(Gdx.files.internal(technicalPath));
        // Pixmap в LibGDX идёт сверху-вниз; для Texture можно оставить так
        map.technicalTexture = new Texture(map.technicalPixmap);
        map.graphicalTexture = new Texture(Gdx.files.internal(graphicalPath));

        CivLoader.load(map, civsJson);
        ProvinceLoader.load(map, provincesJson);

        return map;
    }
}