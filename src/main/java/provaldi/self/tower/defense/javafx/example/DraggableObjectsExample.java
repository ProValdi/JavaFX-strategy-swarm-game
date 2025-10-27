package provaldi.self.tower.defense.javafx.example;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import static provaldi.self.tower.defense.javafx.Constants.GAME_HEIGHT;
import static provaldi.self.tower.defense.javafx.Constants.GAME_WIDTH;

public class DraggableObjectsExample extends Application {

    private boolean isPanelVisible = false;
    private ImageView floatingImageView = null;

    @Override
    public void start(Stage stage) {
        // Основной Canvas для размещения объектов
        Canvas mainCanvas = new Canvas(600, 400);
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, 600, 400);

        // Боковая панель
        Pane sidePanel = new Pane();
        sidePanel.setStyle("-fx-background-color: #333333;");
        sidePanel.setPrefWidth(200);
        sidePanel.setPrefHeight(400);
        sidePanel.setTranslateX(200); // Начальное положение за экраном

        // Создаем несколько кликабельных изображений на боковой панели
        Image objectImage1 = new Image("main1.jpg"); // Замените на путь к вашему изображению
        ImageView imageView1 = createClickableImageView(objectImage1);

        Image objectImage2 = new Image("main2.jpg"); // Замените на путь к вашему изображению
        ImageView imageView2 = createClickableImageView(objectImage2);

        // Располагаем изображения на боковой панели
        imageView1.setLayoutY(20);
        imageView2.setLayoutY(100);
        sidePanel.getChildren().addAll(imageView1, imageView2);

        // Обрабатываем перетаскивание на Canvas
        mainCanvas.setOnDragOver(event -> {
            System.out.println("mose");
            if (event.getGestureSource() instanceof ImageView) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        mainCanvas.setOnDragDropped(event -> {
            System.out.println("here");
            // Получаем изображение и координаты перетаскивания
            ImageView source = (ImageView) event.getGestureSource();
            Image image = source.getImage();
            double x = event.getX();
            double y = event.getY();

            // Рисуем изображение на Canvas
            gc.drawImage(image, x - image.getWidth() / 2, y - image.getHeight() / 2);
            event.setDropCompleted(true);
            event.consume();
        });

        // Кнопка для открытия/закрытия боковой панели
        Button toggleButton = new Button("Меню");
        toggleButton.setOnAction(event -> togglePanel(sidePanel));

        // Добавляем элементы в BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(mainCanvas);
        root.setRight(toggleButton);
        root.setLeft(sidePanel);

        // Настраиваем сцену и окно
        Scene scene = new Scene(root, 800, 400);

        mainCanvas.setOnMouseDragged(event -> {
            if (floatingImageView != null) {
                floatingImageView.setX(event.getX() + 800 - floatingImageView.getFitWidth() / 2);
                floatingImageView.setY(event.getY() + 400 - floatingImageView.getFitHeight() / 2);
                System.out.println(event.getSceneX());
                System.out.println(event.getX());
                System.out.println(event.getScreenX());
                System.out.println(floatingImageView.getFitWidth() / 2);
                mainCanvas.requestFocus();
            }
        });



        // Обрабатываем клик на Canvas для добавления изображения
        mainCanvas.setOnMouseClicked(event -> {
            System.out.println("clicked main canvas");
            if (floatingImageView != null) {
                // Рисуем изображение на Canvas
                gc.drawImage(floatingImageView.getImage(),  event.getX() - floatingImageView.getFitWidth() / 2,
                        event.getY() - floatingImageView.getFitHeight() / 2, 40, 40);
                // Убираем временное изображение
                floatingImageView = null;
            }
        });

        stage.setScene(scene);
        stage.setTitle("Draggable Objects Example");
        stage.show();
    }

    private ImageView createClickableImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        imageView.setOnMouseClicked(event -> {
            System.out.println("clicked image");
            if (floatingImageView == null) {
                // Создаем новый временный ImageView, который будет следовать за мышью
                floatingImageView = new ImageView(imageView.getImage());
                floatingImageView.setFitWidth(50);
                floatingImageView.setFitHeight(50);
                //floatingImageView.setOpacity(10.0);
                floatingImageView.setMouseTransparent(true);
                ((Pane) imageView.getParent()).getChildren().add(floatingImageView);
            }
            event.consume();
        });

        return imageView;
    }

    // Метод для создания перетаскиваемого ImageView
    private ImageView createDraggableImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        imageView.setOnDragDetected(event -> {
            System.out.println("eee");

            ClipboardContent content = new ClipboardContent();
            content.putImage(imageView.getImage());

            var db = imageView.startDragAndDrop(TransferMode.COPY);
            db.setContent(content);
            db.setDragView(imageView.getImage());

            event.consume();
        });

        return imageView;
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
