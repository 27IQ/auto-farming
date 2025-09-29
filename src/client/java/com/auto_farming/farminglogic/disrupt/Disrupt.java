package com.auto_farming.farminglogic.disrupt;

import static com.auto_farming.input.Bindings.PAUSE_TOGGLE;
import static com.auto_farming.skyblock.SkyBlockItem.PEST_REPELLENT_MAX;

public enum Disrupt {
    BLOCK_BREAK_ALERT_DISRUPT(
            "You are breaking no blocks!\nResume with " + PAUSE_TOGGLE.toString()),
    NO_PEST_REPELLENT_IN_INVENTORY(
            "Please add a " + PEST_REPELLENT_MAX.NAME + " to your inventory and resume with " + PAUSE_TOGGLE.toString()
                    + "!"),
    PEST_LIMIT_REACHED_DISRUPT(
            "Pest limit reached!\nClear the Pests and resume with " + PAUSE_TOGGLE.toString());

    private final String message;
    public static final long COOLDOWN = 5000;
    private static long lastSuccess = 0;

    private Disrupt(String messge) {
        this.message = messge;
    }

    public String getMessage() {
        return message;
    }

    public long getLastsuccess() {
        return lastSuccess;
    }

    public void setLastSuccess(long lastSuccess) {
        Disrupt.lastSuccess = lastSuccess;
    }
}
