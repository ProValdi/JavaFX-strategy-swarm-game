package provaldi.self.tower.defense.javafx.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MouseScrollPaneExample extends Application {

    private double lastMouseX;
    private double lastMouseY;

    @Override
    public void start(Stage stage) {
        // Создаем большой Pane для демонстрации прокрутки
        Pane content = new Pane();

        // Создаем холсты для статического и динамического содержимого
        Canvas staticCanvas = new Canvas(600, 400);
        Canvas dynamicCanvas = new Canvas(600, 400);

        // Добавляем оба холста на Pane
        content.getChildren().addAll(staticCanvas, dynamicCanvas);

        // Рисуем статические объекты один раз
        drawStaticObjects(staticCanvas.getGraphicsContext2D());

        // Создаем и запускаем анимационный таймер для обновления динамических объектов
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateDynamicObjects(dynamicCanvas.getGraphicsContext2D());
            }
        };
        animationTimer.start();


        // Добавим несколько объектов на Pane для демонстрации
        for (int i = 0; i < 10; i++) {
            Rectangle rect = new Rectangle(100 + i * 120, 100 + i * 100, 100, 100);
            rect.setFill(Color.color(Math.random(), Math.random(), Math.random()));
            content.getChildren().add(rect);
        }

        // Создаем ScrollPane и добавляем в него содержимое
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setPrefSize(600, 400);
        scrollPane.setPannable(false);  // Отключаем стандартную прокрутку мышью

        // Отслеживаем нажатие мыши для захвата положения
        content.setOnMousePressed(e -> {
            System.out.println("Mouse pressed");
            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });

        // Перемещаем панель при движении мыши
        content.setOnMouseDragged (e -> {
            System.out.println("Mouse dragged");
            double deltaX = lastMouseX - e.getSceneX();
            double deltaY = lastMouseY - e.getSceneY();

            // Обновляем значение прокрутки
            scrollPane.setHvalue(scrollPane.getHvalue() + deltaX / content.getWidth());
            scrollPane.setVvalue(scrollPane.getVvalue() + deltaY / content.getHeight());

            // Обновляем положение мыши
            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });

        // Создаем сцену и отображаем её
        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.setTitle("ScrollPane Mouse Drag Example");
        stage.show();
    }

    // Метод для рисования статических объектов (вызывается один раз)
    private void drawStaticObjects(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, 600, 400);

        // Пример статического объекта
        gc.setFill(Color.BLUE);
        gc.fillRect(100, 100, 200, 200);
        gc.setFill(Color.GREEN);
        gc.fillRect(300, 300, 150, 150);
    }

    // Метод для обновления динамических объектов
    private void updateDynamicObjects(GraphicsContext gc) {
        // Очищаем предыдущие кадры динамического слоя
        gc.clearRect(0, 0, 600, 400);

        // Рисуем пример динамических объектов
        gc.setFill(Color.RED);
        double x = Math.random() * 600;
        double y = Math.random() * 400;
        gc.fillOval(x, y, 30, 30);  // Рисуем случайный круг для примера
    }

    public static void main(String[] args) {
        launch(args);
    }
}