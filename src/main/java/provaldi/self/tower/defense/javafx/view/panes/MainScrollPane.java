package provaldi.self.tower.defense.javafx.view.panes;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import provaldi.self.tower.defense.javafx.view.panes.gamefield.ContentPane;

import static provaldi.self.tower.defense.javafx.Constants.GAME_HEIGHT;
import static provaldi.self.tower.defense.javafx.Constants.GAME_WIDTH;

public class MainScrollPane extends ScrollPane {

    private static final double SCALE_DELTA = 0.1;

    public MainScrollPane(ContentPane contentPane) {
        super(contentPane);
        setPrefSize(GAME_WIDTH, GAME_HEIGHT);
        setPannable(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setFocused(true); // TODO make focusable by default
        setFocusTraversable(false);

        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (KeyCode.SPACE.equals(event.getCode())) {
                if (contentPane.animationTimer != null) {
                    if (contentPane.paused) {
                        contentPane.animationTimer.start();
                        contentPane.paused = false;
                    } else {
                        contentPane.animationTimer.stop();
                        contentPane.paused = true;
                    }
                }
            }
            event.consume();
        });

        addEventFilter(ScrollEvent.SCROLL, event -> {
            System.out.println(event.getDeltaY());
            double delta = event.getDeltaY() > 0 ? SCALE_DELTA : -SCALE_DELTA;

            if (GAME_WIDTH / (contentPane.getCurrentScale() + delta) < getWidth()) {
                event.consume();
                return;
            }

            contentPane.resizeGameField(delta);

            event.consume();
        });
    }

}
