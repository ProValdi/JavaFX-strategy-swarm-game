package chatgptonly;

import chatgptonly.entities.buildings.PlaceMode;
import chatgptonly.entities.buildings.Barracks;
import chatgptonly.entities.buildings.Building;
import chatgptonly.entities.units.Warrior;
import chatgptonly.graphics.DynamicRenderer;
import chatgptonly.graphics.StaticRenderer;
import chatgptonly.logic.GameLoop;
import chatgptonly.panels.MenuPanel;
import chatgptonly.terrain.World;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.List;

import static chatgptonly.constants.WorldConstants.*;

public class GameApp extends Application {

    private Canvas staticCanvas;
    private Canvas dynamicCanvas;

    private GraphicsContext gcStatic;
    private GraphicsContext gcDynamic;
    private GraphicsContext gcUI;

    private World world;
    private StaticRenderer staticRenderer;
    private DynamicRenderer dynamicRenderer;

    private boolean needsStaticRedraw = true; // флаг "карту надо перерисовать"

    // камера (в мировых координатах)
    private double cameraX = 0;
    private double cameraY = 0;
    private double cameraSpeed = 600;
    private double cameraScale = 1.0;      // 1.0 = 100%
    private final double MIN_SCALE = 0.4;  // не даём бесконечно отдалять
    private final double MAX_SCALE = 3.0;  // не даём слишком приближать
    private final Scale scale = new Scale(1, 1, 0, 0);      // pivotX/pivotY будем двигать под курсор
    private final Translate translate = new Translate(0, 0);

    private boolean up, down, left, right;

    private PlaceMode placeMode = PlaceMode.NONE;

    // положение курсора в мире (в тайлах)
    private int hoverTileX = -1;
    private int hoverTileY = -1;

    // цвет для подсветки
    private Color hoverColor = Color.TRANSPARENT;

    private Building selectedBuilding = null; // NEW
    private Warrior selectedWarrior = null;
    MenuPanel menuPanel = new MenuPanel();

    // Параметры меню справа
    private static final double MENU_W = 220;  // ширина панели
    private static final double MENU_X = SCREEN_W - MENU_W;
    private static final double MENU_Y = 10;

    // Выделение воинов
    private final LinkedHashSet<Warrior> selectedWarriors = new LinkedHashSet<>();
    private boolean draggingSelect = false;
    private double dragStartWX, dragStartWY; // мировые коорд. начала
    private double dragNowWX, dragNowWY;   // мировые коорд. текущей точки

    private final Rectangle2D hireBtnBounds =
            new Rectangle2D(SCREEN_W - 180 + 15, 110 + 45, 140, 28);

    private void showHoverIfApplicable() {
        // проверяем, можем ли ставить блок
        boolean valid = switch (placeMode) {
            case DRILL -> world.canPlaceDrill(hoverTileX, hoverTileY);
            case HEAT_GEN -> world.canPlaceHeatGenerator(hoverTileX, hoverTileY);
            case BARRACKS -> world.canPlaceBarracks(hoverTileX, hoverTileY); // NEW
            case NONE -> false;
        };
        hoverColor = valid ? Color.color(0, 1, 0, 0.3) : Color.color(1, 0, 0, 0.3);
    }

