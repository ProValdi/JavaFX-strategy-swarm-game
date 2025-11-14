package chatgptonly.logic;

import javafx.animation.AnimationTimer;
import java.util.function.Function;

public class GameLoop {

    private long lastTimeNs = 0;
    private AnimationTimer loop;

    public AnimationTimer getMainLoop(Function<Double, Void> executeUpdate) {
        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTimeNs == 0) {
                    lastTimeNs = now;
                    return;
                }
                double dt = (now - lastTimeNs) / 1_000_000_000.0;
                lastTimeNs = now;
                executeUpdate.apply(dt);
            }
        };
        return loop;
    }
}
