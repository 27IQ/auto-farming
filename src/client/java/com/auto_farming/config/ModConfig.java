package com.auto_farming.config;

import static com.auto_farming.AutofarmingClient.modData;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.farmprofiles.Profile;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Config(name = "auto-farming")
public class ModConfig implements ConfigData {

	public static Screen build(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.of("Macro Settings"));

		ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		general.addEntry(entryBuilder
				.startSelector(Text.of("Profile"), modData.getProfiles().toArray(new Profile[0]),
						modData.getCurrentProfile())
				.setDefaultValue(modData.getCurrentProfile())
				.setSaveConsumer(modData::setCurrentProfile)
				.build());

		general.addEntry(entryBuilder
				.startTextDescription(Text.of("Current profile" + modData.getCurrentProfile().name))
				.build());

		general.addEntry(entryBuilder
				.startBooleanToggle(Text.of("Show Pause Message"), modData.isShowPauseMessage())
				.setDefaultValue(true)
				.setSaveConsumer(modData::setShowPauseMessage)
				.build());

		general.addEntry(entryBuilder
				.startBooleanToggle(Text.of("Force Attentive Mood"), modData.isForceAttentiveMood())
				.setDefaultValue(false)
				.setSaveConsumer(modData::setForceAttentiveMood)
				.build());

		builder.setSavingRunnable(() -> {
			SaveDataLoader.save(AutofarmingClient.modData);
		});

		return builder.build();
	}
}
