package com.auto_farming.actionwrapper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class KeyMapping {
    public static MinecraftClient client;

    public static KeyBinding getForwardKey(){
        return client.options==null?null:client.options.forwardKey;
    }

    public static KeyBinding getBackKey(){
        return client.options==null?null:client.options.backKey;
    }

    public static KeyBinding getLeftKey(){
        return client.options==null?null:client.options.leftKey;
    }

    public static KeyBinding getRightKey(){
        return client.options==null?null:client.options.rightKey;
    }

    public static KeyBinding getAttackKey(){
        return client.options==null?null:client.options.attackKey;
    }

    public static KeyBinding getSneakKey(){
        return client.options==null?null:client.options.sneakKey;
    }
}
