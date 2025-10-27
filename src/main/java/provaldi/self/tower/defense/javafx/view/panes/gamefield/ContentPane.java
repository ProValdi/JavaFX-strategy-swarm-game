package provaldi.self.tower.defense.javafx.view.panes.gamefield;

import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import lombok.Getter;
import provaldi.self.tower.defense.javafx.logic.GameCurrencyManager;
import provaldi.self.tower.defense.javafx.logic.GameObjects;
import provaldi.self.tower.defense.javafx.view.model.entities.TestModel;

@Getter
public class ContentPane extends Pane {
    private double currentScale = 1;
    public boolean paused = false;

    private final StaticCanvas staticCanvas;
    private final DynamicCanvas dynamicCanvas;
    private final GameObjects gameObjects;

    public AnimationTimer animationTimer = null;

    public ContentPane(GameCurrencyManager gameCurrencyManager, GameObjects gameObjects) {
        staticCanvas = new StaticCanvas(gameObjects);
        dynamicCanvas = new DynamicCanvas(gameObjects);
        this.gameObjects = gameObjects;

        this.getChildren().addAll(staticCanvas, dynamicCanvas);

        dynamicCanvas.drawDynamicObjects();

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                dynamicCanvas.updateDynamicObjects();
                dynamicCanvas.fpsCounter(now);
            }
        };
        animationTimer.start();

        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            gameObjects.getDynamicObjects().forEach(
                    (item -> {
                System.out.println("item clicked: " + ((TestModel)item).isClicked(event.getX(), event.getY()));
                gameCurrencyManager.addGold(10);
            }));
        });

    }

    public void resizeGameField(double scaleFactor) {
        if (currentScale + scaleFactor > 0) {
            currentScale += scaleFactor;
            staticCanvas.rescale(1 + scaleFactor);
            dynamicCanvas.rescale(1 + scaleFactor);
        }
    }

    public void handleMouseEvent(MouseEvent event) {
        System.out.println("EventType: {" + event.getEventType().getName() + "}, Handled by: {" + this.getClass().getSimpleName() + "}");
        Object target = event.getTarget();
        while (target != null) {
            System.out.println(" - " + target.getClass().getSimpleName() + " or " + target.getClass());
            target = ((Node) target).getParent();
        }
    }
}
