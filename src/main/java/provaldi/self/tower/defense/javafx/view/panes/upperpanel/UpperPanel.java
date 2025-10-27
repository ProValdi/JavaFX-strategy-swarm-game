package provaldi.self.tower.defense.javafx.view.panes.upperpanel;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import provaldi.self.tower.defense.javafx.logic.GameCurrencyManager;

import static provaldi.self.tower.defense.javafx.Constants.STATUS_PANEL_HEIGHT;

public class UpperPanel extends HBox {

    public UpperPanel(GameCurrencyManager gameCurrencyManager) {
        setMaxHeight(STATUS_PANEL_HEIGHT);

        StatusPanel statusPanel = new StatusPanel(gameCurrencyManager);
        InfoPanel infoPanel = new InfoPanel();

        HBox.setHgrow(statusPanel, Priority.ALWAYS);

        setBackground(new Background(new BackgroundFill(Color.rgb(203, 209, 229), null, null)));
        getChildren().addAll(statusPanel, infoPanel);
    }
}
