package provaldi.self.tower.defense.javafx.view.model.architecture;

import lombok.*;
import provaldi.self.tower.defense.javafx.view.model.EntityView;

@Getter
@Setter
public abstract class DynamicEntity extends BaseEntity {
    protected double speedX;
    protected double speedY;

    public DynamicEntity(double x, double y, double width, double height, EntityView entityView) {
        super(x, y, width, height, entityView);
    }
}
