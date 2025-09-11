package com.auto_farming.config;

import static com.auto_farming.gui.Alert.setAlertMessage;

import java.util.Arrays;
import java.util.List;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.actionwrapper.Actions;

import static com.auto_farming.actionwrapper.Actions.WALK_LEFT;
import static com.auto_farming.actionwrapper.Actions.WALK_RIGHT;
import static com.auto_farming.actionwrapper.Actions.WALK_FORWARD;
import com.auto_farming.farmprofiles.Profile;

public class ModData {
    private List<Profile> profiles = Arrays.asList(new Profile[] {
            new Profile("5x5 Nether Warts @ 116 (0|0)", 96000, 96000, 3500, 0, 5, new Actions[] { WALK_LEFT },
                    new Actions[] { WALK_RIGHT }, new Actions[] {}),
            new Profile("5x4 Mushroom @ 126 (-25|0)", 92000, 97000, 3500, 0, 4,
                    new Actions[] { WALK_FORWARD, WALK_LEFT }, new Actions[] { WALK_RIGHT }, new Actions[] {})
    });
    private Profile currentProfile = profiles.get(0);
    private Boolean showPauseMessage = true;
    private Boolean forceAttentiveMood = false;

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setCurrentProfile(Profile current_profile) {
        if (AutofarmingClient.autoFarm != null && AutofarmingClient.autoFarm.isActive) {
            setAlertMessage("please deactivate the running profile first");
            return;
        }

        this.currentProfile = current_profile;
    }

    public Profile getCurrentProfile() {
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
}
