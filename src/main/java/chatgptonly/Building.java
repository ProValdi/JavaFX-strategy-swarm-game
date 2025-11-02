package chatgptonly;

import javafx.scene.canvas.GraphicsContext;

public abstract class Building {

    protected final int tileX;
    protected final int tileY;

    public Building(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    // NEW: размер по-умолчанию 1x1
    public int getWidthTiles()  { return 1; }
    public int getHeightTiles() { return 1; }

    public int getTileX() { return tileX; }
    public int getTileY() { return tileY; }

    // логика апдейта (добыча, производство и т.д.)
    public abstract void update(double dt, World world);

    // отрисовка статического корпуса (формы, корпуса турели, бур)
    public abstract void renderStatic(GraphicsContext gc, int tileSize);

    // отрисовка динамических оверлеев (полоска прогресса, хп-бар)
    public abstract void renderDynamic(GraphicsContext gc, int tileSize);
}