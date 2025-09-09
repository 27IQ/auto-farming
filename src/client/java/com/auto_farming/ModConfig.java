package com.auto_farming;

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
                .startEnumSelector(Text.of("Profile"), Profile.class, AutoFarm.getCurrent_profile())
                .setDefaultValue(Profile.NETHERWART)
                .setEnumNameProvider((profile)->{return Text.of(profile.toString());})
                .setSaveConsumer(newValue -> AutoFarm.setCurrent_profile(newValue))
                .build());

        general.addEntry(entryBuilder
                .startTextDescription(Text.of("Current profile"+AutoFarm.getCurrent_profile().name))
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
