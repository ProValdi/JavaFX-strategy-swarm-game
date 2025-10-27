package provaldi.self.tower.defense.javafx.view.model.interfaces;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Shape;
import provaldi.self.tower.defense.javafx.view.model.entities.TestModel;

// Интерфейс для объектов, которые могут быть нарисованы и обновлены
public interface Drawable {

    void draw(GraphicsContext gc);

    void update();

    Shape getBounds(); // Метод для получения границ объекта

    void handleCollision(TestModel other); // Метод для обработки столкновений
}