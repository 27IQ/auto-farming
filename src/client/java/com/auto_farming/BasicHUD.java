package com.auto_farming;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class BasicHUD {

    private static String hudMessage = "";

    public static void setHudMessage(String hudMessage) {
        BasicHUD.hudMessage = hudMessage;
    }

    @SuppressWarnings("deprecation")
    public static void registerHUD() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();
            int textWidth = client.textRenderer.getWidth(hudMessage);

            int x = (screenWidth - textWidth) / 2;
            int y = (screenHeight / 2) - (client.textRenderer.fontHeight / 2);

            drawContext.drawTextWithShadow(client.textRenderer, hudMessage, x, y, 0xFFFFFF);
        });
    }
}

