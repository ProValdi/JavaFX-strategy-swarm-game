package chatgptonly;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class GameApp extends Application {

    public static final double SCREEN_W = 1280;
    public static final double SCREEN_H = 1100;
    public static final int TILE_SIZE = 32;

    private Canvas staticCanvas;
    private Canvas dynamicCanvas;

    private GraphicsContext gcStatic;
    private GraphicsContext gcDynamic;

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

    // для dt
    private long lastTimeNs = 0;

    private PlaceMode placeMode = PlaceMode.NONE;

    // положение курсора в мире (в тайлах)
    private int hoverTileX = -1;
    private int hoverTileY = -1;

    // цвет для подсветки
    private Color hoverColor = Color.TRANSPARENT;

    private Building selectedBuilding = null; // NEW
    private Warrior selectedWarrior = null;



    // Параметры меню справа
    private static final double MENU_W = 220;  // ширина панели
    private static final double MENU_X = SCREEN_W - MENU_W;
    private static final double MENU_Y = 10;
    private static final double MENU_PAD = 10;

    // Размер ячейки сетки под иконку здания
// Должен влезть самый крупный блок (2x2)
    private static final double CELL_W = 2 * TILE_SIZE + 16;
    private static final double CELL_H = 2 * TILE_SIZE + 30; // + место под подпись
    private static final int GRID_COLS = 1; // по одному столбцу (можешь сделать 2)


    private static class MenuItem {
        final PlaceMode mode;
        final String label;
        final int wTiles, hTiles;
        MenuItem(PlaceMode mode, String label, int wTiles, int hTiles) {
            this.mode = mode; this.label = label; this.wTiles = wTiles; this.hTiles = hTiles;
        }
    }

    private final java.util.List<MenuItem> menuItems = java.util.List.of(
            new MenuItem(PlaceMode.DRILL,    "Drill (бур)",         1, 1),
            new MenuItem(PlaceMode.HEAT_GEN,     "HeatGen (генератор)", 1, 1),
            new MenuItem(PlaceMode.BARRACKS, "Barracks (бараки)",   2, 2)
    );



    @Override
    public void start(Stage stage) {
        // UI-слой (фиксированный, без масштабирования/перемещения)
        Canvas uiCanvas = new Canvas(SCREEN_W, SCREEN_H);
        GraphicsContext gcUI = uiCanvas.getGraphicsContext2D();

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

            // проверяем, можем ли ставить блок
            boolean valid = switch (placeMode) {
                case DRILL -> world.canPlaceDrill(hoverTileX, hoverTileY);
                case HEAT_GEN -> world.canPlaceHeatGenerator(hoverTileX, hoverTileY);
                case BARRACKS -> world.canPlaceBarracks(hoverTileX, hoverTileY); // NEW
                case NONE     -> false;
            };
            hoverColor = valid ? Color.color(0, 1, 0, 0.3) : Color.color(1, 0, 0, 0.3);
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
            double factor = (e.getDeltaY() > 0) ? 1.1 : 1/1.1;
            double newScale = Math.max(0.4, Math.min(3.0, cameraScale * factor));

            // мировая точка под курсором в локальных координатах worldLayer:
            var p = worldLayer.sceneToLocal(e.getSceneX(), e.getSceneY());

            // ставим пивот туда, где курсор — зум “в точку”
            scale.setPivotX(p.getX());
            scale.setPivotY(p.getY());

            cameraScale = newScale;
            applyCameraTransform();
            //needsStaticRedraw = true;
        });

        scene.setOnMouseClicked(e -> {
            double mx = e.getSceneX(), my = e.getSceneY();

            // 1) Меню справа — без изменений
            PlaceMode picked = hitTestMenu(mx, my);
            if (picked != null && e.getButton() == MouseButton.PRIMARY) {
                placeMode = picked;
                selectedBuilding = null;
                if (selectedWarrior != null) { selectedWarrior.setSelected(false); selectedWarrior = null; }
                renderUI(gcUI);
                return;
            }

            // 2) Преобразуем координаты клика в мировые
            var p = worldLayer.sceneToLocal(mx, my);
            double wx = p.getX();
            double wy = p.getY();
            int tx = (int)(wx / TILE_SIZE);
            int ty = (int)(wy / TILE_SIZE);

            if (e.getButton() == MouseButton.SECONDARY) {
                // ПКМ: приказ для юнита, если выбран
                if (selectedWarrior != null) {
                    selectedWarrior.moveTo(wx, wy);
                } else {
                    // иначе — отключаем режим строительства
                    placeMode = PlaceMode.NONE;
                    hoverTileX = hoverTileY = -1;
                    hoverColor = javafx.scene.paint.Color.TRANSPARENT;
                    renderUI(gcUI);
                }
                return;
            }

            if (e.getButton() == MouseButton.PRIMARY) {
                // ЛКМ
                if (placeMode != PlaceMode.NONE) {
                    boolean placed = switch (placeMode) {
                        case DRILL    -> world.placeDrill(tx, ty);
                        case HEAT_GEN     -> world.placeHeatGenerator(tx, ty);
                        case BARRACKS -> world.placeBarracks(tx, ty);
                        case NONE     -> false;
                    };
                    if (placed) {
                        selectedBuilding = null;
                        if (selectedWarrior != null) { selectedWarrior.setSelected(false); selectedWarrior = null; }
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
                            if (selectedWarrior != null) { selectedWarrior.setSelected(false); selectedWarrior = null; }
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
                        if (selectedWarrior != null) { selectedWarrior.setSelected(false); selectedWarrior = null; }
                    }
                }
            }
        });

