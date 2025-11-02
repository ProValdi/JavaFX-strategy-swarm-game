package chatgptonly;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Barracks extends Building {

    public Barracks(int tileX, int tileY) { super(tileX, tileY); }

    @Override public int getWidthTiles()  { return 2; }
    @Override public int getHeightTiles() { return 2; }

    @Override public void update(double dt, World world) { /* пока логики нет */ }

    @Override
    public void renderStatic(GraphicsContext gc, int tileSize) {
        double px = tileX * tileSize;
        double py = tileY * tileSize;
        gc.setFill(Color.DODGERBLUE);
        gc.fillRect(px, py, 2 * tileSize, 2 * tileSize);

        gc.setStroke(Color.WHITE);
        gc.strokeRect(px, py, 2 * tileSize, 2 * tileSize);
    }

    @Override
    public void renderDynamic(GraphicsContext gc, int tileSize) {
        // можно добавить подсветку выбора позже
    }
}
