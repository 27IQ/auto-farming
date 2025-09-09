package com.auto_farming.chat;

import net.minecraft.client.MinecraftClient;

public class WarpCommands {

    public static void warp_garden(){
        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage("/garden");
    }
}
