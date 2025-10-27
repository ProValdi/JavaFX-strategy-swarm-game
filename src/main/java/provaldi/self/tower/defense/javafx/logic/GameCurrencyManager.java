package provaldi.self.tower.defense.javafx.logic;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GameCurrencyManager {
    private final IntegerProperty gold = new SimpleIntegerProperty();

    public IntegerProperty goldProperty() {
        return gold;
    }

    public int getGold() {
        return gold.get();
    }

    public void setGold(int amount) {
        gold.set(amount);
    }

    public void addGold(int amount) {
        gold.set(gold.get() + amount);
    }
}
