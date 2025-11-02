package chatgptonly;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
    public void renderStatic(GraphicsContext gc, int tileSize) {
        double px = tileX * tileSize;
        double py = tileY * tileSize;

        // корпус бура
        gc.setFill(Color.GOLD);
        gc.fillRect(px, py, tileSize, tileSize);
    }

    @Override
    public void renderDynamic(GraphicsContext gc, int tileSize) {
        // полоска прогресса над буром
        double px = tileX * tileSize;
        double py = tileY * tileSize;

        double maxBarW = tileSize;
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
