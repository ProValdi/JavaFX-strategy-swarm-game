package chatgptonly.graphics;

import chatgptonly.GameApp;
import chatgptonly.entities.buildings.Building;
import chatgptonly.terrain.TileMap;
import chatgptonly.terrain.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static chatgptonly.constants.WorldConstants.SCREEN_W;
import static chatgptonly.constants.WorldConstants.TILE_SIZE;

public class StaticRenderer {

    // вызывается только при изменении статики
    public void redrawStatic(GraphicsContext gc, World world, int tileSize) {
        // очистка всего слоя
        gc.clearRect(0, 0,
                gc.getCanvas().getWidth(),
                gc.getCanvas().getHeight());

        // 1. Рисуем землю (TileMap)
        TileMap map = world.getTileMap();
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                int t = map.getTile(x, y);

                Color c = switch (t) {
                    case TileMap.TILE_WALL -> Color.DARKGRAY;
                    case TileMap.TILE_ORE  -> Color.SIENNA;
                    case TileMap.TILE_LAVA -> Color.ORANGE;
                    default -> Color.DARKGREEN;
                };

                double px = x * tileSize;
                double py = y * tileSize;
                gc.setFill(c);
                gc.fillRect(px, py, tileSize, tileSize);

                gc.setStroke(Color.GREY);
                gc.strokeRect(px, py, tileSize, tileSize);
            }
        }

        gc.setStroke(Color.YELLOW);
        gc.strokeRect(SCREEN_W - 180 + 15, 110 + 45, 140, 28);


        // 2. Рисуем статические части зданий
        for (Building b : world.getBuildingMap().all()) {
            b.renderStatic(gc);
        }
    }
}
