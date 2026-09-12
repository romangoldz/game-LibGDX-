package com.mygame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygame.Main;
import com.mygame.loader.MapLoader;
import com.mygame.model.Civilization;
import com.mygame.model.Province;
import com.mygame.model.WorldMap;
import com.mygame.render.MapOverlayRenderer;
import com.mygame.ui.ProvinceInfoPanel;

public class GameScreen implements Screen {

    // === Ссылки на игру ===
    private final Main game;

    // === Графика ===
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapes;

    // === Данные мира ===
    private WorldMap map;
    private ProvinceInfoPanel infoPanel;

    // === Временные объекты (не создаём в render!) ===
    private final Vector3 tmp = new Vector3();

    // === Порог, ниже которого движение = клик, выше = панорамирование ===
    private static final float TAP_THRESHOLD_PX = 10f;
    private float touchDownX, touchDownY;
    private boolean dragging;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        // --- Камера и вьюпорт ---
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(1280, 720, camera);

        // --- Батчи ---
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();

        // --- Загрузка мира ---
        map = MapLoader.load(
                "maps/world_technical.png",
                "maps/world_graphical.png",
                "maps/provinces.json",
                "maps/civilizations.json"
        );

        // --- UI ---
        infoPanel = new ProvinceInfoPanel(batch);

        // --- Позиционирование камеры по центру карты ---
        float mapW = map.graphicalTexture.getWidth();
        float mapH = map.graphicalTexture.getHeight();
        camera.position.set(mapW / 2f, mapH / 2f, 0f);

        // Начальный зум — вся карта влезает в экран
        float zoomX = mapW / viewport.getWorldWidth();
        float zoomY = mapH / viewport.getWorldHeight();
        camera.zoom = Math.max(zoomX, zoomY);
        camera.update();

        // --- Ввод: жесты + тапы ---
        setupInput();
    }

    private void setupInput() {
        // 1) Жесты: пинч-зум
        GestureDetector gestureDetector = new GestureDetector(new GestureDetector.GestureAdapter() {
            @Override
            public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
                                 Vector2 pointer1, Vector2 pointer2) {
                float initialDist = initialPointer1.dst(initialPointer2);
                float currentDist = pointer1.dst(pointer2);
                if (initialDist > 0f && currentDist > 0f) {
                    camera.zoom *= initialDist / currentDist;
                    clampZoom();
                    camera.update();
                }
                return true;
            }
        });

        // 2) Тапы и панорамирование одним пальцем
        InputAdapter touchAdapter = new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                touchDownX = screenX;
                touchDownY = screenY;
                dragging = false;
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                float dx = screenX - touchDownX;
                float dy = screenY - touchDownY;

                if (!dragging && (Math.abs(dx) > TAP_THRESHOLD_PX
                        || Math.abs(dy) > TAP_THRESHOLD_PX)) {
                    dragging = true;
                }

                if (dragging && pointer == 0) {
                    // Сдвигаем камеру в противоположную сторону от движения пальца
                    float panX = -Gdx.input.getDeltaX() * camera.zoom;
                    float panY = Gdx.input.getDeltaY() * camera.zoom;

                    camera.position.add(panX, panY, 0f);
                    clampCamera();
                    camera.update();
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (!dragging && pointer == 0) {
                    handleClick(screenX, screenY);
                }
                dragging = false;
                return true;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                // Зум колёсиком мыши (десктоп)
                camera.zoom += amountY * 0.1f;
                clampZoom();
                clampCamera();
                camera.update();
                return true;
            }
        };

        InputMultiplexer multiplexer = new InputMultiplexer(gestureDetector, touchAdapter);
        Gdx.input.setInputProcessor(multiplexer);
    }

    /** Ограничивает зум разумными пределами. */
    private void clampZoom() {
        camera.zoom = MathUtils.clamp(camera.zoom, 0.15f, 8f);
    }

    /** Не даёт камере уехать за пределы карты. */
    private void clampCamera() {
        if (map == null) return;

        float halfW = camera.viewportWidth * camera.zoom / 2f;
        float halfH = camera.viewportHeight * camera.zoom / 2f;

        float mapW = map.graphicalTexture.getWidth();
        float mapH = map.graphicalTexture.getHeight();

        // Если карта меньше экрана — центрируем, иначе ограничиваем краями
        if (mapW <= halfW * 2f) {
            camera.position.x = mapW / 2f;
        } else {
            camera.position.x = MathUtils.clamp(camera.position.x, halfW, mapW - halfW);
        }

        if (mapH <= halfH * 2f) {
            camera.position.y = mapH / 2f;
        } else {
            camera.position.y = MathUtils.clamp(camera.position.y, halfH, mapH - halfH);
        }
    }

    /** Обработка тапа: определяем провинцию и показываем панель. */
    private void handleClick(int screenX, int screenY) {
        tmp.set(screenX, screenY, 0f);
        camera.unproject(tmp);

        // В LibGDX Y растёт вверх, в Pixmap — вниз → инвертируем
        float mapX = tmp.x;
        float mapY = map.graphicalTexture.getHeight() - tmp.y;

        int provinceId = map.getProvinceIdAt(mapX, mapY);
        Gdx.app.log("Click",
                "screen=(" + screenX + "," + screenY + ") "
                        + "map=(" + mapX + "," + mapY + ") "
                        + "id=" + provinceId);

        if (provinceId == -1) {
            infoPanel.hide();
            return;
        }

        Province p = map.provinces.get(provinceId);
        if (p == null) {
            infoPanel.hide();
            return;
        }

        Civilization owner = map.civilizations.get(p.ownerTag);
        if (owner == null) {
            infoPanel.hide();
            return;
        }

        infoPanel.show(owner, p);
    }

    @Override
    public void render(float delta) {
        // --- Очистка ---
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // --- Мир ---
        batch.begin();
        // 1) графическая карта
        batch.draw(map.graphicalTexture, 0, 0);
        // 2) цветной оверлей стран
        MapOverlayRenderer.render(batch, map);
        batch.end();

        // --- UI ---
        infoPanel.render(viewport);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        clampCamera();
        camera.update();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapes != null) shapes.dispose();
        if (infoPanel != null) infoPanel.dispose();
        if (map != null) {
            if (map.graphicalTexture != null) map.graphicalTexture.dispose();
            if (map.technicalTexture != null) map.technicalTexture.dispose();
            if (map.technicalPixmap != null) map.technicalPixmap.dispose();
            for (Civilization civ : map.civilizations.values()) {
                if (civ.flagTexture != null) civ.flagTexture.dispose();
            }
        }
        MapOverlayRenderer.dispose();
    }
}