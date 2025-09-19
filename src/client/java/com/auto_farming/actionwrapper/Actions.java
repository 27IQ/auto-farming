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

        MinecraftClient cl = MinecraftClient.getInstance();

        Actions.WALK_FORWARD.keyMapping = cl.options.forwardKey;
        Actions.WALK_LEFT.keyMapping = cl.options.leftKey;
        Actions.WALK_BACK.keyMapping = cl.options.backKey;
        Actions.WALK_RIGHT.keyMapping = cl.options.rightKey;
        Actions.LEFT_CLICK.keyMapping = cl.options.attackKey;
        Actions.SNEAK.keyMapping = cl.options.sneakKey;

        ClientTickEvents.START_CLIENT_TICK.register(client -> {

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

    public static Actions[] cloneOf(Actions[] actions) {
        if (actions == null)
            return new Actions[0];

        return actions.clone();
    }

    public static boolean checkActionsEqual(Actions[] actions, Actions[] otherActions) {
        if (actions.length != otherActions.length)
            return false;

        for (int i = 0; i < actions.length; i++) {
            if (actions[i] != otherActions[i])
                return false;
        }

        return true;
    }
}
