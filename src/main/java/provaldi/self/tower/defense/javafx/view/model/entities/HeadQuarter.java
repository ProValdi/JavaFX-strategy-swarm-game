//package provaldi.self.tower.defense.javafx.view.model.entities;
//
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.shape.Rectangle;
//import provaldi.self.tower.defense.javafx.view.model.interfaces.Drawable;
//import provaldi.self.tower.defense.javafx.view.model.EntityView;
//import provaldi.self.tower.defense.javafx.view.model.architecture.StaticEntity;
//
//public class HeadQuarter extends StaticEntity implements Drawable {
//    private double size;
//
//    public HeadQuarter(int x, int y, double size, EntityView entityView) {
//        super(x, y, size, size, entityView);
//        this.size = size;
//    }
//
//    @Override
//    public void draw(GraphicsContext gc) {
//        entityView.colorizeEntity(gc);
//    }
//
//    @Override
//    public void update() {
//
//    }
//
//    @Override
//    public Rectangle getBounds() {
//        return new Rectangle(x, y, size, size);
//    }
//
//    @Override
//    public void handleCollision(Drawable other) {
//
//    }
//}
