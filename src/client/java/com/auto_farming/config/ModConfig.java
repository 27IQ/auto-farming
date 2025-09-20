package com.auto_farming.config;

import java.util.List;

import com.auto_farming.config.clothconfigextensions.ButtonEntry;
import com.auto_farming.config.clothconfigextensions.DirtyFlag;
import com.auto_farming.data.ModData;
import com.auto_farming.data.ModDataHolder;
import com.auto_farming.data.SaveDataLoader;
import com.auto_farming.farmprofiles.Profile;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Config(name = "auto-farming")
public class ModConfig implements ConfigData {

	public static Profile currentNewProfile = new Profile();
	public static ModData modData = ModData.cloneOf(ModDataHolder.DATA);
	private static boolean hasReloaded = false;

	public static void register() {
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
	}

	public static Screen build(Screen parent) {

		if (!modData.reload) {
			modData = ModData.cloneOf(ModDataHolder.DATA);
			hasReloaded = false;
		} else {
			modData.reload = false;
			hasReloaded = true;
		}

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
				.startBooleanToggle(Text.of("Enable Distracted Mood"), modData.getEnableDistracted())
				.setDefaultValue(false)
				.setSaveConsumer(modData::setEnableDistracted)
				.build());

		general.addEntry(entryBuilder
				.startTextDescription(Text.of("Profiles:"))
				.build());

		for (Profile profile : modData.getProfiles()) {
			general.addEntry(profile.getNameLabel(entryBuilder));
			general.addEntry(profile.getSettingTextField(entryBuilder));
			general.addEntry(profile.getDeleteButton(entryBuilder, modData, parent));
		}

		general.addEntry(entryBuilder
				.startTextDescription(Text.of("New Profile"))
				.build());

		general.addEntry(new ButtonEntry(Text.literal("Add Profile"), (() -> {
			List<Profile> profiles = modData.getProfiles();
			profiles.add(new Profile(Profile.EMPTY_JSON_PROFILE_STRING));
			modData.setProfiles(profiles);
			reload(parent);
		})));

		general.addEntry(new DirtyFlag(hasReloaded));

		builder.setSavingRunnable(() -> {
			if (!modData.reload)
				SaveDataLoader.save(modData);
		});

		return builder.build();
	}

	public static void reload(Screen parent) {
		modData.reload = true;
		MinecraftClient.getInstance().setScreen(build(parent));
	}
}
