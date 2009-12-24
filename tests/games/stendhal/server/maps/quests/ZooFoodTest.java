package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.outside.AnimalKeeperNPC;
import games.stendhal.server.maps.ados.outside.VeterinarianNPC;
import marauroa.common.game.RPObject.ID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class ZooFoodTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "testzone";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME, new AnimalKeeperNPC(), new VeterinarianNPC());

		final ZooFood zf = new ZooFood();
		zf.addToWorld();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public ZooFoodTest() {
		super(ZONE_NAME, "Katinka", "Dr. Feelgood");
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final Player player = createPlayer("player");

		SpeakerNPC npc = SingletonRepository.getNPCList().get("Katinka");
		assertNotNull(npc);
		final Engine en1 = npc.getEngine();
		assertTrue("test text recognition with additional text after 'hi'",
				en1.step(player, "hi Katinka"));
		assertTrue(npc.isTalking());
		assertEquals(
				"Welcome to the Ados Wildlife Refuge! We rescue animals from being slaughtered by evil adventurers. But we need help... maybe you could do a #task for us?",
				getReply(npc));
		assertTrue("test text recognition with additional text after 'bye'",
				en1.step(player, "bye bye"));
		assertFalse(npc.isTalking());
		assertEquals("Goodbye!", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Dr. Feelgood");
		assertNotNull(npc);
		final Engine en = npc.getEngine();
		assertTrue(en.step(player, "hi"));
		assertFalse(npc.isTalking());
		assertEquals(
				"Sorry, can't stop to chat. The animals are all sick because they don't have enough food. See yourself out, won't you?",
				getReply(npc));
		assertFalse(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals(null, getReply(npc));

	}

	/**
	 * Tests for doQuest.
	 */
	@Test
	public void testDoQuest() {
		final Player player = createPlayer("player");

		final SpeakerNPC katinkaNpc = SingletonRepository.getNPCList().get("Katinka");
		assertNotNull(katinkaNpc);
		final Engine enKatinka = katinkaNpc.getEngine();
		final SpeakerNPC feelgoodNpc = SingletonRepository.getNPCList().get("Dr. Feelgood");
		assertNotNull(feelgoodNpc);
		final Engine enFeelgood = feelgoodNpc.getEngine();
		assertTrue("test saying 'Hallo' instead of 'hi'", enKatinka.step(
				player, "Hallo"));
		assertEquals(
				"Welcome to the Ados Wildlife Refuge! We rescue animals from being slaughtered by evil adventurers. But we need help... maybe you could do a #task for us?",
				getReply(katinkaNpc));

		assertTrue(enKatinka.step(player, "task"));
		assertEquals(
				"Our tigers, lions and bears are hungry. We need 10 pieces of ham to feed them. Can you help us?",
				getReply(katinkaNpc));

		assertTrue(enKatinka.step(player, "yes"));
		assertTrue(player.hasQuest("zoo_food"));
		assertEquals(
				"Okay, but please don't let the poor animals suffer too long! Bring me the pieces of ham as soon as you get them.",
				getReply(katinkaNpc));

		assertTrue(enKatinka.step(player, "bye"));
		assertEquals("Goodbye!", getReply(katinkaNpc));
		assertTrue(player.hasQuest("zoo_food"));
		assertEquals("start", player.getQuest("zoo_food"));
		// feelgood is still in sorrow
		assertTrue(enFeelgood.step(player, "hi"));
		assertFalse(feelgoodNpc.isTalking());
		assertEquals(
				"Sorry, can't stop to chat. The animals are all sick because they don't have enough food. See yourself out, won't you?",
				getReply(feelgoodNpc));
		assertFalse(enFeelgood.step(player, "bye"));
		assertFalse(feelgoodNpc.isTalking());
		assertEquals(null, getReply(feelgoodNpc));
		// bother katinka again
		assertTrue(enKatinka.step(player, "hi"));
		assertEquals("Welcome back! Have you brought the 10 pieces of ham?",
				getReply(katinkaNpc));
		assertTrue("lie", enKatinka.step(player, "yes")); 
		assertEquals(
				"*sigh* I SPECIFICALLY said that we need 10 pieces of ham!",
				getReply(katinkaNpc));
		assertTrue(enKatinka.step(player, "bye"));
		assertEquals("Goodbye!", getReply(katinkaNpc));
		// equip player with to less needed stuff
		final StackableItem ham = new StackableItem("ham", "", "", null);
		ham.setQuantity(5);
		ham.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(ham);
		assertEquals(5, player.getNumberOfEquipped("ham"));

		// bother katinka again
		assertTrue(enKatinka.step(player, "hi"));
		assertEquals("Welcome back! Have you brought the 10 pieces of ham?",
				getReply(katinkaNpc));
		assertTrue("lie", enKatinka.step(player, "yes")); 
		assertEquals(
				"*sigh* I SPECIFICALLY said that we need 10 pieces of ham!",
				getReply(katinkaNpc));
		assertTrue(enKatinka.step(player, "bye"));
		assertEquals("Goodbye!", getReply(katinkaNpc));
		// equip player with to needed stuff
		final StackableItem ham2 = new StackableItem("ham", "", "", null);
		ham2.setQuantity(5);
		ham2.setID(new ID(3, ZONE_NAME));
		player.getSlot("bag").add(ham2);
		assertEquals(10, player.getNumberOfEquipped("ham"));
		// bring stuff to katinka
		assertTrue(enKatinka.step(player, "hi"));
		assertEquals("Welcome back! Have you brought the 10 pieces of ham?",
				getReply(katinkaNpc));
		assertTrue(enKatinka.step(player, "yes"));
		assertEquals("Thank you! You have rescued our rare animals.",
				getReply(katinkaNpc));
		assertTrue(enKatinka.step(player, "bye"));
		assertEquals("Goodbye!", getReply(katinkaNpc));
		assertEquals("done", player.getQuest("zoo_food"));
		// feelgood is reacting
		assertTrue(enFeelgood.step(player, "hi"));
		assertTrue(feelgoodNpc.isTalking());
		assertEquals(
				"Hello! Now that the animals have enough food, they don't get sick that easily, and I have time for other things. How can I help you?",
				getReply(feelgoodNpc));
		assertTrue(enFeelgood.step(player, "offers"));

		assertEquals(
				"I sell antidote, minor potion, potion, and greater potion.",
				getReply(feelgoodNpc));
		assertTrue(enFeelgood.step(player, "bye"));
		assertEquals("Bye!", getReply(feelgoodNpc));
	}
}
