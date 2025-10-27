package provaldi.self.tower.defense.javafx.view.model.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import provaldi.self.tower.defense.javafx.Constants;
import provaldi.self.tower.defense.javafx.view.model.interfaces.Drawable;
import provaldi.self.tower.defense.javafx.view.model.EntityView;
import provaldi.self.tower.defense.javafx.view.model.architecture.DynamicEntity;

import java.util.concurrent.ThreadLocalRandom;

public class TestModel extends DynamicEntity implements Drawable {
    private double size;

    public TestModel(double x, double y, double size, EntityView entityView) {
        super(x, y, size, size, entityView);
        this.size = size;

        hitbox = new Rectangle(x, y, size, size);
        speedX = ThreadLocalRandom.current().nextDouble(-30, 30);
        speedY = ThreadLocalRandom.current().nextDouble(-30, 30);
    }

    public void draw(GraphicsContext gc) {
        entityView.colorizeEntity(gc);
        gc.fillRect(x, y, width, height);
        hitbox.setLayoutX(x);
        hitbox.setLayoutY(y);
    }

    @Override
    public void update() {
        if (x >= 0 && x + size < Constants.GAME_WIDTH) {
            x += speedX;
        } else {
            speedX = -speedX;
            x += speedX;
        }
        if (y >= 0 && y + size < Constants.GAME_HEIGHT) {
            y += speedY;
        } else {
            speedY = -speedY;
            y += speedY;
        }

        hitbox.setLayoutX(x);
        hitbox.setLayoutY(y);
    }

    @Override
    public Shape getBounds() {
        return hitbox;
    }

    @Override
    public void handleCollision(TestModel other) {
        speedX = -speedX;
        speedY = -speedY;
        other.speedX = -other.speedX;
        other.speedY = -other.speedY;
    }

    public boolean isClicked(double mouseX, double mouseY) {
        return hitbox.contains(mouseX, mouseY);
    }

    public boolean intersects(TestModel other) {
        return hitbox.getBoundsInParent().intersects(other.hitbox.getBoundsInParent());
    }
}
