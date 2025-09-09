package com.auto_farming.actionwrapper;

import com.auto_farming.AutofarmingClient;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public enum Actions {
    WALK_FORWARD, WALK_BACK, WALK_LEFT, WALK_RIGHT, LEFT_CLICK;

    private static boolean active=false;

    private void setActive(boolean active){
        Actions.active=active;
    }

    public boolean isActive(){
        return active;
    }

    public void activate(){
        switch (this) {
            case WALK_FORWARD:
                ClientTickEvents.END_CLIENT_TICK.register(client->{
                    if(client.player==null)
                        return;

                        client.options.forwardKey.setPressed(true);
                        AutofarmingClient.LOGGER.info("forwardkey activated");

                    WALK_FORWARD.setActive(true);
                });
            break;

            case WALK_BACK:
                ClientTickEvents.END_CLIENT_TICK.register(client->{
                    if(client.player==null)
                        return;

                        client.options.backKey.setPressed(true);
                        AutofarmingClient.LOGGER.info("backkey activated");

                    WALK_BACK.setActive(true);
                });
            break;

            case WALK_LEFT:
                ClientTickEvents.END_CLIENT_TICK.register(client->{
                    if(client.player==null)
                        return;

                    client.options.leftKey.setPressed(true);
                    AutofarmingClient.LOGGER.info("leftkey activated");

                    WALK_LEFT.setActive(true);
                });
            break;

            case WALK_RIGHT:
                ClientTickEvents.END_CLIENT_TICK.register(client->{
                    if(client.player==null)
                        return;

                    client.options.rightKey.setPressed(true);
                    AutofarmingClient.LOGGER.info("rightkey activated");

                    WALK_RIGHT.setActive(true);
                });
            break;

             case LEFT_CLICK:
                ClientTickEvents.END_CLIENT_TICK.register(client->{
                    if(client.player==null)
                        return;

                    client.options.attackKey.setPressed(true);
                    AutofarmingClient.LOGGER.info("leftclick activated");

                    LEFT_CLICK.setActive(true);
                });
            break;
        }
    }

    public void deactivate(){
        switch (this) {
            case WALK_FORWARD:
                ClientTickEvents.END_CLIENT_TICK.register(client->{
                    if(client.player==null)
                        return;

                    client.options.forwardKey.setPressed(false);
                    AutofarmingClient.LOGGER.info("forwardkey deactivated");

                    WALK_FORWARD.setActive(false);
                });
                break;

            case WALK_BACK:
                ClientTickEvents.END_CLIENT_TICK.register(client->{
                    if(client.player==null)
                        return;

                    client.options.backKey.setPressed(false);
                    AutofarmingClient.LOGGER.info("backkey deactivated");

                    WALK_FORWARD.setActive(false);
                });
                break;

            case WALK_LEFT:
                ClientTickEvents.END_CLIENT_TICK.register(client->{
                    if(client.player==null)
                        return;

                    client.options.leftKey.setPressed(false);
                    AutofarmingClient.LOGGER.info("leftkey deactivated");

                    WALK_FORWARD.setActive(false);
                });
                break;

            case WALK_RIGHT:
                ClientTickEvents.END_CLIENT_TICK.register(client->{
                    if(client.player==null)
                        return;

                    client.options.rightKey.setPressed(false);
                    AutofarmingClient.LOGGER.info("rightkey deactivated");

                    WALK_FORWARD.setActive(false);
                });
                break;

             case LEFT_CLICK:
                ClientTickEvents.END_CLIENT_TICK.register(client->{
                    if(client.player==null)
                        return;

                    client.options.attackKey.setPressed(false);
                    AutofarmingClient.LOGGER.info("leftclick deactivated");

                    WALK_FORWARD.setActive(false);
                });
            break;
        }
    }
}
