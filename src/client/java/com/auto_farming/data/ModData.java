package com.auto_farming.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.actionwrapper.Actions;
import com.auto_farming.inventory.HotbarSlot;

import static com.auto_farming.inventory.HotbarSlot.*;
import com.auto_farming.profiles.Profile;

import static com.auto_farming.actionwrapper.Actions.WALK_LEFT;
import static com.auto_farming.actionwrapper.Actions.WALK_RIGHT;
import static com.auto_farming.actionwrapper.Actions.WALK_FORWARD;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ModData {
    private List<Profile> profiles = Arrays.asList(new Profile[] {
            new Profile("5x5 Nether Warts @ 116 (0|0)", 96000, 96000, 3500, 0, 5, new Actions[] { WALK_LEFT },
                    new Actions[] { WALK_RIGHT }, new Actions[] {}, new Actions[] {}),
            new Profile("5x4 Mushroom @ 126 (-25|0)", 93500, 97500, 3500, 0, 4,
                    new Actions[] { WALK_FORWARD, WALK_LEFT }, new Actions[] { WALK_RIGHT }, new Actions[] {},
                    new Actions[] {})
    });

    @JsonIgnore
    public boolean reload = false;

    private Profile currentProfile = null;
    private Boolean showPauseMessage = true;
    private Boolean forceAttentiveMood = false;
    private Boolean enableDistracted = false;
    private HotbarSlot farmingToolSlot = SLOT_1;
    private HotbarSlot fallbackSlot = SLOT_8;
    private Boolean autoMuteSounds = true;

    public void init() {
        AutofarmingClient.LOGGER.info("initialising moddata");
        if (currentProfile == null && profiles.size() == 0) {
            currentProfile = new Profile(Profile.EMPTY_JSON_PROFILE_STRING);
            return;
        }

        if (currentProfile == null && profiles.size() > 0) {
            currentProfile = profiles.get(0);
            return;
        }

        int goodprofileInstance = -1;

        for (int i = 0; i < profiles.size(); i++) {
            if (currentProfile.equals(profiles.get(i)))
                goodprofileInstance = i;
        }

        if (goodprofileInstance == -1 && profiles.size() == 0) {
            new Profile(Profile.EMPTY_JSON_PROFILE_STRING);
            return;
        }

        if (goodprofileInstance == -1 && profiles.size() != 0) {
            currentProfile = profiles.get(0);
            return;
        }

        currentProfile = profiles.get(goodprofileInstance);
    }

    public void set(ModData other) {
        this.profiles = Profile.cloneOf(other.profiles);
        this.reload = other.reload;

        if (other.currentProfile == null) {
            this.currentProfile = null;
        } else {
            this.currentProfile = this.profiles
                    .get(this.profiles.indexOf(other.currentProfile));
        }

        this.showPauseMessage = other.showPauseMessage;
        this.forceAttentiveMood = other.forceAttentiveMood;
        this.enableDistracted = other.enableDistracted;

        this.farmingToolSlot = other.farmingToolSlot;
        this.fallbackSlot = other.fallbackSlot;

        this.autoMuteSounds = other.autoMuteSounds;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public List<Profile> getProfiles() {
        return new ArrayList<>(profiles);
    }

    public void setCurrentProfile(Profile currentProfile) {
        currentProfile = profiles.get(profiles.indexOf(currentProfile));

        this.currentProfile = currentProfile;
    }

    public Profile getCurrentProfile() {
        if (profiles.size() == 0)
            return new Profile(Profile.EMPTY_JSON_PROFILE_STRING);

        return currentProfile;
    }

    public void setShowPauseMessage(boolean showPauseMessage) {
        this.showPauseMessage = showPauseMessage;
    }

    public boolean isShowPauseMessage() {
        return showPauseMessage;
    }

    public void setForceAttentiveMood(boolean forceAttentiveMood) {
        this.forceAttentiveMood = forceAttentiveMood;
    }

    public boolean isForceAttentiveMood() {
        return forceAttentiveMood;
    }

    public void setEnableDistracted(Boolean enableDistracted) {
        this.enableDistracted = enableDistracted;
    }

    public Boolean getEnableDistracted() {
        return enableDistracted;
    }

    public void setFarmingToolSlot(HotbarSlot farmingToolSlot) {
        this.farmingToolSlot = farmingToolSlot;
    }

    public HotbarSlot getFarmingToolSlot() {
        return farmingToolSlot;
    }

    public void setFallbackSlot(HotbarSlot fallbackSlot) {
        this.fallbackSlot = fallbackSlot;
    }

    public HotbarSlot getFallbackSlot() {
        return fallbackSlot;
    }

    public void setAutoMuteSounds(Boolean autoMuteSounds) {
        this.autoMuteSounds = autoMuteSounds;
    }

    public Boolean getAutoMuteSounds() {
        return autoMuteSounds;
    }

    public static ModData cloneOf(ModData modData) {
        ModData clonedModData = new ModData();
        clonedModData.reload = modData.reload;
        clonedModData.profiles = Profile.cloneOf(modData.profiles);

        if (modData.currentProfile == null) {
            clonedModData.currentProfile = null;
        } else {
            clonedModData.currentProfile = clonedModData.profiles
                    .get(clonedModData.profiles.indexOf(modData.currentProfile));
        }

        clonedModData.showPauseMessage = modData.showPauseMessage;
        clonedModData.forceAttentiveMood = modData.forceAttentiveMood;
        clonedModData.enableDistracted = modData.enableDistracted;

        clonedModData.farmingToolSlot = modData.farmingToolSlot;
        clonedModData.fallbackSlot = modData.fallbackSlot;

        clonedModData.autoMuteSounds = modData.autoMuteSounds;

        return clonedModData;
    }

}
