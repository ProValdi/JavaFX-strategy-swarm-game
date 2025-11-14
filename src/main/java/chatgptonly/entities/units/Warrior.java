package chatgptonly.entities.units;

import chatgptonly.GameApp;
import chatgptonly.terrain.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static chatgptonly.constants.WorldConstants.TILE_SIZE;

public class Warrior implements Entity {

    private double x, y;           // мировые координаты в пикселях
    private double speed = 320;    // px/sec
    private boolean selected = false;

    // цель движения (nullable): если null — стоим
    private Double targetX = null;
    private Double targetY = null;

    // визуальные параметры
    private double radius = 10;

    public Warrior(double x, double y) {
        this.x = x; this.y = y;
    }

    public void setSelected(boolean sel) { this.selected = sel; }

    public boolean containsPoint(double wx, double wy) {
        double dx = wx - x, dy = wy - y;
        return dx*dx + dy*dy <= radius*radius;
    }

    public void moveTo(double wx, double wy) {
        targetX = wx; targetY = wy;
    }

    @Override
    public void update(double dt, World world) {
        if (targetX == null || targetY == null) return;

        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.hypot(dx, dy);
        if (dist < 1e-3) { x = targetX; y = targetY; targetX = targetY = null; return; }

        double step = speed * dt;
        double nx = (step >= dist) ? targetX : x + dx / dist * step;
        double ny = (step >= dist) ? targetY : y + dy / dist * step;

        // тайл целевой позиции
        int ntx = (int)Math.floor(nx / TILE_SIZE);
        int nty = (int)Math.floor(ny / TILE_SIZE);

        if (world.isBlocked(ntx, nty)) {
            // блок — останавливаемся (простое поведение)
            targetX = targetY = null;
            return;
        }
        x = nx; y = ny;
    }

    @Override
    public void render(GraphicsContext gc) {
        // тело
        gc.setFill(Color.CORNFLOWERBLUE);
        gc.fillOval(x - radius, y - radius, radius*2, radius*2);

        // выделение
        if (selected) {
            gc.setStroke(Color.WHITE);
            gc.strokeOval(x - radius - 3, y - radius - 3, (radius+3)*2, (radius+3)*2);
        }

        // маркер цели
        if (targetX != null && targetY != null) {
            gc.setStroke(Color.YELLOW);
            gc.strokeOval(targetX - 6, targetY - 6, 12, 12);
            gc.strokeLine(targetX - 8, targetY, targetX + 8, targetY);
            gc.strokeLine(targetX, targetY - 8, targetX, targetY + 8);
        }
    }

    @Override
    public boolean isAlive() { return true; }

    // геттеры пригодятся позже
    public double getX() { return x; }
    public double getY() { return y; }
}
