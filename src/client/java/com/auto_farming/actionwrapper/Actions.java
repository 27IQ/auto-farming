package com.auto_farming.actionwrapper;

import static com.auto_farming.misc.RandNumberHelper.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public enum Actions {
    WALK_FORWARD(), WALK_BACK(), WALK_LEFT(), WALK_RIGHT(), LEFT_CLICK(), SNEAK(), RIGHT_CLICK();

    private boolean active = false;
    private boolean stop = true;
    private Supplier<KeyBinding> keyMapping;
    public static boolean first = true;

    public static void register() {

        Actions.WALK_FORWARD.keyMapping = () -> MinecraftClient.getInstance().options.forwardKey;
        Actions.WALK_LEFT.keyMapping = () -> MinecraftClient.getInstance().options.leftKey;
        Actions.WALK_BACK.keyMapping = () -> MinecraftClient.getInstance().options.backKey;
        Actions.WALK_RIGHT.keyMapping = () -> MinecraftClient.getInstance().options.rightKey;
        Actions.LEFT_CLICK.keyMapping = () -> MinecraftClient.getInstance().options.attackKey;
        Actions.SNEAK.keyMapping = () -> MinecraftClient.getInstance().options.sneakKey;
        Actions.RIGHT_CLICK.keyMapping = () -> MinecraftClient.getInstance().options.useKey;

        ClientTickEvents.START_CLIENT_TICK.register(client -> {

            if (client == null || client.player == null)
                return;

            for (Actions action : Actions.values()) {
                if (action.stop) {
                    action.keyMapping.get().setPressed(false);
                    action.stop = false;
                } else if (action.active || action.keyMapping.get().isPressed()) {
                    action.keyMapping.get().setPressed(true);
                }
            }
        });
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
        // AutofarmingClient.LOGGER.info(this.name().toLowerCase() + " activated");
    }

    public void deactivate() {
        this.active = false;
        this.stop = true;
        // AutofarmingClient.LOGGER.info(this.name().toLowerCase() + " deactivated");
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

    public static Actions[] randomiseActionOrder(Actions[] actions) {

        int min = 0;
        int max = actions.length - 1;

        List<Actions> results = new ArrayList<>();

        while (results.size() < actions.length) {
            int pull = Random(min, max);

            if (results.size() == 0) {
                results.add(actions[pull]);
                continue;
            }

            boolean foundFlag = false;

            for (Actions action : results) {
                if (action == actions[pull])
                    foundFlag = true;
            }

            if (!foundFlag)
                results.add(actions[pull]);
        }

        return results.toArray(new Actions[0]);
    }
}
