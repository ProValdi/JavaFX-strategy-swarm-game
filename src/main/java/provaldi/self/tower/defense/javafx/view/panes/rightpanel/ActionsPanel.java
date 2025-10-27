package provaldi.self.tower.defense.javafx.view.panes.rightpanel;

import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import static provaldi.self.tower.defense.javafx.Constants.*;

@Getter
@Setter
public class ActionsPanel extends GridPane {

    private boolean isActionsPanelVisible = true;

    public ActionsPanel() {
        setMinWidth(ACTIONS_PANEL_WIDTH);
        Button button1 = new Button("Base");
        Button button2 = new Button("Turret");

        GridPane.setRowIndex(button1, 0);
        GridPane.setColumnIndex(button1, 1);

        GridPane.setRowIndex(button2, 1);
        GridPane.setColumnIndex(button2, 0);

        getChildren().addAll(button1, button2);

        setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
    }
}
