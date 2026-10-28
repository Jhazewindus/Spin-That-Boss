package com.jhazewindus.spinthatboss;

import net.runelite.client.party.messages.PartyMemberMessage;

public class MatchMessage extends PartyMemberMessage
{
	public String operation;
	public MatchState state;
	public String matchId;
	public String boss;
	public int legNumber;
	public PlayerProgress memberProgress;
}
