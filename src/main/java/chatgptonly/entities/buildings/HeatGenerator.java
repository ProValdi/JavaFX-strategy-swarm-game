package chatgptonly.entities.buildings;

import chatgptonly.terrain.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static chatgptonly.constants.WorldConstants.TILE_SIZE;

public class HeatGenerator extends Building {

    private double storedEnergy = 0.0;
    private double ratePerSecond = 2.0; // генерим энергию быстрее, чем бур руду — для демонстрации

    public HeatGenerator(int tileX, int tileY) {
        super(tileX, tileY);
    }

    @Override
    public void update(double dt, World world) {
        storedEnergy += ratePerSecond * dt;
    }

    @Override
    public void renderStatic(GraphicsContext gc) {
        double px = tileX * TILE_SIZE;
        double py = tileY * TILE_SIZE;

        // корпус генератора — красно-оранжевый
        gc.setFill(Color.FIREBRICK);
        gc.fillRect(px, py, TILE_SIZE, TILE_SIZE);

        // «тепловые ребра» (простая декорация)
        gc.setStroke(Color.ORANGE);
        gc.strokeLine(px + 4, py + 4, px + TILE_SIZE - 4, py + 4);
        gc.strokeLine(px + 4, py + TILE_SIZE - 4, px + TILE_SIZE - 4, py + TILE_SIZE - 4);
    }

    @Override
    public void renderDynamic(GraphicsContext gc) {
        // индикатор энергии над блоком
        double px = tileX * TILE_SIZE;
        double py = tileY * TILE_SIZE;

        double maxBarW = TILE_SIZE;
        double barH = 4;
        double ratio = Math.min(storedEnergy / 20.0, 1.0);

        gc.setFill(Color.BLACK);
        gc.fillRect(px, py - barH - 2, maxBarW, barH);

        gc.setFill(Color.ORANGERED);
        gc.fillRect(px, py - barH - 2, maxBarW * ratio, barH);
    }
}
