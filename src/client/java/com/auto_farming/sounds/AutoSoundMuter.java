package com.auto_farming.sounds;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.auto_farming.farminglogic.AutoFarmHolder;

public class AutoSoundMuter {

    private static final Set<String> soundEventSet = new HashSet<>();

    static {
        for (SoundAlert alert : SoundAlert.values()) {
            soundEventSet.add(alert.getIdentifier().toString());
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
