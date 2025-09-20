package com.auto_farming.gui;

import com.auto_farming.AutofarmingClient;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public final class AlertHUD {

    private static String alertMessage = "";

    public static void setMessage(String alertMessage, long durationMillis) {
        AlertHUD.alertMessage = alertMessage;

        Thread.ofPlatform().start(() -> {
            try {
                Thread.sleep(durationMillis);
            } catch (InterruptedException e) {
                AutofarmingClient.LOGGER.error(e.getMessage(), e);
            }
            AlertHUD.alertMessage = "";
        });

        AutofarmingClient.LOGGER.info("alert: " + alertMessage);
    }

    public static void register() {
        HudLayerRegistrationCallback.EVENT
                .register(layeredDrawer -> layeredDrawer.attachLayerAfter(IdentifiedLayer.CROSSHAIR,
                        Identifier.of(AutofarmingClient.MOD_ID, "alert_hud"), (context, tickCounter) -> {
                            render(context, tickCounter);
                        }));
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null)
            return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int textWidth = client.textRenderer.getWidth(alertMessage);

        int x = (screenWidth - textWidth) / 2;
        int y = (screenHeight / 2) - (client.textRenderer.fontHeight / 2);

        context.drawTextWithShadow(client.textRenderer, alertMessage, x, y, 0xFF0000);
    }
}
