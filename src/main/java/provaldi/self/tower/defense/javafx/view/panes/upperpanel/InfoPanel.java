package provaldi.self.tower.defense.javafx.view.panes.upperpanel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static provaldi.self.tower.defense.javafx.Constants.STATUS_PANEL_HEIGHT;

public class InfoPanel extends HBox {

    public InfoPanel() {
        setMinWidth(160);
        setMaxHeight(STATUS_PANEL_HEIGHT);

        Label timeLabel = configureTextCurrentTimeBlock();
        Label clockLabel = configureValueCurrentTimeBlock();

        getChildren().addAll(timeLabel, clockLabel);
    }

    private Label configureValueCurrentTimeBlock() {
        Label clockLabel = new Label();
        clockLabel.setFont(Font.font(15));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    LocalTime currentTime = LocalTime.now();
                    clockLabel.setText(currentTime.format(timeFormatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play(); // можно лучше включать вообще все асинхронные штуки такого рода откуда-то из одного места
        // возможно чтобы иметь возможность паузить вообще всё в игре

        return clockLabel;
    }

    private Label configureTextCurrentTimeBlock() {
        Label timeLabel = new Label("Time:  ");
        timeLabel.setFont(Font.font(15));
        return timeLabel;
    }
}