    @Override
    public void start(Stage stage) {
        // UI-слой (фиксированный, без масштабирования/перемещения)
        Canvas uiCanvas = new Canvas(SCREEN_W, SCREEN_H);
        gcUI = uiCanvas.getGraphicsContext2D();

        // --- init world ---
        world = new World(60, 40); // карта 60 x 40 тайлов
        staticRenderer = new StaticRenderer();
        dynamicRenderer = new DynamicRenderer();

        // --- canvases ---
        staticCanvas = new Canvas(SCREEN_W, SCREEN_H);
        dynamicCanvas = new Canvas(SCREEN_W, SCREEN_H);

        gcStatic = staticCanvas.getGraphicsContext2D();
        gcDynamic = dynamicCanvas.getGraphicsContext2D();

//        // Кладём слои один над другим
//        StackPane root = new StackPane(staticCanvas, dynamicCanvas);

        Pane root = new Pane();                    // вместо StackPane
        Group worldLayer = new Group(staticCanvas, dynamicCanvas);
        worldLayer.getTransforms().setAll(scale, translate);
        root.getChildren().add(worldLayer);
        root.getChildren().add(uiCanvas);

        Scene scene = new Scene(root, SCREEN_W, SCREEN_H);
        stage.setScene(scene);
        stage.setTitle("Factory Prototype (layered)");
        stage.show();

        // --- Input: клавиатура для камеры ---
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case DIGIT1, NUMPAD1 -> placeMode = PlaceMode.DRILL;
                case DIGIT2, NUMPAD2 -> placeMode = PlaceMode.HEAT_GEN;
                case DIGIT3, NUMPAD3 -> placeMode = PlaceMode.BARRACKS;
                case W, UP -> up = true;
                case S, DOWN -> down = true;
                case A, LEFT -> left = true;
                case D, RIGHT -> right = true;
                case ESCAPE -> {
                    placeMode = PlaceMode.NONE;
                    hoverTileX = hoverTileY = -1;
                    hoverColor = Color.TRANSPARENT;
                }
            }
            showHoverIfApplicable();
        });
        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W, UP -> up = false;
                case S, DOWN -> down = false;
                case A, LEFT -> left = false;
                case D, RIGHT -> right = false;
            }
        });
        scene.setOnScroll(e -> {
            double factor = (e.getDeltaY() > 0) ? 1.1 : 1 / 1.1;
            double newScale = Math.max(0.4, Math.min(3.0, cameraScale * factor));

            // мировая точка под курсором в локальных координатах worldLayer:
            var p = worldLayer.sceneToLocal(e.getSceneX(), e.getSceneY());

            // ставим пивот туда, где курсор — зум “в точку”
            scale.setPivotX(p.getX());
            scale.setPivotY(p.getY());

            cameraScale = newScale;
            applyCameraTransform();
        });

        scene.setOnMouseClicked(e -> {
            System.out.println("scene mouse clicked");
            double mx = e.getSceneX(), my = e.getSceneY();

            // 1) Меню справа — без изменений
            PlaceMode picked = hitTestMenu(mx, my);
            if (picked != null && e.getButton() == MouseButton.PRIMARY) {
                placeMode = picked;
                //selectedBuilding = null;
                if (selectedWarrior != null) {
                    selectedWarrior.setSelected(false);
                    selectedWarrior = null;
                }
                renderUI(gcUI);
                return;
            }
            //System.out.println(selectedBuilding);

            // 2) Преобразуем координаты клика в мировые
            var p = worldLayer.sceneToLocal(mx, my);
            double wx = p.getX();
            double wy = p.getY();
            int tx = (int) (wx / TILE_SIZE);
            int ty = (int) (wy / TILE_SIZE);

            if (e.getButton() == MouseButton.SECONDARY) {

                if (!selectedWarriors.isEmpty()) {
                    for (var w : selectedWarriors) w.moveTo(wx, wy);
                    return;
                }

                // ПКМ: приказ для юнита, если выбран
                if (selectedWarrior != null) {
                    selectedWarrior.moveTo(wx, wy);
                } else {
                    // иначе — отключаем режим строительства
                    placeMode = PlaceMode.NONE;
                    hoverTileX = hoverTileY = -1;
                    hoverColor = Color.TRANSPARENT;
                    renderUI(gcUI);
                }
                return;
            }

            if (e.getButton() == MouseButton.PRIMARY) {
                // ЛКМ
                if (placeMode != PlaceMode.NONE) {
                    boolean placed = switch (placeMode) {
                        case DRILL -> world.placeDrill(tx, ty);
                        case HEAT_GEN -> world.placeHeatGenerator(tx, ty);
                        case BARRACKS -> world.placeBarracks(tx, ty);
                        case NONE -> false;
                    };
                    if (placed) {
                        selectedBuilding = null;
                        if (selectedWarrior != null) {
                            selectedWarrior.setSelected(false);
                            selectedWarrior = null;
                        }
                        needsStaticRedraw = true;
                    } else {
                        // Не удалось построить — пробуем выбрать воина
                        Warrior hit = world.findWarriorAt(wx, wy);
                        if (hit != null) {
                            // снять прошлое выделение
                            if (selectedWarrior != null) selectedWarrior.setSelected(false);
                            selectedWarrior = hit;
                            selectedWarrior.setSelected(true);
                            selectedBuilding = null; // панель бараков не нужна
                        } else {
                            // если по воину не попали — оставим выбор построек (как раньше)
                            Building b = world.getBuildingMap().get(tx, ty);
                            selectedBuilding = (b != null) ? b : null;
                            if (selectedWarrior != null) {
                                selectedWarrior.setSelected(false);
                                selectedWarrior = null;
                            }
                        }
                    }
                    return;
                } else {
                    // Режим строительства выключен → чистый режим выбора
                    Warrior hit = world.findWarriorAt(wx, wy);
                    if (hit != null) {
                        if (selectedWarrior != null) selectedWarrior.setSelected(false);
                        selectedWarrior = hit;
                        selectedWarrior.setSelected(true);
                        selectedBuilding = null;
                    } else {
                        Building b = world.getBuildingMap().get(tx, ty);
                        selectedBuilding = (b != null) ? b : null;
                        if (selectedWarrior != null) {
                            selectedWarrior.setSelected(false);
                            selectedWarrior = null;
                        }
                    }
                }
            }
        });

        // Нажатие ЛКМ — старт рамки, если не в режиме строительства и не по меню
        scene.setOnMousePressed(e -> {
            System.out.println("scene mouse pressed");
            double mx = e.getSceneX(), my = e.getSceneY();

            // 0) Кнопка "Нанять" всегда обрабатывается первой
            if (e.getButton() == MouseButton.PRIMARY
                    && selectedBuilding instanceof Barracks
                    && hireBtnBounds.contains(mx, my)) {

                world.spawnWarriorNear((Barracks) selectedBuilding);
                // фокус оставляем на бараках (панель остаётся открытой)
                renderUI(gcUI);
                e.consume();
                return;
            }

            // 1) Рамка выделения — только если ЛКМ, не по меню, и placeMode == NONE
            if (e.getButton() == MouseButton.PRIMARY) {
                if (placeMode == PlaceMode.NONE && !(mx >= MENU_X && mx <= SCREEN_W && my >= MENU_Y)) {
                    var p = worldLayer.sceneToLocal(mx, my);
                    draggingSelect = true;
                    dragStartWX = dragNowWX = p.getX();
                    dragStartWY = dragNowWY = p.getY();
                    // не трогаем focusedBuilding здесь
                }
            }
        });

        scene.setOnMouseDragged(e -> {
            if (!draggingSelect) return;
            var p = worldLayer.sceneToLocal(e.getSceneX(), e.getSceneY());
            dragNowWX = p.getX();
            dragNowWY = p.getY();
        });

        scene.setOnMouseReleased(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            if (!draggingSelect) return;
            draggingSelect = false;

            double x1 = Math.min(dragStartWX, dragNowWX);
            double y1 = Math.min(dragStartWY, dragNowWY);
            double x2 = Math.max(dragStartWX, dragNowWX);
            double y2 = Math.max(dragStartWY, dragNowWY);

            // Тап без драгга — пороговое окно 6 пикселей: считаем как одиночный клик (снимем/поставим выбор)
            boolean tiny = (Math.hypot(x2 - x1, y2 - y1) < 6);

            // очистим прошлый выбор
            for (var w : selectedWarriors) w.setSelected(false);
            selectedWarriors.clear();

            if (tiny) {
                // одиночный выбор воина под курсором (если есть)
                Warrior hit = world.findWarriorAt(dragNowWX, dragNowWY);
                if (hit != null) {
                    hit.setSelected(true);
                    selectedWarriors.add(hit);
                } else {
                    // можно тут же попробовать выбрать здание, если нужно
                    int tx = (int) (dragNowWX / TILE_SIZE), ty = (int) (dragNowWY / TILE_SIZE);
                    selectedBuilding = world.getBuildingMap().get(tx, ty);
                }
            } else {
                // прямоугольник — мультивыделение
                for (var e1 : world.getDynamicEntities()) {
                    if (e1 instanceof Warrior w) {
                        double wx = w.getX(), wy = w.getY();
                        if (wx >= x1 && wx <= x2 && wy >= y1 && wy <= y2) {
                            w.setSelected(true);
                            selectedWarriors.add(w);
                        }
                    }
                }
                selectedBuilding = null;
            }
        });

        scene.setOnMouseMoved(e -> {
            double mx = e.getSceneX(), my = e.getSceneY();

            // если курсор над меню — не показываем «призрак» на карте
            if (mx >= MENU_X && mx <= SCREEN_W && my >= MENU_Y) {
                hoverTileX = hoverTileY = -1;
                hoverColor = Color.TRANSPARENT;
                return;
            }

            var p = worldLayer.sceneToLocal(mx, my);
            hoverTileX = (int) (p.getX() / TILE_SIZE);
            hoverTileY = (int) (p.getY() / TILE_SIZE);

            if (placeMode == PlaceMode.NONE) {
                hoverColor = Color.TRANSPARENT;
                return;
            }
            showHoverIfApplicable();
        });


        scene.setOnMouseExited(e -> hoverTileX = hoverTileY = -1);

        GameLoop gameloop = new GameLoop();
        AnimationTimer loop = gameloop.getMainLoop(dt -> {
            update(dt);
            render();

            // TODO: удешевить
            renderUI(gcUI);
            return null;
        });
        loop.start();
    }

    private void pickEntityOrBuilding(double wx, double wy, int tx, int ty) {
        // мультивыбор уже обрабатывается в MouseReleased, это — одиночный клик
        Warrior hit = world.findWarriorAt(wx, wy);
        if (hit != null) {
            // сбрасываем выделение зданий, фокус не нужен
            selectedBuilding = null;
            for (var w : selectedWarriors) w.setSelected(false);
            selectedWarriors.clear();
            hit.setSelected(true);
            selectedWarriors.add(hit);
            renderUI(gcUI);
            return;
        }
        Building b = world.getBuildingMap().get(tx, ty);
        selectedBuilding = b; // может быть null — тогда панель исчезнет
        // юнитов снимаем
        for (var w : selectedWarriors) w.setSelected(false);
        selectedWarriors.clear();
        renderUI(gcUI);
    }

    private void applyCameraTransform() {
        scale.setX(cameraScale);
        scale.setY(cameraScale);
        translate.setX(-cameraX);
        translate.setY(-cameraY);
    }

    private void update(double dt) {
        // камера
        double dx = 0;
        double dy = 0;
        if (up) dy -= 1;
        if (down) dy += 1;
        if (left) dx -= 1;
        if (right) dx += 1;

        double len = Math.sqrt(dx * dx + dy * dy);
        if (len > 0) {
            dx /= len;
            dy /= len;
        }

        double moveSpeedWorld = cameraSpeed / cameraScale; // приятнее ощущается
        cameraX += dx * moveSpeedWorld * dt;
        cameraY += dy * moveSpeedWorld * dt;
        applyCameraTransform();

        // обновляем модель мира
        world.update(dt);
    }

    private void render() {
        // 1) перерисовать статику только если надо
        if (needsStaticRedraw) {
            staticRenderer.redrawStatic(gcStatic, world, TILE_SIZE);
            needsStaticRedraw = false;
        }

        // 2) динамику — каждый кадр
        dynamicRenderer.redrawDynamic(gcDynamic, world,
                hoverTileX, hoverTileY, hoverColor, placeMode, selectedBuilding,
                draggingSelect, dragStartWX, dragStartWY, dragNowWX, dragNowWY
        );
    }

    public static void main(String[] args) {
        launch(args);
    }

    private PlaceMode hitTestMenu(double mx, double my) {
        return menuPanel.hitTestMenu(mx, my);
    }

    private void renderUI(GraphicsContext gcUI) {
        menuPanel.renderUI(gcUI, placeMode);
    }
}
