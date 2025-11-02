package chatgptonly;

public class TileMap {

    public static final int TILE_GRASS = 0;
    public static final int TILE_WALL  = 1;
    public static final int TILE_ORE   = 2;
    public static final int TILE_LAVA  = 3;

    final int width;
    final int height;
    final int[][] tiles;

    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new int[width][height];
    }

    public boolean isWalkable(int tx, int ty) {
        if (!inBounds(tx, ty)) return false;
        return getTile(tx, ty) != TILE_WALL; // можно расширить (например, запретить лаву)
    }

    public void generateBasicTerrain() {
        // Пример: стены по периметру, трава внутри, пятно руды
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    tiles[x][y] = TILE_WALL;
                } else {
                    tiles[x][y] = TILE_GRASS;
                }
            }
        }

        // жила руды
        for (int x = 5; x < 12; x++) {
            for (int y = 8; y < 11; y++) {
                tiles[x][y] = TILE_ORE;
            }
        }

        // NEW: пятно лавы
        for (int x = 20; x < 26; x++) {
            for (int y = 12; y < 16; y++) {
                tiles[x][y] = TILE_LAVA;
            }
        }
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public int getTile(int x, int y) {
        return tiles[x][y];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
