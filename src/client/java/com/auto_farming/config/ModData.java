package com.auto_farming.config;

import static com.auto_farming.gui.Alert.setAlertMessage;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.farmprofiles.Profile;

public class ModData {
    private Profile currentProfile = Profile.NETHERWART;
    private boolean showPauseMessage = true;
    private boolean forceAttentiveMood = false;

    public void setCurrentProfile(Profile current_profile) {
        if (AutofarmingClient.autoFarm != null && AutofarmingClient.autoFarm.isActive) {
            setAlertMessage("please deactivate the running profile first");
            return;
        }

        this.currentProfile = current_profile;
        SaveDataLoader.save(this);
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public void setShowPauseMessage(boolean showPauseMessage) {
        this.showPauseMessage = showPauseMessage;
        SaveDataLoader.save(this);
    }

    public boolean showPauseMessage() {
        return showPauseMessage;
    }

    public void setForceAttentiveMood(boolean forceAttentiveMood) {
        this.forceAttentiveMood = forceAttentiveMood;
        SaveDataLoader.save(this);
    }

    public boolean forceAttentiveMood() {
        return forceAttentiveMood;
    }
}
