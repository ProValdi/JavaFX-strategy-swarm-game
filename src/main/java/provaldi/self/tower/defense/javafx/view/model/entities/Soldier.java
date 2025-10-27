//package provaldi.self.tower.defense.javafx.view.model.entities;
//
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.shape.Rectangle;
//import lombok.Data;
//import provaldi.self.tower.defense.javafx.view.model.interfaces.Drawable;
//import javafx.scene.paint.Color;
//import provaldi.self.tower.defense.javafx.view.model.EntityView;
//
//@Data
//public class Soldier implements Drawable {
//    private int x, y, size;
//    private final Color color;
//    private int dx = 0; // Изменение по оси X
//    private int dy = 0; // Изменение по оси Y
//
//    public Soldier(int x, int y, int size, Color color) {
//        this.x = x;
//        this.y = y;
//        this.size = size;
//        this.color = color;
//    }
//
//    @Override
//    public void draw(GraphicsContext gc) {
//        gc.setFill(color);
//        gc.fillRect(x, y, size, size);
//    }
//
//    @Override
//    public void update() {
//        // Двигаем квадрат
//        if (x >= 0 && x + size < 500) {
//            x += dx;
//            dx = 0;
//        }
//        if (y >= 0 && y + size < 400) {
//            y += dy;
//            dy = 0;
//        }
//    }
//
//    public Bullet shoot () {
//        Bullet bullet = new Bullet(x + size, y + size, size/8, new EntityView(Color.GREEN));
//        bullet.setSpeedX(3);
//        bullet.setSpeedY(3);
//        return bullet;
//    }
//
//    @Override
//    public Rectangle getBounds() {
//        // Возвращаем границы квадрата как прямоугольник
//        return new Rectangle(x, y, size, size);
//    }
//
//    @Override
//    public void handleCollision(Drawable other) {
//        //
//    }
//
//    public void moveUp() {
//        dy = -10;
//    }
//    public void moveDown() {
//        dy = 10;
//    }
//    public void moveLeft() {
//        dx = -10;
//    }
//    public void moveRight() {
//        dx = 10;
//    }
//}
