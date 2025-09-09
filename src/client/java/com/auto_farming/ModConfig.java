package com.auto_farming;

import com.auto_farming.farmprofiles.Profile;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModConfig {
    public static Screen build(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent) // now parent is properly defined
                .setTitle(Text.of("Macro Settings"));

        ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder
                .startEnumSelector(Text.of("Profile"), Profile.class, AutoFarm.current_profile)
                .setDefaultValue(Profile.NETHERWART)
                .setSaveConsumer(newValue -> AutoFarm.current_profile = newValue)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.of("Show Pause Message"), AutoFarm.show_pause_Message)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> AutoFarm.show_pause_Message = newValue)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.of("Force Attentive Mood"), AutoFarm.force_attentive_mood)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> AutoFarm.force_attentive_mood = newValue)
                .build());

        return builder.build();
    }
}
