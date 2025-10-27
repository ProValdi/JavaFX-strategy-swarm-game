package provaldi.self.tower.defense.javafx.view.panes.rightpanel;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import lombok.Getter;

import static provaldi.self.tower.defense.javafx.Constants.GAME_HEIGHT;

@Getter
public class RightPanel extends HBox {

    private final ActionsPanel actionsPanel = new ActionsPanel();

    public RightPanel() {
        Button expandButton = initExpandButton();

        getChildren().addAll(expandButton, actionsPanel);
    }

    private void togglePanel(ActionsPanel sidePanel) {
        sidePanel.setVisible(!sidePanel.isVisible());
        sidePanel.setManaged(sidePanel.isVisible()); // чтобы освободившееся место автоматом заполнялось центральной панелью
    }

    private Button initExpandButton() {
        Button button = new Button();
        button.setMaxWidth(10);
        button.setPrefHeight(GAME_HEIGHT);
        button.setMaxHeight(GAME_HEIGHT);
        button.setTextAlignment(TextAlignment.JUSTIFY);
        button.setOnMouseClicked(e -> togglePanel(actionsPanel));
        Label label = new Label("Expand");
        label.setRotate(-90);
        button.setGraphic(new Group(label));

        return button;
    }
}
