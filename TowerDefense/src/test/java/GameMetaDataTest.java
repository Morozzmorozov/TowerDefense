import org.junit.Assert;
import org.junit.Test;
import ru.nsu.fit.towerdefense.model.GameMetaData;
import ru.nsu.fit.towerdefense.model.map.GameMap;
import ru.nsu.fit.towerdefense.model.world.types.EnemyType;
import ru.nsu.fit.towerdefense.model.world.types.ProjectileType;
import ru.nsu.fit.towerdefense.model.world.types.TowerType;

public class GameMetaDataTest {
    @Test
    public void testMapRead()
    {
        GameMetaData data = GameMetaData.getInstance();
        GameMap map = data.getMapDescription("map1");
        Assert.assertNotNull(map);
        Assert.assertEquals((Integer)2, map.getSize().getX());
        Assert.assertEquals((Integer)3, map.getSize().getY());
        Assert.assertEquals((Integer)4, map.getScienceReward());
        Assert.assertEquals((Integer)5, map.getBaseDescription().getHealth());
        Assert.assertEquals((Integer)1, map.getBaseDescription().getPosition().getX());
        Assert.assertEquals((Integer)2, map.getBaseDescription().getPosition().getY());


    }

    @Test
    public void testEnemyTypeRead()
    {
        GameMetaData data = GameMetaData.getInstance();
        EnemyType goblin = data.getEnemyType("Goblin");
        Assert.assertNotNull(goblin);
        Assert.assertEquals(10, goblin.getHealth());
        var x = data.getEnemyType(goblin.getTypeName());
        Assert.assertEquals(x, goblin);
    }

    @Test
    public void testTowerTypeRead()
    {
        GameMetaData data = GameMetaData.getInstance();
        TowerType archer = data.getTowerType("Archer");
        Assert.assertNotNull(archer);
        Assert.assertEquals(2, archer.getUpgrades().size());
        var x = data.getTowerType(archer.getTypeName());
        Assert.assertEquals(x, archer);
    }

    @Test
    public void testProjectileTypeRead()
    {
        GameMetaData data = GameMetaData.getInstance();
        ProjectileType arrow = data.getProjectileType("Arrow");
        Assert.assertNotNull(arrow);
        Assert.assertEquals(3, arrow.getEnemyTypeDamageMap().size());
        var x = data.getProjectileType(arrow.getTypeName());
        Assert.assertEquals(x, arrow);
    }
}
