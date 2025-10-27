package provaldi.self.tower.defense.javafx.view.panes.gamefield;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import provaldi.self.tower.defense.javafx.logic.GameObjects;
import provaldi.self.tower.defense.javafx.view.model.EntityView;
import provaldi.self.tower.defense.javafx.view.model.entities.TestModel;
import provaldi.self.tower.defense.javafx.view.model.interfaces.Drawable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static provaldi.self.tower.defense.javafx.Constants.GAME_HEIGHT;
import static provaldi.self.tower.defense.javafx.Constants.GAME_WIDTH;

public class DynamicCanvas extends Canvas {
    double fps = 0;
    double before = 0;

    private final GameObjects gameObjects;
    private final GraphicsContext gc;

    public DynamicCanvas(GameObjects gameObjects) {
        super(GAME_WIDTH, GAME_HEIGHT);
        this.gameObjects = gameObjects;
        this.gc = getGraphicsContext2D();
    }

    public void fpsCounter(long now) {
        if (before == 0) {
            before = now;
        } else if (now - before > 1_000_000_000) {
            System.out.println(fps);
            fps = 0;
            before = now;
        }
    }

    public void drawDynamicObjects() {
        gc.clearRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        gameObjects.getDynamicObjects().forEach(dynamic ->  {
            dynamic.draw(gc);
        });
    }

    // Метод для обновления динамических объектов
    public void updateDynamicObjects() {
        // Очищаем предыдущие кадры динамического слоя
        gc.clearRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        // Рисуем пример динамических объектов
        gameObjects.getDynamicObjects().forEach(dynamic ->  {
            dynamic.update();
            dynamic.draw(gc);
        });

        fps++;
    }

    public void rescale(double scaleFactor) {
        gc.clearRect(0, 0, getWidth(), getHeight());
        //gc.save();
        gc.scale(scaleFactor, scaleFactor);

        updateDynamicObjects();

        //gc.restore();
    }
}
