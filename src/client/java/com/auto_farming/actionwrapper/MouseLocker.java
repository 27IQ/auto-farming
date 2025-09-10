package com.auto_farming.actionwrapper;

import com.auto_farming.AutofarmingClient;

public class MouseLocker {
    private static boolean isMouseLocked = false;

    public static void unlockMouse() {
        if (!isMouseLocked)
            return;

        isMouseLocked = false;
        AutofarmingClient.LOGGER.info("mouse unlocked");
    }

    public static void lockMouse() {
        if (isMouseLocked)
            return;

        isMouseLocked = true;
        AutofarmingClient.LOGGER.info("mouse locked");
    }

    public static boolean isMouseLocked() {
        return isMouseLocked;
    }
}
