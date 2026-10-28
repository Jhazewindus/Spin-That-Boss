package com.jhazewindus.spinthatboss;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class BossWheelTest
{
	@Test
	public void poolTrimsAndDeduplicatesNames()
	{
		assertEquals(Arrays.asList("Zulrah", "Vorkath"), BossWheel.parsePool(" Zulrah, ,Vorkath,zULRAH,"));
	}

	@Test
	public void absentPoolIsEmpty()
	{
		assertTrue(BossWheel.parsePool(null).isEmpty());
		assertTrue(BossWheel.parsePool(" , ").isEmpty());
	}

	@Test
	public void bagsContainEveryBossAndAvoidBoundaryRepeats()
	{
		List<String> pool = Arrays.asList("Zulrah", "Vorkath", "Kraken", "Cerberus");
		String previous = "zulrah";
		for (int cycle = 0; cycle < 100; cycle++)
		{
			List<String> bag = BossWheel.shuffledBag(pool, previous);
			assertEquals(pool.size(), bag.size());
			assertEquals(new HashSet<>(pool), new HashSet<>(bag));
			assertFalse(previous.equalsIgnoreCase(bag.get(0)));
			previous = bag.get(bag.size() - 1);
		}
		assertEquals(Arrays.asList("Zulrah", "Vorkath", "Kraken", "Cerberus"), pool);
	}

	@Test
	public void singleBossCanRepeatAndEmptyBagIsSafe()
	{
		assertEquals(Collections.singletonList("Zulrah"), BossWheel.shuffledBag(Collections.singletonList("Zulrah"), "Zulrah"));
		assertTrue(BossWheel.shuffledBag(Collections.emptyList(), null).isEmpty());
	}
}
