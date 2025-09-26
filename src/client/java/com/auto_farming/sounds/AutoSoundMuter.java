package com.auto_farming.sounds;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.auto_farming.farminglogic.AutoFarmHolder;

public class AutoSoundMuter {

    public static final String[] MUTED_SOUNDS = new String[] {
            "block.crop.break",
            "block.crop.hit",
            "block.nether_wart.break",
            "block.nether_wart.hit",
            "block.wood.break",
            "block.wood.hit",
            "block.cactus.break",
            "block.cactus.hit",
            "block.sugar_cane.break",
            "block.sugar_cane.hit",
            "block.fungus.break",
            "block.fungus.hit",
            "block.melon.break",
            "block.melon.hit",
            "block.pumpkin.break",
            "block.pumpkin.hit",
            "entity.experience_orb.pickup",
            "block.wood.break"
    };

    private static final Set<String> soundEventSet = new HashSet<>();

    static {
        for (String event : MUTED_SOUNDS) {
            soundEventSet.add("minecraft:" + event);
        }
    }

    public static final Set<String> IDENTIFIER_SET = Collections.unmodifiableSet(soundEventSet);

    private static boolean muteSounds = false;

    public static void activate() {
        AutoFarmHolder.get().ifPresent((farm) -> {
            if (farm.getAutoMuteSounds()) {
                muteSounds = true;
            }
        });
    }

    public static void deactivate() {
        muteSounds = false;
    }

    public static boolean isSoundMuted() {
        return muteSounds;
    }
}
