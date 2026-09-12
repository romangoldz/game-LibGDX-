package com.mygame.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.model.Civilization;
import com.mygame.model.WorldMap;

public class MapOverlayRenderer {

    public static final float OVERLAY_ALPHA = 0.45f;

    private static Texture overlayTexture;
    private static int lastOwnersHash = 0;
    private static boolean dirty = true;

    public static void markDirty() {
        dirty = true;
    }

    /** Вызывается из GameScreen внутри batch.begin()/end(). */
    public static void render(SpriteBatch batch, WorldMap map) {
        if (map == null || map.technicalPixmap == null) return;

        int currentHash = map.provinceOwners.hashCode();
        if (dirty || overlayTexture == null || currentHash != lastOwnersHash) {
            rebuildOverlay(map);
            lastOwnersHash = currentHash;
            dirty = false;
        }

        if (overlayTexture != null) {
            batch.draw(overlayTexture, 0, 0);
        }
    }

    private static void rebuildOverlay(WorldMap map) {
        Pixmap tech = map.technicalPixmap;
        int w = tech.getWidth();
        int h = tech.getHeight();

        Pixmap overlay = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        overlay.setBlending(Pixmap.Blending.None);
        overlay.setColor(0f, 0f, 0f, 0f);
        overlay.fill();

        Color c = new Color();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = tech.getPixel(x, y);
                Color.rgba8888ToColor(c, pixel);

                int r = (int) (c.r * 255f + 0.5f);
                int g = (int) (c.g * 255f + 0.5f);
                int id = r + (g << 8);

                if (id == 0) continue;

                String tag = map.provinceOwners.get(id);
                if (tag == null) continue;

                Civilization civ = map.civilizations.get(tag);
                if (civ == null) continue;

                overlay.setColor(civ.color.r, civ.color.g, civ.color.b, OVERLAY_ALPHA);
                overlay.drawPixel(x, y);
            }
        }

        if (overlayTexture != null) {
            overlayTexture.dispose();
        }
        overlayTexture = new Texture(overlay);
        overlayTexture.setFilter(Texture.TextureFilter.Nearest,
                                 Texture.TextureFilter.Nearest);

        overlay.dispose();
    }

    public static void dispose() {
        if (overlayTexture != null) {
            overlayTexture.dispose();
            overlayTexture = null;
        }
    }
}