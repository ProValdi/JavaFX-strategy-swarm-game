package provaldi.self.tower.defense.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import provaldi.self.tower.defense.javafx.view.panes.MainView;

import static provaldi.self.tower.defense.javafx.Constants.WINDOW_WIDTH;
import static provaldi.self.tower.defense.javafx.Constants.WINDOW_HEIGHT;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();

        primaryStage.setScene(new Scene(mainView, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.setTitle("NotepadFX");

        mainView.startGame();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}