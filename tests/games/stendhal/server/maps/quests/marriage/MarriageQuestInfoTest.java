package games.stendhal.server.maps.quests.marriage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class MarriageQuestInfoTest {

	@BeforeClass
	public static void setupBeforeClass() {
		MockStendlRPWorld.get();
	}
	
	@AfterClass
	public static void teardownAfterClass() throws Exception {
		
		MockStendlRPWorld.reset();
	}
	/**
	 * Tests for getQuestSlot.
	 */
	@Test
	public void testGetQuestSlot() {
		MarriageQuestInfo questinfo = new MarriageQuestInfo();
		assertEquals("marriage", questinfo.getQuestSlot());
	}

	/**
	 * Tests for getSpouseQuestSlot.
	 */
	@Test
	public void testGetSpouseQuestSlot() {
		MarriageQuestInfo questinfo = new MarriageQuestInfo();
		assertEquals("spouse", questinfo.getSpouseQuestSlot());

	}

	/**
	 * Tests for isMarried.
	 */
	@Test
	public void testIsMarried() {
		MarriageQuestInfo questinfo = new MarriageQuestInfo();
		Player bob = PlayerTestHelper.createPlayer("bob");
		assertFalse(questinfo.isMarried(bob));
		bob.setQuest(questinfo.getSpouseQuestSlot(), "any");
		assertTrue(questinfo.isMarried(bob));
	}

	/**
	 * Tests for isEngaged.
	 */
	@Test
	public void testIsEngaged() {
		MarriageQuestInfo questinfo = new MarriageQuestInfo();
		Player bob = PlayerTestHelper.createPlayer("bob");
		assertFalse(questinfo.isEngaged(bob));
		bob.setQuest(questinfo.getQuestSlot(), "engagedany");
		assertTrue(questinfo.isEngaged(bob));
		
		bob.setQuest(questinfo.getQuestSlot(), "forging;any");
		assertTrue(questinfo.isEngaged(bob));
		
		

	}

}
