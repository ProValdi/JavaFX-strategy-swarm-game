package chatgptonly;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
    public void renderStatic(GraphicsContext gc, int tileSize) {
        double px = tileX * tileSize;
        double py = tileY * tileSize;

        // корпус генератора — красно-оранжевый
        gc.setFill(Color.FIREBRICK);
        gc.fillRect(px, py, tileSize, tileSize);

        // «тепловые ребра» (простая декорация)
        gc.setStroke(Color.ORANGE);
        gc.strokeLine(px+4, py+4, px+tileSize-4, py+4);
        gc.strokeLine(px+4, py+tileSize-4, px+tileSize-4, py+tileSize-4);
    }

    @Override
    public void renderDynamic(GraphicsContext gc, int tileSize) {
        // индикатор энергии над блоком
        double px = tileX * tileSize;
        double py = tileY * tileSize;

        double maxBarW = tileSize;
        double barH = 4;
        double ratio = Math.min(storedEnergy / 20.0, 1.0);

        gc.setFill(Color.BLACK);
        gc.fillRect(px, py - barH - 2, maxBarW, barH);

        gc.setFill(Color.ORANGERED);
        gc.fillRect(px, py - barH - 2, maxBarW * ratio, barH);
    }
}
