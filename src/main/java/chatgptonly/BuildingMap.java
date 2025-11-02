package chatgptonly;

import java.util.ArrayList;
import java.util.List;

public class BuildingMap {

    private final int width;
    private final int height;
    private final Building[][] grid;

    public BuildingMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Building[width][height];
    }

    public Building get(int x, int y) {
        if (x >= 0 && y>= 0 && x < width & y < height) return grid[x][y];
        else return null;
    }

//    public void place(Building b) {
//        grid[b.getTileX()][b.getTileY()] = b;
//    }

    // NEW: установка с учётом footprint (помечаем все клетки ссылкой на один и тот же объект)
    public void place(Building b) {
        for (int dx = 0; dx < b.getWidthTiles(); dx++) {
            for (int dy = 0; dy < b.getHeightTiles(); dy++) {
                grid[b.getTileX() + dx][b.getTileY() + dy] = b;
            }
        }
    }

    public void remove(int x, int y) {
        grid[x][y] = null;
    }

    // Удобно, чтобы пройтись по всем зданиям
//    public Iterable<Building> all() {
//        List<Building> out = new ArrayList<>();
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                Building b = grid[x][y];
//                if (b != null) {
//                    out.add(b);
//                }
//            }
//        }
//        return out;
//    }

    // NEW: уникальный список построек (чтобы 2x2 не дублировалось 4 раза)
    public Iterable<Building> all() {
        java.util.LinkedHashSet<Building> set = new java.util.LinkedHashSet<>();
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if (grid[x][y] != null) set.add(grid[x][y]);
        return set;
    }
}
