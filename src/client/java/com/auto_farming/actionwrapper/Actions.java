package com.auto_farming.actionwrapper;

import com.auto_farming.AutofarmingClient;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public enum Actions {
    WALK_FORWARD(), WALK_BACK(), WALK_LEFT(), WALK_RIGHT(), LEFT_CLICK(), SNEAK();

    private boolean active = false;
    private boolean stop = true;
    private KeyBinding keyMapping;
    public static boolean first = true;

    public static void initActions() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (first) {
                first = false;

                KeyMapping.client = MinecraftClient.getInstance();

                Actions.WALK_FORWARD.keyMapping = KeyMapping.getForwardKey();
                Actions.WALK_LEFT.keyMapping = KeyMapping.getLeftKey();
                Actions.WALK_BACK.keyMapping = KeyMapping.getBackKey();
                Actions.WALK_RIGHT.keyMapping = KeyMapping.getRightKey();
                Actions.LEFT_CLICK.keyMapping = KeyMapping.getAttackKey();
                Actions.SNEAK.keyMapping = KeyMapping.getSneakKey();
            }

            if (client == null || client.player == null)
                return;

            for (Actions action : Actions.values()) {
                if (action.stop) {
                    action.keyMapping.setPressed(false);
                    action.stop = false;
                } else if (action.active || action.keyMapping.isPressed()) {
                    action.keyMapping.setPressed(true);
                }
            }
        });
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
        AutofarmingClient.LOGGER.info(this.name().toLowerCase() + " activated");
    }

    public void deactivate() {
        this.active = false;
        this.stop = true;
        AutofarmingClient.LOGGER.info(this.name().toLowerCase() + " deactivated");
    }
}
