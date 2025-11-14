package chatgptonly.panels;

import chatgptonly.GameApp;
import chatgptonly.entities.buildings.PlaceMode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.List;
import static chatgptonly.constants.WorldConstants.*;

public class MenuPanel extends RightPanel {

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
            this.mode = mode;
            this.label = label;
            this.wTiles = wTiles;
            this.hTiles = hTiles;
        }
    }

    private final List<MenuItem> menuItems = List.of(
            new MenuItem(PlaceMode.DRILL, "Drill (бур)", 1, 1),
            new MenuItem(PlaceMode.HEAT_GEN, "HeatGen (генератор)", 1, 1),
            new MenuItem(PlaceMode.BARRACKS, "Barracks (бараки)", 2, 2)
    );

    public void renderUI(GraphicsContext gcUI, PlaceMode placeMode) {
        // очистка
        gcUI.clearRect(0, 0, SCREEN_W, SCREEN_H);

        // панель фоном
        gcUI.setFill(Color.color(0, 0, 0, 0.6));
        gcUI.fillRect(MENU_X, MENU_Y, MENU_W - MENU_PAD, MENU_H);

        // заголовок
        gcUI.setFill(Color.WHITE);
        gcUI.setFont(Font.font("Consolas", 18));
        gcUI.fillText("Постройки", MENU_X + 12, MENU_Y + 26);

        // сетка
        double startY = MENU_Y + 40;
        double x = MENU_X + 12;
        double y = startY;

        int col = 0;
        for (MenuItem it : menuItems) {
            // ячейка
            gcUI.setStroke(Color.color(1, 1, 1, 0.5));
            gcUI.strokeRect(x, y, CELL_W, CELL_H);

            // иконка «в натуральную величину»
            double iconW = it.wTiles * TILE_SIZE;
            double iconH = it.hTiles * TILE_SIZE;

            // центрируем иконку в ячейке
            double iconX = x + (CELL_W - iconW) / 2;
            double iconY = y + 8 + (2 * TILE_SIZE - iconH) / 2; // визуально пониже, чтобы текст влез

            // рисуем упрощённые прямоугольники цветов как в игре
            Color body =
                    (it.mode == PlaceMode.DRILL) ? Color.GOLD :
                            (it.mode == PlaceMode.HEAT_GEN) ? Color.FIREBRICK :
                                    Color.DODGERBLUE;

            gcUI.setFill(body);
            gcUI.fillRect(iconX, iconY, iconW, iconH);
            gcUI.setStroke(Color.WHITE);
            gcUI.strokeRect(iconX, iconY, iconW, iconH);

            // подпись
            gcUI.setFill(Color.WHITE);
            gcUI.setFont(Font.font("Consolas", 14));
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
        gcUI.setFill(Color.LIGHTGREEN);
        gcUI.setFont(Font.font("Consolas", 14));
        gcUI.fillText("Режим: " + placeMode, MENU_X + 12, y + 20);
    }

    public PlaceMode hitTestMenu(double mx, double my) {
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
}
