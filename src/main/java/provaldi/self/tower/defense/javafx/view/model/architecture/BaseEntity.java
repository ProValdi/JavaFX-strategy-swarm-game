package provaldi.self.tower.defense.javafx.view.model.architecture;

import javafx.scene.shape.Shape;
import lombok.*;
import provaldi.self.tower.defense.javafx.view.model.EntityView;

@Getter
@Setter
@ToString
@AllArgsConstructor
public abstract class BaseEntity {
    protected double x, y, width, height;
    protected EntityView entityView;
    protected Shape hitbox;
    protected int health;
    protected boolean isDead = false;

    public BaseEntity(double x, double y, double width, double height, EntityView entityView) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.entityView = entityView;
    }
}
