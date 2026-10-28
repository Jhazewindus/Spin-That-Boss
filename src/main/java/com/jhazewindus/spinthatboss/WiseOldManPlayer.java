package com.jhazewindus.spinthatboss;

import java.util.Map;

public class WiseOldManPlayer
{
	public Snapshot latestSnapshot;

	public static class Snapshot
	{
		public SnapshotData data;
	}

	public static class SnapshotData
	{
		public Map<String, BossScore> bosses;
	}

	public static class BossScore
	{
		public int kills;
	}
}
