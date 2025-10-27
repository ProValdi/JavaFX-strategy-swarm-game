package provaldi.self.tower.defense.javafx.example;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SlideInPanelExample extends Application {

    private boolean isPanelVisible = false;

    @Override
    public void start(Stage stage) {
        // Основная панель
        StackPane mainContent = new StackPane(new Rectangle(600, 400, Color.LIGHTGRAY));

        // Боковая панель
        Pane sidePanel = new Pane();
        sidePanel.setStyle("-fx-background-color: #333333;");
        sidePanel.setPrefWidth(200);
        sidePanel.setPrefHeight(400);

        // Настраиваем начальное положение панели за экраном
        sidePanel.setTranslateX(200);

        // Кнопка для открытия/закрытия боковой панели
        Button toggleButton = new Button("Меню");
        toggleButton.setOnAction(event -> togglePanel(sidePanel));

        // Добавляем элементы в BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(mainContent);
        root.setRight(toggleButton);
        root.setLeft(sidePanel);

        // Создаем сцену и отображаем её
        Scene scene = new Scene(root, 800, 400);
        stage.setScene(scene);
        stage.setTitle("Slide-in Panel Example");
        stage.show();
    }

    // Метод для открытия/закрытия панели с анимацией
    private void togglePanel(Pane sidePanel) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidePanel);

        if (isPanelVisible) {
            // Закрываем панель
            transition.setToX(200);
            isPanelVisible = false;
        } else {
            // Открываем панель
            transition.setToX(0);
            isPanelVisible = true;
        }
        transition.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
