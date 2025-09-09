package com.auto_farming.actionwrapper;

import com.auto_farming.AutofarmingClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public enum Actions {
    WALK_FORWARD, WALK_BACK, WALK_LEFT, WALK_RIGHT, LEFT_CLICK;

    private boolean active = false;

    static {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client == null || client.player == null) return;

            client.options.forwardKey.setPressed(false);
            client.options.backKey.setPressed(false);
            client.options.leftKey.setPressed(false);
            client.options.rightKey.setPressed(false);
            client.options.attackKey.setPressed(false);

            if (WALK_FORWARD.active) client.options.forwardKey.setPressed(true);
            if (WALK_BACK.active)    client.options.backKey.setPressed(true);
            if (WALK_LEFT.active)    client.options.leftKey.setPressed(true);
            if (WALK_RIGHT.active)   client.options.rightKey.setPressed(true);
            if (LEFT_CLICK.active)   client.options.attackKey.setPressed(true);
        });
    }

    public boolean isActive() { return active; }

    public void activate() {
        this.active = true;
        AutofarmingClient.LOGGER.info(this.name().toLowerCase() + " activated");
    }

    public void deactivate() {
        this.active = false;
        AutofarmingClient.LOGGER.info(this.name().toLowerCase() + " deactivated");
    }
}
