package provaldi.self.tower.defense.javafx.view.panes.upperpanel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import provaldi.self.tower.defense.javafx.logic.GameCurrency;
import provaldi.self.tower.defense.javafx.logic.GameCurrencyManager;

import static provaldi.self.tower.defense.javafx.Constants.STATUS_PANEL_HEIGHT;

public class StatusPanel extends HBox {

    public StatusPanel(GameCurrencyManager gameCurrencyManager) {
        setMaxHeight(STATUS_PANEL_HEIGHT);
        setPadding(new Insets(0, 50, 0, 50));
        setAlignment(Pos.CENTER_LEFT);

        Label labelGold = new Label(String.format("%s: ", GameCurrency.GOLD.getCurrencyName()));
        labelGold.setFont(Font.font(15));

        Label labelGoldResource = new Label();
        labelGoldResource.textProperty().bind(gameCurrencyManager.goldProperty().asString());
        labelGoldResource.setFont(Font.font(15));

        getChildren().addAll(labelGold, labelGoldResource);
    }
}
