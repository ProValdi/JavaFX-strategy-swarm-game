package provaldi.self.tower.defense.javafx.view.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EntityView {
    private Color color;

    public void colorizeEntity(GraphicsContext gc) {
        gc.setFill(color);
        //gc.drawImage(); - for future
    }
}
