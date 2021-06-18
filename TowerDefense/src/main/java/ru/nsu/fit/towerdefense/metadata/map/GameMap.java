package ru.nsu.fit.towerdefense.metadata.map;

import ru.nsu.fit.towerdefense.util.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameMap {
    private Vector2<Integer> size;
    private Integer scienceReward;
    private Integer playersNumber = 1;

    private BaseDescription baseDescription;
    private TowerBuildingPositions positions;
    private RoadDescription roads;
    private List<WaveDescription> waves;
    private List<List<Integer>> playersPlatform;

    private Iterator<WaveDescription> descriptionIterator;

    private GameMap(Vector2<Integer> size, Integer scienceReward, BaseDescription baseDescription,
                    TowerBuildingPositions positions, RoadDescription roads, List<WaveDescription> waves, int playersNumber,
                    List<List<Integer>> playersPlatform)
    {
        this.size = size;
        this.scienceReward = scienceReward;
        this.baseDescription = baseDescription;
        this.positions = positions;
        this.roads = roads;
        this.waves = waves;
        descriptionIterator = waves.iterator();
        this.playersNumber = playersNumber;
        if (playersPlatform == null)
        {
            this.playersPlatform = new ArrayList<>();
            List<Integer> t = new ArrayList<>();
            for (int i = 0; i < positions.getPositions().size(); i++)
            {
                t.add(i);
            }
            this.playersPlatform.add(t);
        }
    }

    public static class Builder
    {
        private Vector2<Integer> size;
        private Integer scienceReward;

        private BaseDescription baseDescription;
        private TowerBuildingPositions positions;
        private RoadDescription roads;
        private List<WaveDescription> waves;
        private int playersNumber = 1;
        private List<List<Integer>> playerPlatforms;


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

        public void setPlayersNumber(int number){
            this.playersNumber = number;
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
        public void setPlayerPlatforms(List<List<Integer>> playerPlatforms)
        {
            this.playerPlatforms = playerPlatforms;
        }
        public GameMap build()
        {
            return new GameMap(size, scienceReward, baseDescription, positions, roads, waves, playersNumber, playerPlatforms);
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

    public Integer getPlayersNumber()
    {
        return playersNumber;
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

    public List<Vector2<Integer>> getBuildingPositions(int playerId)
    {
        List<Vector2<Integer>> positions = new ArrayList<>();
        for (var x : playersPlatform.get(playerId))
        {
            positions.add(positions.get(x));
        }
        return positions;
    }

    public boolean isCooperativeAvailable()
    {
        if (playersPlatform == null) {//todo del
            System.out.println("NULL");
        } else {
            System.out.println(playersPlatform);
        }
        return playersPlatform != null && playersPlatform.size() != 1;
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
