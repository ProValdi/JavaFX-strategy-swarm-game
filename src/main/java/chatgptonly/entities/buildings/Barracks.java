package chatgptonly.entities.buildings;

import chatgptonly.terrain.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static chatgptonly.constants.WorldConstants.TILE_SIZE;

public class Barracks extends Building {

    public Barracks(int tileX, int tileY) { super(tileX, tileY); }

    @Override public int getWidthTiles()  { return 2; }
    @Override public int getHeightTiles() { return 2; }

    @Override public void update(double dt, World world) { /* пока логики нет */ }

    @Override
    public void renderStatic(GraphicsContext gc) {
        double px = tileX * TILE_SIZE;
        double py = tileY * TILE_SIZE;
        gc.setFill(Color.DODGERBLUE);
        gc.fillRect(px, py, 2 * TILE_SIZE, 2 * TILE_SIZE);

        gc.setStroke(Color.WHITE);
        gc.strokeRect(px, py, 2 * TILE_SIZE, 2 * TILE_SIZE);
    }

    @Override
    public void renderDynamic(GraphicsContext gc) {
        // можно добавить подсветку выбора позже
    }
}
