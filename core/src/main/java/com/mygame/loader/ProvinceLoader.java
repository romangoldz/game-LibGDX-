package com.mygame.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.model.Province;
import com.mygame.model.WorldMap;

public class ProvinceLoader {
    public static void load(WorldMap map, String path) {
        FileHandle file = Gdx.files.internal(path);
        JsonValue root = new JsonReader().parse(file);
        JsonValue arr = root.get("provinces");

        for (JsonValue jv : arr) {
            int id = jv.getInt("id");
            String owner = jv.getString("owner");
            String name = jv.getString("name");

            Province p = new Province(id, name, owner);
            map.provinces.put(id, p);
            map.provinceOwners.put(id, owner);
        }
    }
}