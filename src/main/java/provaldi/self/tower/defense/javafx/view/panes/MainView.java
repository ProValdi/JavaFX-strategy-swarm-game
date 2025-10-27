package provaldi.self.tower.defense.javafx.view.panes;

import javafx.scene.layout.BorderPane;
import provaldi.self.tower.defense.javafx.logic.GameCurrencyManager;
import provaldi.self.tower.defense.javafx.logic.GameObjects;
import provaldi.self.tower.defense.javafx.view.panes.gamefield.ContentPane;
import provaldi.self.tower.defense.javafx.view.panes.rightpanel.RightPanel;
import provaldi.self.tower.defense.javafx.view.panes.upperpanel.UpperPanel;

public class MainView extends BorderPane {

    public MainView() {
        GameCurrencyManager gameCurrencyManager = new GameCurrencyManager();
        gameCurrencyManager.setGold(100);
        GameObjects gameObjects = new GameObjects();

        // нужно разделить логику от view
        ContentPane contentPane = new ContentPane(gameCurrencyManager, gameObjects);
        MainScrollPane scrollPane = new MainScrollPane(contentPane);
        RightPanel rightPanel = new RightPanel();
        UpperPanel upperPanel = new UpperPanel(gameCurrencyManager);

        setCenter(scrollPane);
        setRight(rightPanel);
        setTop(upperPanel);
    }


    public void startGame() {

    }
}
