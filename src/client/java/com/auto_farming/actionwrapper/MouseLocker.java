package com.auto_farming.actionwrapper;

import com.auto_farming.AutofarmingClient;

public class MouseLocker {
    private static boolean is_mouse_locked=false;

    public static void unlockMouse(){
        if(!is_mouse_locked)
            return;

        is_mouse_locked=false;
        AutofarmingClient.LOGGER.info("mouse unlocked");
    }

    public static void lockMouse(){
        if(is_mouse_locked)
           return;

        is_mouse_locked=true;
        AutofarmingClient.LOGGER.info("mouse locked");
    }

    public static boolean is_mouse_locked(){
        return is_mouse_locked;
    }
}
