package com.auto_farming.input;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.actionwrapper.Directions;

import static com.auto_farming.actionwrapper.Directions.LEFT;
import static com.auto_farming.actionwrapper.Directions.RIGHT;
import static com.auto_farming.input.Bindings.PAUSE_TOGGLE;
import static com.auto_farming.input.Bindings.START_LEFT;
import static com.auto_farming.input.Bindings.START_RIGHT;
import static com.auto_farming.input.Bindings.AUTO_SET_UP;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class InputHandler {

	public static Thread farmThread = null;

	public static void registerKeybinds() {

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (START_LEFT.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed " + START_LEFT.toString());
				taskHelper(LEFT);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (START_RIGHT.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed " + START_RIGHT.toString());
				taskHelper(RIGHT);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (PAUSE_TOGGLE.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed " + PAUSE_TOGGLE.toString());
				AutofarmingClient.autoFarm.pause_toggle();
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (AUTO_SET_UP.bind.wasPressed()) {
				AutofarmingClient.LOGGER.info("pressed " + AUTO_SET_UP.toString());
				Runnable setupTask = () -> {
					AutofarmingClient.autoFarm.autoSetUp();
				};

				Thread setupThread = new Thread(setupTask);
				setupThread.setDaemon(false);
				setupThread.start();
			}
		});
	}

	private static void taskHelper(Directions direction) {

		if (farmThread != null) {
			AutofarmingClient.autoFarm.isActive = false;
			farmThread = null;
			return;
		}

		Runnable startTask = () -> {
			AutofarmingClient.getNewAutofarmInstance().runFarm(direction);
		};

		farmThread = new Thread(startTask);
		farmThread.setDaemon(false);
		farmThread.start();
	}
}
