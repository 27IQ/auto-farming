package com.auto_farming.gui;

import com.auto_farming.AutofarmingClient;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public final class StatusHUD {

    private static String message = "";

    public static void setMessage(String hudMessage) {
        StatusHUD.message = hudMessage;
    }

    public static void register() {

        HudLayerRegistrationCallback.EVENT
                .register(layeredDrawer -> layeredDrawer.attachLayerAfter(IdentifiedLayer.CROSSHAIR,
                        Identifier.of(AutofarmingClient.MOD_ID, "status_hud"), (context, tickCounter) -> {
                            StatusHUD.render(context, tickCounter);
                        }));
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null)
            return;

        String[] lines = message.split("\n");

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int totalHeight = lines.length * client.textRenderer.fontHeight;
        int startY = (screenHeight / 2) - (totalHeight / 2);

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int textWidth = client.textRenderer.getWidth(line);
            int x = (screenWidth - textWidth) / 2;
            int y = startY + (i * client.textRenderer.fontHeight);

            context.drawTextWithShadow(client.textRenderer, line, x, y, 0xFFFFFF);
        }
    }
}
