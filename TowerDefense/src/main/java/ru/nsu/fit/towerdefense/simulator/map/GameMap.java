package ru.nsu.fit.towerdefense.simulator.map;

import ru.nsu.fit.towerdefense.simulator.util.Vector2;

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

    private GameMap(Vector2<Integer> size, Integer scienceReward, BaseDescription baseDescription,
                    TowerBuildingPositions positions, RoadDescription roads, List<WaveDescription> waves)
    {
        this.size = size;
        this.scienceReward = scienceReward;
        this.baseDescription = baseDescription;
        this.positions = positions;
        this.roads = roads;
        this.waves = waves;
        descriptionIterator = waves.iterator();
    }

    public static class Builder
    {
        private Vector2<Integer> size;
        private Integer scienceReward;

        private BaseDescription baseDescription;
        private TowerBuildingPositions positions;
        private RoadDescription roads;
        private List<WaveDescription> waves;

        public Builder()
        {
            waves = new LinkedList<>();
            size = new Vector2<>(0, 0);
        }

        public void addWave(WaveDescription wave)
        {
            this.waves.add(wave);
        }

        public void setSize(int x, int y)
        {
            this.size.setX(x);
            this.size.setY(y);
        }

        public void setScienceReward(Integer scienceReward)
        {
            this.scienceReward = scienceReward;
        }

        public void setRoads(RoadDescription roads)
        {
            this.roads = roads;
        }

        public void setPositions(TowerBuildingPositions positions)
        {
            this.positions = positions;
        }

        public void setBaseDescription(BaseDescription baseDescription)
        {
            this.baseDescription = baseDescription;
        }

        public GameMap build()
        {
            return new GameMap(size, scienceReward, baseDescription, positions, roads, waves);
        }
    }


    public WaveDescription getDescription()
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

   /* public void setPositions(TowerBuildingPositions positions) {
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
    */
}
