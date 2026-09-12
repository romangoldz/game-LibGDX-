package com.mygame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygame.model.Civilization;
import com.mygame.model.Province;

public class ProvinceInfoPanel {

    private final SpriteBatch batch;
    private BitmapFont font;
    private Texture whitePixel;

    private Civilization currentCiv;
    private Province currentProvince;

    private static final float W = 320f;
    private static final float H = 220f;
    private static final float MARGIN = 20f;

    public ProvinceInfoPanel(SpriteBatch batch) {
        this.batch = batch;
        this.font = new BitmapFont();
        font.getData().setScale(1.4f);

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        whitePixel = new Texture(pm);
        pm.dispose();
    }

    public void show(Civilization civ, Province province) {
        this.currentCiv = civ;
        this.currentProvince = province;
    }

    public void hide() {
        currentCiv = null;
        currentProvince = null;
    }

    public void render(Viewport viewport) {
        if (currentCiv == null || currentProvince == null) return;

        float x = viewport.getWorldWidth() - W - MARGIN;
        float y = MARGIN;

        batch.begin();

        // фон панели
        batch.setColor(0f, 0f, 0f, 0.75f);
        batch.draw(whitePixel, x, y, W, H);
        batch.setColor(Color.WHITE);

        // флаг
        float flagSize = 80f;
        if (currentCiv.flagTexture != null) {
            batch.draw(new TextureRegion(currentCiv.flagTexture),
                    x + 15f, y + H - flagSize - 15f, flagSize, flagSize);
        }

        // тексты
        font.setColor(Color.WHITE);
        font.draw(batch, currentCiv.name,
                x + flagSize + 30f, y + H - 25f);

        font.getData().setScale(1.1f);
        font.draw(batch, "Тег: " + currentCiv.tag,
                x + flagSize + 30f, y + H - 55f);
        font.draw(batch, "Идеология: " + currentCiv.ideology,
                x + 15f, y + H - 130f);
        font.draw(batch, "Религия: " + currentCiv.religion,
                x + 15f, y + H - 155f);
        font.draw(batch, "Провинция: " + currentProvince.name,
                x + 15f, y + H - 180f);
        font.draw(batch, "ID: " + currentProvince.id,
                x + 15f, y + H - 205f);
        font.getData().setScale(1.4f);

        batch.end();
    }

    public void dispose() {
        if (font != null) font.dispose();
        if (whitePixel != null) whitePixel.dispose();
    }
}