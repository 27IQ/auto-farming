package com.auto_farming.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class Alert {

    private static String alertMessage = "";

    public static void setAlertMessage(String alertMessage) {
        Alert.alertMessage = alertMessage;
        Thread stopThread=new Thread(()->{
            try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            Alert.alertMessage="";
        });

        stopThread.start();
    }

    @SuppressWarnings("deprecation")
    public static void registerHUD() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();
            int textWidth = client.textRenderer.getWidth(alertMessage);

            int x = (screenWidth - textWidth) / 2;
            int y = (screenHeight / 2) - (client.textRenderer.fontHeight / 2);

            drawContext.drawTextWithShadow(client.textRenderer, alertMessage, x, y, 0xFF0000);
        });
    }
}
