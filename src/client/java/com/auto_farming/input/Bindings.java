package com.auto_farming.input;

import net.minecraft.client.option.KeyBinding;

public enum Bindings {

    START_LEFT(Key.start_left), START_RIGHT(Key.start_right), PAUSE_TOGGLE(Key.pause_toggle);

    public final KeyBinding bind;

    private Bindings(KeyBinding bind){
        this.bind=bind;
    }

    @Override
    public String toString() {

        String bind_string=PAUSE_TOGGLE.bind.getBoundKeyTranslationKey().toString();
        String[] bind_arr=bind_string.split("\\.");
        return bind_arr[bind_arr.length-1].toUpperCase();
    }
}
