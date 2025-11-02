package chatgptonly;

import javafx.scene.canvas.GraphicsContext;

public interface Entity {
    void update(double dt, World world);
    void render(GraphicsContext gc); // координаты уже мировые; Canvas сам сдвинут камерой
    boolean isAlive();
}
