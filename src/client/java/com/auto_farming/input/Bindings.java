package com.auto_farming.input;

import net.minecraft.client.option.KeyBinding;

public enum Bindings {

    START_LEFT(Key.startLeft), START_RIGHT(Key.startRight), PAUSE_TOGGLE(Key.pauseToggle), AUTO_SET_UP(Key.autoSetup);

    public final KeyBinding bind;

    private Bindings(KeyBinding bind) {
        this.bind = bind;
    }

    @Override
    public String toString() {

        String bindString = bind.getBoundKeyTranslationKey().toString();
        String[] bindArr = bindString.split("\\.");
        return bindArr[bindArr.length - 1].toUpperCase();
    }
}
