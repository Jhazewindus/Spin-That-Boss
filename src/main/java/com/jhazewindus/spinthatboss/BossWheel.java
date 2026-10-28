package com.jhazewindus.spinthatboss;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class BossWheel
{
	private static final SecureRandom RANDOM = new SecureRandom();

	private BossWheel()
	{
	}

	static List<String> parsePool(String value)
	{
		List<String> pool = new ArrayList<>();
		if (value == null)
		{
			return pool;
		}

		for (String raw : value.split(","))
		{
			String boss = raw.trim();
			if (!boss.isEmpty() && pool.stream().noneMatch(existing -> existing.equalsIgnoreCase(boss)))
			{
				pool.add(boss);
			}
		}
		return pool;
	}

	static List<String> shuffledBag(List<String> pool, String previousBoss)
	{
		List<String> bag = new ArrayList<>(pool);
		Collections.shuffle(bag, RANDOM);
		if (bag.size() > 1 && bag.get(0).equalsIgnoreCase(previousBoss))
		{
			Collections.swap(bag, 0, 1);
		}
		return bag;
	}
}
