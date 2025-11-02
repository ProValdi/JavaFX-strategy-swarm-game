package chatgptonly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class World {

    private final TileMap tileMap;
    private final BuildingMap buildingMap;
    private final List<Entity> dynamicEntities;

    public World(int width, int height) {
        this.tileMap = new TileMap(width, height);
        this.buildingMap = new BuildingMap(width, height);
        this.dynamicEntities = new ArrayList<>();

        tileMap.generateBasicTerrain(); // трава, руда, стены и т.д.
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public BuildingMap getBuildingMap() {
        return buildingMap;
    }

    public List<Entity> getDynamicEntities() {
        return dynamicEntities;
    }

    public void update(double dt) {
        // обновляем здания (их внутреннюю экономику)
        for (Building b : buildingMap.all()) {
            b.update(dt, this);
        }

        // обновляем динамические сущности (дроны, пули и т.д.)
        Iterator<Entity> it = dynamicEntities.iterator();
        while (it.hasNext()) {
            Entity e = it.next();
            e.update(dt, this);
            if (!e.isAlive()) {
                it.remove();
            }
        }
    }

    // вызывается из GameApp по клику игрока
    public boolean placeDrill(int tileX, int tileY) {
        // проверка границ
        if (!tileMap.inBounds(tileX, tileY)) return false;

        // можно ли ставить бур только на руде?
        int tileType = tileMap.getTile(tileX, tileY);
        if (tileType != TileMap.TILE_ORE) return false;

        // занято ли здание уже?
        if (buildingMap.get(tileX, tileY) != null) return false;

        // всё ок, ставим
        Drill d = new Drill(tileX, tileY);
        buildingMap.place(d);
        return true;
    }

    public boolean placeHeatGenerator(int tileX, int tileY) {
        if (!tileMap.inBounds(tileX, tileY)) return false;
        if (tileMap.getTile(tileX, tileY) != TileMap.TILE_LAVA) return false; // только на лаве
        if (buildingMap.get(tileX, tileY) != null) return false;

        buildingMap.place(new HeatGenerator(tileX, tileY));
        return true;
    }

    public boolean canPlaceDrill(int tileX, int tileY) {
        return tileMap.inBounds(tileX, tileY)
                && tileMap.getTile(tileX, tileY) == TileMap.TILE_ORE
                && buildingMap.get(tileX, tileY) == null;
    }

    public boolean canPlaceHeatGenerator(int tileX, int tileY) {
        return tileMap.inBounds(tileX, tileY)
                && tileMap.getTile(tileX, tileY) == TileMap.TILE_LAVA
                && buildingMap.get(tileX, tileY) == null;
    }

    public boolean canPlaceBarracks(int ox, int oy) {
        // границы под footprint
        if (!tileMap.inBounds(ox, oy)) return false;
        if (!tileMap.inBounds(ox+1, oy+1)) return false; // 2x2

        for (int dx=0; dx<2; dx++) for (int dy=0; dy<2; dy++) {
            int x = ox+dx, y = oy+dy;
            if (tileMap.getTile(x,y) != TileMap.TILE_GRASS) return false; // только трава
            if (buildingMap.get(x,y) != null) return false;               // пусто
        }
        return true;
    }

    public boolean placeBarracks(int ox, int oy) {
        if (!canPlaceBarracks(ox, oy)) return false;
        buildingMap.place(new Barracks(ox, oy));
        return true;
    }
}
