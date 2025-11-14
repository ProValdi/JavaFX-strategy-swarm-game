package chatgptonly.entities.buildings;

import chatgptonly.terrain.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static chatgptonly.constants.WorldConstants.TILE_SIZE;

public class Drill extends Building {

    private double storedOre = 0.0;
    private double ratePerSecond = 1.5; // сколько руды копаем в секунду

    public Drill(int tileX, int tileY) {
        super(tileX, tileY);
    }

    @Override
    public void update(double dt, World world) {
        storedOre += ratePerSecond * dt;
        // потом можно будет передавать в склад или конвейер
    }

    @Override
    public void renderStatic(GraphicsContext gc) {
        double px = tileX * TILE_SIZE;
        double py = tileY * TILE_SIZE;

        // корпус бура
        gc.setFill(Color.GOLD);
        gc.fillRect(px, py, TILE_SIZE, TILE_SIZE);
    }

    @Override
    public void renderDynamic(GraphicsContext gc) {
        // полоска прогресса над буром
        double px = tileX * TILE_SIZE;
        double py = tileY * TILE_SIZE;

        double maxBarW = TILE_SIZE;
        double barH = 4;
        double ratio = Math.min(storedOre / 10.0, 1.0); // просто нормализация 0..1

        // фон полоски
        gc.setFill(Color.BLACK);
        gc.fillRect(px, py - barH - 2, maxBarW, barH);

        // заполнение
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(px, py - barH - 2, maxBarW * ratio, barH);
    }
}
