package com.auto_farming.input;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.farminglogic.AutoFarmHolder;
import com.auto_farming.misc.AutoFarmSetup;

import static com.auto_farming.actionwrapper.Direction.LEFT;
import static com.auto_farming.actionwrapper.Direction.RIGHT;
import static com.auto_farming.input.Bindings.PAUSE_TOGGLE;
import static com.auto_farming.input.Bindings.START_LEFT;
import static com.auto_farming.input.Bindings.START_RIGHT;

import static com.auto_farming.input.Bindings.AUTO_SET_UP;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class InputHandler {

	public static void register() {

		Key.register();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (START_LEFT.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed " + START_LEFT.toString());

				AutoFarmHolder.get().ifPresentOrElse((farm) -> AutoFarmHolder.removeInstance(),
						() -> AutoFarmHolder.startNewFarm(LEFT));
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (START_RIGHT.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed " + START_RIGHT.toString());

				AutoFarmHolder.get().ifPresentOrElse((farm) -> AutoFarmHolder.removeInstance(),
						() -> AutoFarmHolder.startNewFarm(RIGHT));
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (PAUSE_TOGGLE.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed " + PAUSE_TOGGLE.toString());

				AutoFarmHolder.get().ifPresent((farm) -> {
					if(farm.isForcePaused()){
						farm.nextDisrupt();
						return;
					}

					farm.pauseToggle();
				});
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (AUTO_SET_UP.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed " + AUTO_SET_UP.toString());

				Thread.ofPlatform().daemon(false).start(() -> {
					AutoFarmSetup.autoSetup();
				});
			}
		});
	}
}
