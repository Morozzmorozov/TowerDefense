package ru.nsu.fit.towerdefense.model.world.map;

import ru.nsu.fit.towerdefense.model.world.Vector2;

import java.util.LinkedList;
import java.util.List;

public class TowerBuildingPositions {
    List<Vector2<Integer>> positions;

    public TowerBuildingPositions()
    {
        positions = new LinkedList<>();
    }

    public List<Vector2<Integer>> getPositions() {
        return positions;
    }

    public void setPositions(List<Vector2<Integer>> positions) {
        this.positions = positions;
    }

    public void addPosition(Vector2<Integer> pos)
    {
        positions.add(pos);
    }
}
