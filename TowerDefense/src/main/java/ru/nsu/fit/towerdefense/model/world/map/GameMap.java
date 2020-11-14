package ru.nsu.fit.towerdefense.model.world.map;

import ru.nsu.fit.towerdefense.model.world.Vector2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameMap {
    private Vector2<Integer> size;
    private Integer scienceReward;

    private BaseDescription baseDescription;
    private TowerBuildingPositions positions;
    private RoadDescription roads;
    private List<WaveDescription> waves;

    private Iterator<WaveDescription> descriptionIterator;

    public GameMap()
    {
        waves = new LinkedList<>();
        descriptionIterator = waves.listIterator();
    }



    public WaveDescription getDesctiprtion()
    {
        if (descriptionIterator.hasNext())
        {
            return descriptionIterator.next();
        }
        else
        {
            return null;
        }
    }

    public TowerBuildingPositions getPositions() {
        return positions;
    }

    public BaseDescription getBaseDescription() {
        return baseDescription;
    }

    public Vector2<Integer> getSize() {
        return size;
    }

    public Integer getScienceReward() {
        return scienceReward;
    }

    public Iterator<WaveDescription> getDescriptionIterator() {
        return descriptionIterator;
    }

    public List<WaveDescription> getWaves() {
        return waves;
    }

    public RoadDescription getRoads() {
        return roads;
    }

    public void setPositions(TowerBuildingPositions positions) {
        this.positions = positions;
    }

    public void setBaseDescription(BaseDescription baseDescription) {
        this.baseDescription = baseDescription;
    }

    public void setDescriptionIterator(Iterator<WaveDescription> descriptionIterator) {
        this.descriptionIterator = descriptionIterator;
    }

    public void setRoads(RoadDescription roads) {
        this.roads = roads;
    }

    public void setScienceReward(Integer scienceReward) {
        this.scienceReward = scienceReward;
    }

    public void setSize(Vector2<Integer> size) {
        this.size = size;
    }

    public void setWaves(List<WaveDescription> waves) {
        this.waves = waves;
    }
}
