package com.auto_farming.input;

import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class Key {
    private Key() {
    }

    public static final int START_LEFT_KEY = GLFW.GLFW_KEY_F6;
    public static final int START_RIGHT_KEY = GLFW.GLFW_KEY_F7;
    public static final int PAUSE_KEY = GLFW.GLFW_KEY_F9;
    public static final int AUTO_SETUP_KEY = GLFW.GLFW_KEY_F4;

    public static final String KEY_CAT = "Auto Farming";
    public static final String START_LEFT_ID = "Start Left Direction";
    public static final String START_RIGHT_ID = "Start Right Direction";
    public static final String PAUSE_TOGGLE_ID = "Pause Toggle";
    public static final String AUTO_SETUP_ID = "Auto Setup";

    public static KeyBinding startLeft;
    public static KeyBinding startRight;
    public static KeyBinding pauseToggle;
    public static KeyBinding autoSetup;

    public static void register() {
        startLeft = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                START_LEFT_ID, InputUtil.Type.KEYSYM, START_LEFT_KEY, KEY_CAT));
        startRight = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                START_RIGHT_ID, InputUtil.Type.KEYSYM, START_RIGHT_KEY, KEY_CAT));
        pauseToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                PAUSE_TOGGLE_ID, InputUtil.Type.KEYSYM, PAUSE_KEY, KEY_CAT));
        autoSetup = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                AUTO_SETUP_ID, InputUtil.Type.KEYSYM, AUTO_SETUP_KEY, KEY_CAT));
    }
}
