package com.abyssaldrip;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("exporter")
public interface ClanDataExporterConfig extends Config
{
	@ConfigItem(
		keyName = "File name",
		name = "File name",
		description = "Select an output file"
	)

	default String greeting()
	{
		return "";
	}
	@ConfigItem(
			keyName = "checkbox",
			name = "Add current date",
			description = "Adds current date to file name"
	)
	default  boolean checkbox(){return false;}
}
