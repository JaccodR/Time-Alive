package com.alive;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("alive")
public interface AliveConfig extends Config
{
	@ConfigItem(
			position = 0,
			keyName = "showOverlay",
			name = "Show timer over NPCs",
			description = "Configures whether or not to show timer over NPCs"
	)
	default boolean showOverlay()
	{
		return true;
	}

	@ConfigItem(
			position = 1,
			keyName = "Npclist",
			name = "NPC names",
			description = "Enter name of NPCs to show"
	)
	default String npcToShow()
	{
		return "";
	}

	@ConfigItem(
			position = 2,
			keyName = "ShowTicks",
			name = "Show timer in ticks",
			description = "Shows timer in ticks instead of seconds"
	)
	default boolean showTicks() { return false; }

}
