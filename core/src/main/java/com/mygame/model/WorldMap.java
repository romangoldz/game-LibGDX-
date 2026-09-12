package com.mygame.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

public class WorldMap {
    public Texture graphicalTexture;
    public Texture technicalTexture;
    public Pixmap technicalPixmap;

    public Map<Integer, Province> provinces = new HashMap<>();
    public Map<String, Civilization> civilizations = new HashMap<>();

    // кэш: какой стране принадлежит провинция → для быстрого рендера
    public Map<Integer, String> provinceOwners = new HashMap<>();

    /**
     * Определяет ID провинции по координатам экрана карты.
     * Возвращает -1, если пиксель "нейтральный" (0,0,0).
     */
    public int getProvinceIdAt(float mapX, float mapY) {
        int px = (int) mapX;
        int py = (int) mapY;

        if (px < 0 || py < 0 || px >= technicalPixmap.getWidth() || py >= technicalPixmap.getHeight())
            return -1;

        int color = technicalPixmap.getPixel(px, py);
        Color c = new Color(color);

        // ID = R + G*256 (можно расширить)
        int id = ((int) (c.r * 255)) + ((int) (c.g * 255) << 8);
        return id == 0 ? -1 : id;
    }

    public Civilization getOwnerOf(int provinceId) {
        String tag = provinceOwners.get(provinceId);
        return tag == null ? null : civilizations.get(tag);
    }
}