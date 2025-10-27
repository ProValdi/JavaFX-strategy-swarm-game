package provaldi.self.tower.defense.javafx.logic;

import javafx.scene.paint.Color;
import lombok.Getter;
import provaldi.self.tower.defense.javafx.view.model.EntityView;
import provaldi.self.tower.defense.javafx.view.model.entities.TestModel;
import provaldi.self.tower.defense.javafx.view.model.interfaces.Drawable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameObjects {
    @Getter
    private final List<Drawable> staticObjects = new ArrayList<>();
    @Getter
    private final List<Drawable> dynamicObjects = new ArrayList<>();

    public GameObjects() {
        createCircles();
    }

    public void createCircles () {
        for (int i = 0; i < 20; i++) {
            Color rand = Color.color(ThreadLocalRandom.current().nextDouble(),
                    ThreadLocalRandom.current().nextDouble(),
                    ThreadLocalRandom.current().nextDouble());
            TestModel testModel = new TestModel(ThreadLocalRandom.current().nextDouble(30, 500),
                    ThreadLocalRandom.current().nextDouble(30, 400),
                    ThreadLocalRandom.current().nextDouble(2, 5), new EntityView(rand));
            dynamicObjects.add(testModel);
        }
    }

}