//        scene.setOnMouseClicked(e -> {
//            double mx = e.getSceneX();
//            double my = e.getSceneY();
//
//            // 1) Сначала — меню справа (uiCanvas)
//            PlaceMode picked = hitTestMenu(mx, my);
//            if (picked != null && e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
//                placeMode = picked;
//                selectedBuilding = null;
//                // обновим UI сразу
//                renderUI(gcUI);
//                return; // не обрабатываем мир
//            }
//
//            if (e.getButton() == MouseButton.SECONDARY) {
//                // Правой кнопкой выключаем режим строительства
//                placeMode = PlaceMode.NONE;
//                // опционально: убрать призрак
//                hoverTileX = hoverTileY = -1;
//                hoverColor = Color.TRANSPARENT;
//                renderUI(gcUI);
//                return;
//            }
//
//            // 3) Клик по миру — только если ЛКМ и не попали в меню
//            if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
//                var p = worldLayer.sceneToLocal(mx, my);
//                int tileX = (int)(p.getX() / TILE_SIZE);
//                int tileY = (int)(p.getY() / TILE_SIZE);
//
//                boolean placed = switch (placeMode) {
//                    case DRILL    -> world.placeDrill(tileX, tileY);
//                    case HEAT_GEN -> world.placeHeatGenerator(tileX, tileY);
//                    case BARRACKS -> world.placeBarracks(tileX, tileY); // NEW
//                    case NONE     -> false;
//                };
//
//                if (placed) {
//                    selectedBuilding = null;     // строим — снимаем выбор
//                    needsStaticRedraw = true;
//                } else {
//                    // НЕ построили — пробуем выбрать постройку под курсором
//                    Building b = world.getBuildingMap().get(tileX, tileY);
//                    selectedBuilding = b;
//                    // (можно расширить на выбор любых зданий позже)
//                }
//            }
//        });


        scene.setOnMouseMoved(e -> {
            double mx = e.getSceneX(), my = e.getSceneY();

            // если курсор над меню — не показываем «призрак» на карте
            if (mx >= MENU_X && mx <= SCREEN_W && my >= MENU_Y) {
                hoverTileX = hoverTileY = -1;
                hoverColor = javafx.scene.paint.Color.TRANSPARENT;
                return;
            }

            var p = worldLayer.sceneToLocal(mx, my);
            hoverTileX = (int)(p.getX() / TILE_SIZE);
            hoverTileY = (int)(p.getY() / TILE_SIZE);

            if (placeMode == PlaceMode.NONE) {
                hoverColor = javafx.scene.paint.Color.TRANSPARENT;
                return;
            }

            boolean valid = switch (placeMode) {
                case DRILL    -> world.canPlaceDrill(hoverTileX, hoverTileY);
                case HEAT_GEN -> world.canPlaceHeatGenerator(hoverTileX, hoverTileY);
                case BARRACKS -> world.canPlaceBarracks(hoverTileX, hoverTileY);
                case NONE     -> false;
            };
            hoverColor = valid ? javafx.scene.paint.Color.color(0,1,0,0.3)
                    : javafx.scene.paint.Color.color(1,0,0,0.3);
        });


        scene.setOnMouseExited(e -> hoverTileX = hoverTileY = -1);



        // --- Геймлуп ---
        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTimeNs == 0) {
                    lastTimeNs = now;
                    return;
                }
                double dt = (now - lastTimeNs) / 1_000_000_000.0;
                lastTimeNs = now;

                update(dt);
                render();

                // TODO: удешевить
                renderUI(gcUI);
            }
        };
        loop.start();
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

        double len = Math.sqrt(dx*dx + dy*dy);
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

