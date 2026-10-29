package com.jhazewindus.spinthatboss;

import java.awt.Color;
import java.awt.image.BufferedImage;

final class PlayerCardData
{
	final String name;
	final String details;
	final Color color;
	final BufferedImage avatar;

	PlayerCardData(String name, String details, Color color, BufferedImage avatar)
	{
		this.name = name;
		this.details = details;
		this.color = color;
		this.avatar = avatar;
	}
}
