package chatgptonly;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import static chatgptonly.PlaceMode.BARRACKS;
import static chatgptonly.PlaceMode.NONE;

public class DynamicRenderer {

    // вызывается каждый кадр
    public void redrawDynamic(GraphicsContext gc, World world, int tileSize,
                              int hoverTileX, int hoverTileY, Color hoverColor,
                              PlaceMode placeMode, Building selected) {

        // очистка слоя
        gc.clearRect(0, 0,
                gc.getCanvas().getWidth(),
                gc.getCanvas().getHeight());

        // динамические части зданий
        for (Building b : world.getBuildingMap().all()) {
            b.renderDynamic(gc, tileSize);
        }

        // 1️⃣ «призрак» блока под курсором
        if (placeMode != NONE && hoverTileX >= 0 && hoverTileY >= 0) {
            int w = 1, h = 1;
            if (placeMode == BARRACKS) { w = 2; h = 2; }

            gc.setFill(hoverColor);
            gc.fillRect(hoverTileX * tileSize, hoverTileY * tileSize, w * tileSize, h * tileSize);

            gc.setStroke(Color.WHITE);
            gc.strokeRect(hoverTileX * tileSize, hoverTileY * tileSize, w * tileSize, h * tileSize);
        }

        // 2️⃣ HUD в правом верхнем углу
        drawHud(gc, placeMode);

        if (selected instanceof Barracks) {
            drawBarracksPanel(gc);
        }
    }

    private void drawHud(GraphicsContext gc, PlaceMode mode) {
        gc.setFont(Font.font("Consolas", 18));
        gc.setFill(Color.color(0, 0, 0, 0.5));
        gc.fillRect(GameApp.SCREEN_W - 180, 10, 170, 70);

        gc.setFill(Color.WHITE);
        gc.fillText("1 - Drill", GameApp.SCREEN_W - 170, 35);
        gc.fillText("2 - HeatGen", GameApp.SCREEN_W - 170, 55);
        gc.fillText("3 - Barracks", GameApp.SCREEN_W - 170, 75);
        gc.setFill(Color.LIGHTGREEN);
        gc.fillText("Mode: " + mode, GameApp.SCREEN_W - 170, 95);
    }

    private void drawBarracksPanel(GraphicsContext gc) {
        double x = GameApp.SCREEN_W - 180;
        double y = 110;                 // сразу под HUD
        double w = 170, h = 60;

        gc.setFill(Color.color(0,0,0,0.4));
        gc.fillRect(x, y, w, h);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", 16));
        gc.fillText("доступно для найма", x + 10, y + 35);
    }
}