//        // сдвигаем Canvas-ы, чтобы симулировать камеру
//        staticCanvas.setTranslateX(-cameraX);
//        staticCanvas.setTranslateY(-cameraY);
//        dynamicCanvas.setTranslateX(-cameraX);
//        dynamicCanvas.setTranslateY(-cameraY);

//        staticCanvas.setScaleX(cameraScale);
//        staticCanvas.setScaleY(cameraScale);
//        dynamicCanvas.setScaleX(cameraScale);
//        dynamicCanvas.setScaleY(cameraScale);
    }

    private void render() {
        // 1) перерисовать статику только если надо
        if (needsStaticRedraw) {
            staticRenderer.redrawStatic(gcStatic, world, TILE_SIZE);
            needsStaticRedraw = false;
        }

        // 2) динамику — каждый кадр
        dynamicRenderer.redrawDynamic(gcDynamic, world, TILE_SIZE,
                hoverTileX, hoverTileY, hoverColor, placeMode, selectedBuilding);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private PlaceMode hitTestMenu(double mx, double my) {
        // вне панели — не меню
        if (mx < MENU_X || mx > SCREEN_W || my < MENU_Y) return null;

        // координаты внутри панели
        double x = MENU_X + 12;
        double y = MENU_Y + 40;
        int col = 0;

        for (MenuItem it : menuItems) {
            double cellX = x;
            double cellY = y;
            if (mx >= cellX && mx <= cellX + CELL_W && my >= cellY && my <= cellY + CELL_H) {
                return it.mode;
            }

            col++;
            if (col >= GRID_COLS) {
                col = 0;
                y += CELL_H + 8;
            } else {
                x += CELL_W + 8;
            }
        }
        return null;
    }

    private void renderUI(GraphicsContext gcUI) {
        // очистка
        gcUI.clearRect(0, 0, SCREEN_W, SCREEN_H);

        // панель фоном
        gcUI.setFill(javafx.scene.paint.Color.color(0,0,0,0.35));
        gcUI.fillRect(MENU_X, MENU_Y, MENU_W - MENU_PAD, SCREEN_H - 2*MENU_Y);

        // заголовок
        gcUI.setFill(javafx.scene.paint.Color.WHITE);
        gcUI.setFont(javafx.scene.text.Font.font("Consolas", 18));
        gcUI.fillText("Постройки", MENU_X + 12, MENU_Y + 26);

        // сетка
        double startY = MENU_Y + 40;
        double x = MENU_X + 12;
        double y = startY;

        int col = 0;
        for (MenuItem it : menuItems) {
            // ячейка
            gcUI.setStroke(javafx.scene.paint.Color.color(1,1,1,0.5));
            gcUI.strokeRect(x, y, CELL_W, CELL_H);

            // иконка «в натуральную величину»
            double iconW = it.wTiles * TILE_SIZE;
            double iconH = it.hTiles * TILE_SIZE;

            // центрируем иконку в ячейке
            double iconX = x + (CELL_W - iconW) / 2;
            double iconY = y + 8 + (2 * TILE_SIZE - iconH) / 2; // визуально пониже, чтобы текст влез

            // рисуем упрощённые прямоугольники цветов как в игре
            javafx.scene.paint.Color body =
                    (it.mode == PlaceMode.DRILL)    ? javafx.scene.paint.Color.GOLD :
                            (it.mode == PlaceMode.HEAT_GEN)     ? javafx.scene.paint.Color.FIREBRICK :
                                    javafx.scene.paint.Color.DODGERBLUE;

            gcUI.setFill(body);
            gcUI.fillRect(iconX, iconY, iconW, iconH);
            gcUI.setStroke(javafx.scene.paint.Color.WHITE);
            gcUI.strokeRect(iconX, iconY, iconW, iconH);

            // подпись
            gcUI.setFill(javafx.scene.paint.Color.WHITE);
            gcUI.setFont(javafx.scene.text.Font.font("Consolas", 14));
            gcUI.fillText(it.label, x + 8, y + CELL_H - 8);

            // следующий элемент
            col++;
            if (col >= GRID_COLS) {
                col = 0;
                y += CELL_H + 8;
            } else {
                x += CELL_W + 8;
            }
        }

        // статус выбранного режима
        gcUI.setFill(javafx.scene.paint.Color.LIGHTGREEN);
        gcUI.setFont(javafx.scene.text.Font.font("Consolas", 14));
        gcUI.fillText("Режим: " + placeMode, MENU_X + 12, y + 20);
    }
}
