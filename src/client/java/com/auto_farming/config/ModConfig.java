package com.auto_farming.config;

import static com.auto_farming.AutofarmingClient.modData;

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
				.startEnumSelector(Text.of("Profile"), Profile.class, modData.getCurrentProfile())
				.setDefaultValue(Profile.NETHERWART)
				.setEnumNameProvider((profile) -> {
					return Text.of(profile.toString());
				})
				.setSaveConsumer(newValue -> modData.setCurrentProfile(newValue))
				.build());

		general.addEntry(entryBuilder
				.startTextDescription(Text.of("Current profile" + modData.getCurrentProfile().NAME))
				.build());

		general.addEntry(entryBuilder
				.startBooleanToggle(Text.of("Show Pause Message"), modData.showPauseMessage())
				.setDefaultValue(true)
				.setSaveConsumer(newValue -> modData.setShowPauseMessage(newValue))
				.build());

		general.addEntry(entryBuilder
				.startBooleanToggle(Text.of("Force Attentive Mood"), modData.forceAttentiveMood())
				.setDefaultValue(false)
				.setSaveConsumer(newValue -> modData.setForceAttentiveMood(newValue))
				.build());

		return builder.build();
	}
}
