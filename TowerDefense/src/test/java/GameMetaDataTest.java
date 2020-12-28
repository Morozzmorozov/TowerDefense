import org.junit.Assert;
import org.junit.Test;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.EffectType;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.EnemyType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.ProjectileType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;
import ru.nsu.fit.towerdefense.metadata.techtree.Research;
import ru.nsu.fit.towerdefense.metadata.techtree.TechTree;

import java.util.ArrayList;
import java.util.Collection;

public class GameMetaDataTest {
    @Test
    public void testMapRead()
    {
        GameMetaData data = GameMetaData.getInstance();
        GameMap map = data.getMapDescription("map1");
        Assert.assertNotNull(map);
        Assert.assertEquals((Integer)10, map.getSize().getX());
        Assert.assertEquals((Integer)10, map.getSize().getY());
        Assert.assertEquals((Integer)4, map.getScienceReward());
        Assert.assertEquals((Integer)500, map.getBaseDescription().getHealth());
        Assert.assertEquals((Integer)1, map.getBaseDescription().getPosition().getX());
        Assert.assertEquals((Integer)2, map.getBaseDescription().getPosition().getY());


    }

    @Test
    public void testEnemyTypeRead()
    {
        GameMetaData data = GameMetaData.getInstance();
        EnemyType goblin = data.getEnemyType("Goblin");
        Assert.assertNotNull(goblin);
        Assert.assertEquals(20, goblin.getHealth());
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

    @Test
    public void testGetGameMapNames()
    {
        GameMetaData data = GameMetaData.getInstance();
        Collection<String> names = data.getGameMapNames();
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("Level 1", names.iterator().next());
    }
    @Test
    public void testGetTowerNames()
    {
        GameMetaData data = GameMetaData.getInstance();
        Collection<String> names = data.getTowerNames();
        Assert.assertEquals(5, names.size());

    }

    @Test
    public void testTech()
    {
        GameMetaData data = GameMetaData.getInstance();
        TechTree tree = data.getTechTree();

        ArrayList<Research> researchArrayList = tree.getResearch();

        Assert.assertEquals(1, tree.getAvailableResearches().size());


        var t = tree.getSortedResearches();

        Assert.assertEquals(3, t.size());

        tree.unlock(researchArrayList.get(0).getName());

        Assert.assertEquals(2, tree.getAvailableResearches().size());

    }

    @Test
    public void testEffect()
    {
        GameMetaData data = GameMetaData.getInstance();
        EffectType poison = data.getEffectType("Poison");

        System.out.println(poison.getDamagePerTick());
    }

    /*
    @Test
    public void testGetImageName()
    {
        GameMetaData data = GameMetaData.getInstance();
        Assert.assertEquals("src/main/resources/ru/nsu/fit/towerdefense/images/triangle.png", data.getImagePath("triangle"));
    }
*/
}
