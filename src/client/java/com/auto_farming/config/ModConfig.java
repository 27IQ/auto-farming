package com.auto_farming.config;

import org.jetbrains.annotations.NotNull;

import com.auto_farming.config.clothconfigextensions.ButtonEntry;
import com.auto_farming.config.clothconfigextensions.WideStringListEntry;
import com.auto_farming.farmprofiles.Profile;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Config(name = "auto-farming")
public class ModConfig implements ConfigData {

	public static Profile currentNewProfile=new Profile();

	public static Screen build(Screen parent) {

		ModData modData=SaveDataLoader.load();

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


		general.addEntry(entryBuilder
				.startTextDescription(Text.of("Profiles:"))
				.build());

		for (Profile profile : modData.getProfiles()) {
			general.addEntry(profile.getNameLabel(entryBuilder));
			general.addEntry(profile.getSettingTextField(entryBuilder));
			general.addEntry(profile.getDeleteButton(entryBuilder,modData));
		}

		general.addEntry(entryBuilder
				.startTextDescription(Text.of("New Profile"))
				.build());

		general.addEntry(new WideStringListEntry(
				Profile.EMPTY_JSON_PROFILE_STRING, 
				()->Profile.EMPTY_JSON_PROFILE_STRING, 
				jsonString -> {
					currentNewProfile.setJsonString(jsonString);
				}));

		general.addEntry(new ButtonEntry(Text.literal("Add Profile"), (()->{modData.flagProfileForAddition(currentNewProfile);})));

		builder.setSavingRunnable(() -> {
			modData.applyProfiles();
			SaveDataLoader.save(modData);
		});

		return builder.build();
	}
}
