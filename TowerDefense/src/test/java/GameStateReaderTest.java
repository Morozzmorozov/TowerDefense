import org.junit.Assert;
import org.junit.Test;
import ru.nsu.fit.towerdefense.replay.GameStateReader;
import ru.nsu.fit.towerdefense.replay.Replay;
import ru.nsu.fit.towerdefense.replay.WorldState;

public class GameStateReaderTest {

	@Test
	public void test()
	{
		GameStateReader reader = GameStateReader.getInstance();

		String[] a = reader.getReplays("map1");
		String[] b = reader.getSaves();

		WorldState state = reader.loadSave("map1");

		Replay replay = reader.readReplay("map1", a[1]);

		Assert.assertEquals(2, replay.getEventRecords().size());

	}
}