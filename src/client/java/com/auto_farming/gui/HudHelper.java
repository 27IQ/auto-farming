package com.auto_farming.gui;

public final class HudHelper {

    public static final void registerAllHuds() {
        StatusHUD.register();
        AlertHUD.register();
        TopStatusHUD.register();
    }
}
