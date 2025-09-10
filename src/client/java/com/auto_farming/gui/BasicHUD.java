package com.auto_farming.gui;

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
            if (client.player == null)
                return;

            String[] lines = hudMessage.split("\n");

            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            int totalHeight = lines.length * client.textRenderer.fontHeight;
            int startY = (screenHeight / 2) - (totalHeight / 2);

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                int textWidth = client.textRenderer.getWidth(line);
                int x = (screenWidth - textWidth) / 2;
                int y = startY + (i * client.textRenderer.fontHeight);

                drawContext.drawTextWithShadow(client.textRenderer, line, x, y, 0xFFFFFF);
            }
        });
    }

}
