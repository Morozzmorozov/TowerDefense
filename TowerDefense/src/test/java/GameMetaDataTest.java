import org.junit.Assert;
import org.junit.Test;
import ru.nsu.fit.towerdefense.model.world.GameMetaData;
import ru.nsu.fit.towerdefense.model.world.map.GameMap;

public class GameMetaDataTest {
    @Test
    public void testMapRead()
    {
        GameMetaData data = GameMetaData.getInstance();
        GameMap map = data.loadMapDescription("map1");
        Assert.assertNotNull(map);
        Assert.assertEquals((Integer)2, map.getSize().getX());
        Assert.assertEquals((Integer)3, map.getSize().getY());
        Assert.assertEquals((Integer)4, map.getScienceReward());
        Assert.assertEquals((Integer)5, map.getBaseDescription().getHealth());
        Assert.assertEquals((Integer)1, map.getBaseDescription().getPosition().getX());
        Assert.assertEquals((Integer)2, map.getBaseDescription().getPosition().getY());
    }
}
