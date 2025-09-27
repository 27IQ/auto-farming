package com.auto_farming.sounds;

import com.auto_farming.AutofarmingClient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public enum SoundAlert {
    MAMBO_ALERT(Identifier.of("auto_farming", "mambo"));

    Identifier identifier;
    SoundEvent soundEvent;

    private SoundAlert(Identifier identifier) {
        this.identifier = identifier;
        this.soundEvent = SoundEvent.of(this.identifier);
    }

    public void register() {
        Registry.register(Registries.SOUND_EVENT, identifier, soundEvent);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void play() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.getSoundManager().play(
                PositionedSoundInstance.master(soundEvent, 1.0F));
        AutofarmingClient.LOGGER.info("playing " + this.name());
    }

    public void stop() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.getSoundManager().stopSounds(identifier, SoundCategory.MASTER);
        AutofarmingClient.LOGGER.info("stopping " + this.name());
    }

    public static void registerAll() {
        for (SoundAlert alert : SoundAlert.values()) {
            alert.register();
        }
    }
}
