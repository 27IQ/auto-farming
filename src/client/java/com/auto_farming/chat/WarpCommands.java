package com.auto_farming.chat;

import net.minecraft.client.MinecraftClient;

public class WarpCommands {

    public static void warpGarden() {
        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage("/garden");
    }
}
