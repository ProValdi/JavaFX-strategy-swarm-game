//package provaldi.self.tower.defense.javafx.view.model.entities;
//
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.shape.Circle;
//import javafx.scene.shape.Shape;
//import lombok.Data;
//import provaldi.self.tower.defense.javafx.view.model.interfaces.Drawable;
//import provaldi.self.tower.defense.javafx.view.model.EntityView;
//import provaldi.self.tower.defense.javafx.view.model.architecture.DynamicEntity;
//
//@Data
//public class Bullet extends DynamicEntity implements Drawable {
//    private double size;
//    private boolean showCollisionModel = false;
//
//    public Bullet(int x, int y, double size, EntityView entityView) {
//        super(x, y, size, size, entityView);
//        this.size = size;
//    }
//
//    @Override
//    public void draw(GraphicsContext gc) {
//        entityView.colorizeEntity(gc);
//        gc.fillOval(x, y, this.size, size);
//        if (showCollisionModel) {
//            gc.rect(x, y, size, size);
//        }
//    }
//
//    @Override
//    public void update() {
//        x += speedX;
//        y += speedY;
//
//        checkWindowBounds();
//    }
//
//    private void checkWindowBounds() {
//        if (x < 0 || x + size > 500) speedX = -speedX; // Изменение направления по X
//        if (y < 0 || y + size > 400) speedY = -speedY; // Изменение направления по Y
//    }
//
//    @Override
//    public Shape getBounds() {
//        return new Circle(x, y, size);
//    }
//
//    @Override
//    public void handleCollision(Drawable other) {
//        // Простая реакция на столкновение: изменяем направление
//        isDead = true;
//    }
//
//}
