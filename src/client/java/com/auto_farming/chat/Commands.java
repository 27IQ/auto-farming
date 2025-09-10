package com.auto_farming.chat;

import java.util.LinkedList;
import java.util.Queue;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class Commands {

    private static Queue<String> sendQueue= new LinkedList<String>();
    private static final long MESSAGE_DELAY=300;
    private static long lastMessageSent= System.currentTimeMillis();

    public static void registerCommandQueue(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            onTick();
        });
    }

    private static void onTick(){
        if (!sendQueue.isEmpty()&&(System.currentTimeMillis()-lastMessageSent) > MESSAGE_DELAY) {
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(sendQueue.remove());
            lastMessageSent = System.currentTimeMillis();
        }
    }

    public static void warpGarden() {
        sendMessageToServer("/garden");
    }

    private static void sendMessageToServer(String message) {
        if (canSendInstantly()) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.networkHandler.sendChatMessage(message);
                lastMessageSent = System.currentTimeMillis();
                return;
            }
        }
        sendQueue.add(message);
    }

    private static boolean canSendInstantly(){
        return sendQueue.isEmpty() && (System.currentTimeMillis()-lastMessageSent) > MESSAGE_DELAY;
    } 
}
