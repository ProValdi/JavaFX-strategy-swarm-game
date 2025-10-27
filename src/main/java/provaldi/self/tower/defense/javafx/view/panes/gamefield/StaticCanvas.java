package provaldi.self.tower.defense.javafx.view.panes.gamefield;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import provaldi.self.tower.defense.javafx.logic.GameObjects;

import static provaldi.self.tower.defense.javafx.Constants.GAME_HEIGHT;
import static provaldi.self.tower.defense.javafx.Constants.GAME_WIDTH;

public class StaticCanvas extends Canvas {
    private final GameObjects gameObjects;
    private final GraphicsContext gc;

    public StaticCanvas(GameObjects gameObjects) {
        super(GAME_WIDTH, GAME_HEIGHT);
        this.gameObjects = gameObjects;
        this.gc = getGraphicsContext2D();

        drawStaticObjects(getGraphicsContext2D());
    }

    // Метод для рисования статических объектов (вызывается один раз)
    private void drawStaticObjects(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Пример статического объекта
        gc.setFill(Color.BLUE);
        gc.fillRect(100, 100, 200, 200);
        gc.setFill(Color.GREEN);
        gc.fillRect(300, 300, 150, 150);
    }

    public void rescale(double scaleFactor) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        //gc.save();
        gc.scale(scaleFactor, scaleFactor);

        drawStaticObjects(gc);

        //gc.restore();
    }

}
